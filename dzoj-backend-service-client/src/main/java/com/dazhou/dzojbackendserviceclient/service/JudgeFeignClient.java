package com.dazhou.dzojbackendserviceclient.service;
import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author dazhou
 * @title 判题服务
 * @create 2023-08-30 21:57
 */
@FeignClient(name = "dzoj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}

