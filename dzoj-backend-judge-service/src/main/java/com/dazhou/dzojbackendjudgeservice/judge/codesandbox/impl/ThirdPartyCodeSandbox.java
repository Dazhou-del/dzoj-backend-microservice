package com.dazhou.dzojbackendjudgeservice.judge.codesandbox.impl;


import com.dazhou.dzojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRepose;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRequest;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 * @author da zhou
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
