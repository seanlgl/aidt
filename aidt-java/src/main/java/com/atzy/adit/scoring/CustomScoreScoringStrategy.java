package com.atzy.adit.scoring;

import cn.hutool.json.JSONUtil;
import com.atzy.adit.model.dto.question.QuestionContentDTO;
import com.atzy.adit.model.entity.App;
import com.atzy.adit.model.entity.Question;
import com.atzy.adit.model.entity.ScoringResult;
import com.atzy.adit.model.entity.UserAnswer;
import com.atzy.adit.model.vo.QuestionVO;
import com.atzy.adit.service.QuestionService;
import com.atzy.adit.service.ScoringResultService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @description:自定义得分类应用评分策略
 * @param:
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //根据Id查询到题目和题目结果信息(按分数降序排序)
        Long appId = app.getId();
        Question question = questionService.getOne(Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId));
        List<ScoringResult> scoringResultList = scoringResultService.list(Wrappers.lambdaQuery(ScoringResult.class)
                .eq(ScoringResult::getAppId, appId).orderByDesc(ScoringResult::getResultScoreRange));
        //统计用户的总得分
        //定义一个初始值
        int totalScore = 0;
        //对象转化为封装类 获取题目信息
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        //遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            //遍历答案列表
            for (String choice : choices) {
                //遍历题目中的选项列表
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    //如果答案和选项中的key值匹配
                    if (option.getKey().equals(choice)) {
                        //将选项中的分数加到总分数中
                        Integer score = Optional.of(option.getScore()).orElse(0);
                        totalScore += score;
                    }
                }
            }
        }
        //遍历得分结果，找到第一个用户分数大于得分范围的结果将其作为最终结果
        //定义一个初始最大值
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                break;
            }
        }
        //构造返回值,填充相应的属性值
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
        }
    }
