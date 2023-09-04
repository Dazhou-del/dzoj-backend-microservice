package com.dazhou.dzojsandbox.model;

import lombok.Data;

/**
 * @author dazhou
 * @title 进程执行信息
 * @create 2023-08-18 23:02
 */
@Data
public class ExecuteMessage {

    //错误码
    private Integer exitValue;

    //正常输出信息
    private String message;

    //异常输出信息
    private String errorMessage;

    //运行代码的时间
    private Long time;

    private Long memory;
}
