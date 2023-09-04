package com.dazhou.dzojsandbox;

import com.dazhou.dzojsandbox.model.ExecuteCodeRepose;
import com.dazhou.dzojsandbox.model.ExecuteCodeRequest;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author dazhou
 * @title   java原生代码沙箱实现(直接复用模板方法)
 * @create 2023-08-30 15:45
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate{

    @Override
    public ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
