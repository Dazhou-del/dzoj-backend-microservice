package com.dazhou.dzojbackendjudgeservice.judge.strategy;


import com.dazhou.dzojbackendmodel.model.codesandbox.JudgeInfo;
import com.dazhou.dzojbackendmodel.model.dto.question.JudgeCase;
import com.dazhou.dzojbackendmodel.model.entity.Question;
import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 * @author da zhou
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
