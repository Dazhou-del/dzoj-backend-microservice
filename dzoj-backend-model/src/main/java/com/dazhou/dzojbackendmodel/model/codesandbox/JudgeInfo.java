package com.dazhou.dzojbackendmodel.model.codesandbox;

import lombok.Data;

/**
 * 判题信息
 * @author dazhou
 * @create 2023-08-16 11:46
 */
@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存(KB)
     */
    private Long memory;

    /**
     * 消耗时间(ms)
     */
    private Long time;

}
