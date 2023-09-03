package com.dazhou.dzojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;

import com.dazhou.dzojbackendcommon.common.ErrorCode;
import com.dazhou.dzojbackendcommon.exception.BusinessException;
import com.dazhou.dzojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.dazhou.dzojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.dazhou.dzojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.dazhou.dzojbackendjudgeservice.judge.strategy.JudgeContext;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRepose;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.dazhou.dzojbackendmodel.model.codesandbox.JudgeInfo;
import com.dazhou.dzojbackendmodel.model.dto.question.JudgeCase;
import com.dazhou.dzojbackendmodel.model.entity.Question;
import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;
import com.dazhou.dzojbackendmodel.model.enums.StatusEnum;
import com.dazhou.dzojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author da zhou
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(StatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(StatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        //调用沙箱
        ExecuteCodeRepose executeCodeRepose = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeRepose.getOutputList();
        List<String> updateOutputList = new ArrayList<>();
        //截取字符\n
        if (outputList!=null) {
            for (int i = 0; i < outputList.size(); i++) {
                if (outputList.get(i)!=null){
                    String sxs = outputList.get(i).replaceAll("\\n", "");
                    updateOutputList.add(sxs);
                }
            }
        }
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        //沙箱代码执行后的内存，数据，信息
        judgeContext.setJudgeInfo(executeCodeRepose.getJudgeInfo());
        //设置的是预期输入
        judgeContext.setInputList(inputList);
        //设置的是沙箱代码输出
        judgeContext.setOutputList(updateOutputList);
        //传入的的是预期JudgeCase,里面有预期输出和输入。
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        //出错设置状态为错误
        String message = judgeInfo.getMessage();
        if (!"成功".equals(message)){
            questionSubmitUpdate.setStatus(StatusEnum.FAILED.getValue());
        }else {
            Integer acceptedNum = question.getAcceptedNum();
            Integer submitNum = question.getSubmitNum();
            //通过数+1
            acceptedNum=acceptedNum+1;
            question.setAcceptedNum(acceptedNum);
            //总数+1
            submitNum=submitNum+1;
            question.setSubmitNum(submitNum);
            questionFeignClient.updateQuestion(question);
            questionSubmitUpdate.setStatus(StatusEnum.SUCCEED.getValue());
        }
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }
}
