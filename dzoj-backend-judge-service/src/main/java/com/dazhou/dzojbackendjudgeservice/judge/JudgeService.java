package com.dazhou.dzojbackendjudgeservice.judge;


import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;

/**
 * 判题服务
 * @author da zhou
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
