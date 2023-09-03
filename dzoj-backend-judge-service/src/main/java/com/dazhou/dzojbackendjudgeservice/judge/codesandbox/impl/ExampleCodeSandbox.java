package com.dazhou.dzojbackendjudgeservice.judge.codesandbox.impl;


import com.dazhou.dzojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRepose;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.dazhou.dzojbackendmodel.model.codesandbox.JudgeInfo;
import com.dazhou.dzojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.dazhou.dzojbackendmodel.model.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 * @author da zhou
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeRepose executeCodeResponse = new ExecuteCodeRepose();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(StatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;

    }
}
