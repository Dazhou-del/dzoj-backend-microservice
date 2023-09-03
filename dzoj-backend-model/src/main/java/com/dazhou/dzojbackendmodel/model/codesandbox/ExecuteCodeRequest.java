package com.dazhou.dzojbackendmodel.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dazhou
 * @title
 * @create 2023-08-17 18:12
 */
@Data
@Builder    //创建对象时可以使用链式编程的方法，为对象中的属性赋值
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeRequest {

    //输入用例
    private List<String> inputList;

    //代码
    private String code;
    //语言
    private String language;
}
