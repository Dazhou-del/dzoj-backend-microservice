package com.dazhou.dzojbackendmodel.model.dto.question;

import lombok.Data;

/**判题用例
 * @author dazhou
 * @create 2023-08-16 11:41
 */
@Data
public class JudgeCase {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;

}
