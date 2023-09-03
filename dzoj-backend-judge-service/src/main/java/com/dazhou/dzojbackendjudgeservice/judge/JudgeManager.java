package com.dazhou.dzojbackendjudgeservice.judge;


import com.dazhou.dzojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.dazhou.dzojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.dazhou.dzojbackendjudgeservice.judge.strategy.JudgeContext;
import com.dazhou.dzojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.dazhou.dzojbackendmodel.model.codesandbox.JudgeInfo;
import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 * @author da zhou
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
