package com.dazhou.dzojsandbox;

import com.dazhou.dzojsandbox.model.ExecuteCodeRepose;
import com.dazhou.dzojsandbox.model.ExecuteCodeRequest;

/**
 * @author dazhou
 * @title 代码沙箱接口定义
 * @create 2023-08-17 18:11
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest);
}
