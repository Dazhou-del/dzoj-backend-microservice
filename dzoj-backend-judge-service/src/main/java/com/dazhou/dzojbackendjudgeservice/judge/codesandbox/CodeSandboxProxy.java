package com.dazhou.dzojbackendjudgeservice.judge.codesandbox;


import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRepose;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author da zhou
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;


    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeRepose executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
