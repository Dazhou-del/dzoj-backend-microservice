package com.dazhou.dzojbackendmodel.model.vo;

import cn.hutool.json.JSONUtil;

import com.dazhou.dzojbackendmodel.model.codesandbox.JudgeInfo;
import com.dazhou.dzojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目封装类
 * @TableName question
 */
@Data
public class QuestionSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private String status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 提交用户信息
     */
    private UserVO userVO;

    /**
     * 提交题目信息
     */
    private QuestionVO questionVO;


    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        //把名字一样的字段复制过去
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);
        //方便前端展示
        questionSubmit.setStatus(Integer.parseInt(questionSubmitVO.getStatus()));
        //JudgeInfo在数据库中是用text来存的，所以在QuestionSubmit中的JudgeInfo是String类型
        //所以我们这里需要把对象类型转换为JOSN字符串类型
        JudgeInfo judgeInfoObj = questionSubmitVO.getJudgeInfo();
        if (judgeInfoObj != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoObj));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        //把名字一样的字段复制过去
        QuestionSubmitVO questionVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionVO);
        //方便展示
        Integer statusOld = questionSubmit.getStatus();
        String statusNew="";
        if (statusOld==0){
            statusNew="待判题";
        } else if (statusOld==1) {
            statusNew="判题中";
        } else if (statusOld==2) {
            statusNew="成功";
        } else if (statusOld==3) {
            statusNew="失败";
        }
        questionVO.setStatus(statusNew);
        //反过来，把QuestionSubmit中String类型 转换为对象类型 好让QuestionSubmitVO能够接受
        JudgeInfo judgeInfo = JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class);
        questionVO.setJudgeInfo(judgeInfo);
        return questionVO;
    }
    private static final long serialVersionUID = 1L;
}