package com.dazhou.dzojbackendjudgeservice.judge.strategy;


import com.dazhou.dzojbackendmodel.model.codesandbox.JudgeInfo;

/**
 * 判题策略
 * @author da zhou
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
