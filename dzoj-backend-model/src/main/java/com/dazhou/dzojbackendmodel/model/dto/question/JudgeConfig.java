package com.dazhou.dzojbackendmodel.model.dto.question;

import lombok.Data;

/**判题配置
 * @author dazhou
 * @create 2023-08-16 11:46
 */
@Data
public class JudgeConfig {
    /**
     * 时间限制（ms）
     */
    private Long timeLimit;

    /**
     * 内存限制(KB)
     */
    private Long memoryLimit;

    /**
     * 推栈限制(KB)
     */
    private Long stackLimit;

}
