package com.dazhou.dzojbackendquestionservice.controller.inner;


import com.dazhou.dzojbackendmodel.model.entity.Question;
import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;
import com.dazhou.dzojbackendquestionservice.service.QuestionService;
import com.dazhou.dzojbackendquestionservice.service.QuestionSubmitService;
import com.dazhou.dzojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

    @PostMapping("/update/id")
    @Override
    public void updateQuestion(Question question) {
        questionService.updateById(question);
    }
}
