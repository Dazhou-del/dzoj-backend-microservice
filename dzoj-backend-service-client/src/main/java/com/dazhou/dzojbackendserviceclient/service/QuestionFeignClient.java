package com.dazhou.dzojbackendserviceclient.service;

import com.dazhou.dzojbackendmodel.model.entity.Question;
import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author dazhou
 * @title
 * @create 2023-08-30 21:58
 */
@FeignClient(name = "dzoj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {
    /**
     * 根据id获取题目
     * @param questionId
     * @return
     */
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    /**
     * 根据id获取题目提交
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId);

    /**
     * 修改题目提交信息
     * @param questionSubmit
     * @return
     */
    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

    @PostMapping("/update/id")
    void updateQuestion(@RequestBody Question question);

}

