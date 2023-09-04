package com.dazhou.dzojsandbox.controller;

import com.dazhou.dzojsandbox.JavaDockerCodeSandbox;
import com.dazhou.dzojsandbox.JavaDockerCodeSandboxOld;
import com.dazhou.dzojsandbox.model.ExecuteCodeRepose;
import com.dazhou.dzojsandbox.model.ExecuteCodeRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dazhou
 * @title
 * @create 2023-08-18 17:39
 */
@RestController
public class MainController {
    //用一个字符串来保证接口调用的安全性，只要对面传入这个字符串，那就代表可以调用
    public static final String AUTH_REQUEST_HEADER="auth";
    public static final String AUTH_REQUEST_SECRET="secretKey";
    @Resource
//    private JavaNativeCodeSandbox javaNativeCodeSandbox;
    private JavaDockerCodeSandbox javaDockerCodeSandbox;
    @GetMapping("/health")
    public String healCheck(){
        return "我在测试";
    }

    @PostMapping("/executeCode")
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeRepose executeCode(@RequestBody  ExecuteCodeRequest executeCodeRequest,
                                  HttpServletRequest request,
                                  HttpServletResponse response){
        //基本认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)){
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest==null){
            throw  new RuntimeException("请求参数为空");
        }
        return javaDockerCodeSandbox.executeCode(executeCodeRequest);
    }
}
