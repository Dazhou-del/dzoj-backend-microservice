package com.dazhou.dzojsandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dazhou.dzojsandbox.model.ExecuteCodeRepose;
import com.dazhou.dzojsandbox.model.ExecuteCodeRequest;
import com.dazhou.dzojsandbox.model.ExecuteMessage;
import com.dazhou.dzojsandbox.model.JudgeInfo;
import com.dazhou.dzojsandbox.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author dazhou
 * @title java沙箱代码模板实现
 * @create 2023-08-30 15:07
 */
@Slf4j
public abstract class JavaCodeSandboxTemplate implements  CodeSandbox{
    public static final String GLOBAL_CODE_DIR_NAME="tmpCode";

    public static final String GLOBAL_JAVA_CLASS_NAME="Main.java";
    private static final long TIME_OUT = 5000L;
    @Override
    public ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        //获取到传入的代码，写到文件中
        File userCodeFile=saveCodeToFile(code);
//        2.编译代码，得到Class文件
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        System.out.println(compileFileExecuteMessage);
        //3.执行代码，得到输出结果。
        List<ExecuteMessage> executeMessagesList = runFile(userCodeFile, inputList);

        //4.收集整理输出结果
        ExecuteCodeRepose outputResponse = getOutputResponse(executeMessagesList);
        //5.文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b){
            log.error("deleteFile error,userCodeFilePath={}",userCodeFile.getAbsolutePath());
        }
        //6.错误处理
        return outputResponse;
    }


    /**
     * 1将用户代码保存成文件
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code){
        //1.获取到传入的代码，写到文件中
        //获取当前的工作目录D:\OJ\dzoj-sandbox
        String userDir=System.getProperty("user.dir");
        //设置路径  File.separator 用来标识/
        String globalCodePathName=userDir+ File.separator+GLOBAL_CODE_DIR_NAME;
        //判断全局路径是否存在，没有则新建
        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }
        //把用户的代码隔离存放
        String userCodeParentPath=globalCodePathName+File.separator+ UUID.randomUUID();

        String userCodePath=userCodeParentPath+File.separator+GLOBAL_JAVA_CLASS_NAME;
        //把用户的代码写入这个用户目录下的Main.java文件中
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2编译文件
     * @param userCodeFile java文件
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile){
        String compileCmd=String.format("javac -encoding utf-8 %s",userCodeFile.getAbsolutePath());
        //java可以使用这个来执行 命令行的命令 需要出来报错
        try {
//            执行编译命令
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            //获取编译时，cmd中输出的信息
            ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(compileProcess, "编译");
            if (executeMessage.getExitValue()!=0){
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 3执行文件,获得执行结果
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile,List<String> inputList){
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }


    /**
     * 4整理获取输出结果
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeRepose getOutputResponse(List<ExecuteMessage> executeMessageList){
        ExecuteCodeRepose executeCodeRepose = new ExecuteCodeRepose();
        List<String> outputList = new ArrayList<>();
        //取用时最大值，便于判断是否超时
        long maxTime=0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)){
                //错误执行信息
                executeCodeRepose.setMessage(errorMessage);
                //执行中存在错误
                executeCodeRepose.setStatus(3);
                break;
            }
            //正确的成功信息
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time!=null){
                //取出来的时间进行比较 取最大的那个
                maxTime=Math.max(maxTime,time);
            }
        }
        //正常运行完成
        if (outputList.size()==executeMessageList.size()){
            executeCodeRepose.setStatus(1);
        }
        executeCodeRepose.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        executeCodeRepose.setJudgeInfo(judgeInfo);
        return executeCodeRepose;
    }

    /**
     * 5删除文件
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile){
        if (userCodeFile.getParentFile() != null) {
            //获取上一级目录
            String userCodeParentPath=userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }



    /**
     * 6.获取错误响应
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
