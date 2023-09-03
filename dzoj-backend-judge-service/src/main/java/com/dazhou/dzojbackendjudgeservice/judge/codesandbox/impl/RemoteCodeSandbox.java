package com.dazhou.dzojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.dazhou.dzojbackendcommon.common.ErrorCode;
import com.dazhou.dzojbackendcommon.exception.BusinessException;
import com.dazhou.dzojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRepose;
import com.dazhou.dzojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 * @author da zhou
 */
public class RemoteCodeSandbox implements CodeSandbox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    public static final String URL="http://8.130.71.29:8090/executeCode";


    @Override
    public ExecuteCodeRepose executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("开始调用远程代码沙箱");
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(URL)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        System.out.println("远程代码沙箱结束");
        return JSONUtil.toBean(responseStr, ExecuteCodeRepose.class);
    }
}
