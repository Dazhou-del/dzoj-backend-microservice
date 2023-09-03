package com.dazhou.dzojbackendjudgeservice.judge.codesandbox;


import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRepose;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRequest;

/**
 * 代码沙箱接口定义
 * @author da zhou
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest);
}
