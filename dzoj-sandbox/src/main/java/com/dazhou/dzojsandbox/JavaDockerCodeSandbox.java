package com.dazhou.dzojsandbox;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.dazhou.dzojsandbox.model.ExecuteCodeRepose;
import com.dazhou.dzojsandbox.model.ExecuteCodeRequest;
import com.dazhou.dzojsandbox.model.ExecuteMessage;
import com.dazhou.dzojsandbox.model.JudgeInfo;
import com.dazhou.dzojsandbox.util.ProcessUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author dazhou
 * @title  docker实现沙箱代码
 * @create 2023-08-18 18:14
 */
@Component
public class JavaDockerCodeSandbox implements CodeSandbox {
    public static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    public static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    public static final Boolean FIRST_INIT = true;

    public static final Long TIME_OUT = 5000L;


    @Override
    public ExecuteCodeRepose
    executeCode(ExecuteCodeRequest executeCodeRequest) {
        if (executeCodeRequest==null){
            return new ExecuteCodeRepose();
        }
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        //1.获取到传入的代码，写到文件中
        //获取当前的工作目录D:\OJ\dzoj-sandbox
        String userDir = System.getProperty("user.dir");
        //设置路径  File.separator 用来标识/
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        //判断全局路径是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        //把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        //把用户的代码写入这个用户目录下的Main.java文件中
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        //2.编译代码，得到Class文件
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        //java可以使用这个来执行 命令行的命令 需要出来报错
        try {
            // 执行编译命令
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            //获取编译时，cmd中输出的信息
            ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);

        } catch (IOException e) {
            return getErrorResponse(e);
        }
        //创建Docker连接
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        //3.创建容器把文件放到docker中
        //拉取镜像
        String imgage = "openjdk:8-alpine";
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(imgage);
            //回调函数 查询输出的信息
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像:" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();  //阻塞 直到下载完成才进行下一步

            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }

        System.out.println("下载完成");

        //创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(imgage);
        HostConfig hostConfig = new HostConfig();
        //设置内存
        hostConfig.withMemory(100 * 1000 * 1000L);
        //设置交换区的大小，
        hostConfig.withMemorySwap(0L);
        //设置cpu
        hostConfig.withCpuCount(1L);
//        /dzoj-sandbox/src/main/java/com/dazhou/dzojsandbox/profile.json
//        String profileConfig = ResourceUtil.readUtf8Str(userDir+ File.separator+
//                "src"+File.separator+
//                "main"+File.separator+
//                "java"+File.separator+
//                "com"+File.separator+
//                "dazhou"+ File.separator+
//                "dzojsandbox"+ File.separator+"profile.json");
        //允许你限制进程可以执行的系统调用
//        hostConfig.withSecurityOpts(Arrays.asList("seccomp=" + profileConfig));
        //容器挂载目录
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                //禁用网络
                .withNetworkDisabled(true)
                //禁止在root目录写文件
                .withReadonlyRootfs(true)
                //开启交互的容器
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();


        //启动容器
        dockerClient.startContainerCmd(containerId).exec();

        //docker exec 容器name java -cp /app Main
        //进入容器执行命令
        //执行命令获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            //计时
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    //执行运行命令
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("创建执行命令：" + execCreateCmdResponse);
            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            //判断是否超时,默认就是超时
            final boolean[] timeout = {true};
            String execId = execCreateCmdResponse.getId();
            //回调函数
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                //正常执行完后会执行这个方法
                @Override
                public void onComplete() {
                    //如果执行完成，则表示没超时
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    //并且通过 StreamType 来区分标准输出和错误输出。
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("输出错误结果" + errorMessage[0]);
                    } else {
                        message[0] = new String(frame.getPayload());
                        System.out.println("输出结果" + message[0]);
                    }
                    super.onNext(frame);
                }
            };
            //获取占用内存
            final long[] maxMemory = {0L};
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    Long usageMemory = statistics.getMemoryStats().getUsage();
                    System.out.println("内存占用" +usageMemory);
                    if (usageMemory!=null){
                        maxMemory[0] = Math.max(usageMemory, maxMemory[0]);

                    }
                }

                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });
            //启动监控 监控内存等信息
            statsCmd.exec(statisticsResultCallback);
            statsCmd.close();
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);  //设置超时时间
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            } catch (InterruptedException e) {
                System.out.println("执行异常");
                throw new RuntimeException(e);
            }

            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
        }

        //删除容器
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
        System.out.println("删除成功");
        //封装结果
        ExecuteCodeRepose executeCodeRepose = new ExecuteCodeRepose();
        List<String> outputList = new ArrayList<>();
        //取用时最大值，便于判断是否超时
        long maxTime = 0L;
        //最大内存
        long maxMemory = 0L;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                //错误执行信息
                executeCodeRepose.setMessage(errorMessage);
                //执行中存在错误
                executeCodeRepose.setStatus(3);
                break;
            }
            //正确的成功信息
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            Long memory = executeMessage.getMemory();
            if (time != null) {
                //取出来的时间进行比较 取最大的那个
                maxTime = Math.max(maxTime, time);
            }
            if (memory != null) {
                maxMemory = Math.max(maxMemory, memory);
            }
        }
        //正常运行完成
        if (outputList.size() == executeMessageList.size()) {
            executeCodeRepose.setStatus(1);
        }
        executeCodeRepose.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
//        Message在判题服务中填写
        judgeInfo.setMemory(maxMemory);
        judgeInfo.setTime(maxTime);
        executeCodeRepose.setJudgeInfo(judgeInfo);
        //5.文件清理
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
        }
        return executeCodeRepose;


    }

    /**
     * 获取错误响应
     *
     * @param e
     * @return
     */
    private ExecuteCodeRepose getErrorResponse(Throwable e) {
        ExecuteCodeRepose executeCodeResponse = new ExecuteCodeRepose();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}

