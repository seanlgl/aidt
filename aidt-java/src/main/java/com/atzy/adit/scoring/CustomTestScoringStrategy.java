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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 自定义测评类应用评分策略
 * @param: [choices, app]
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //根据id查询题目和题目结果信息
        Long appId = app.getId();
        //构建查询条件
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
        );
        //统计用户每个选择对应的属性个数 先初始化一个map来存储每种选择对应的评分结果个数
        Map<String, Integer> optionCount = new HashMap<>();
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        //遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            //遍历答案列表
            for (String answer : choices) {
                //遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    //如果答案和选项中的Key匹配
                    if (option.getKey().equals(answer)) {
                        //获取选项的评分结果
                        String result = option.getResult();
                        //如果该结果不在计数中,将其初始化为0
                        if (!optionCount.containsKey(result)) {
                            optionCount.put(result, 0);
                        }
                        //如果在其中,则计数加1
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }
        //遍历每种评分结果,计算哪个评分结果得分最高
        //先初始化一个最高分数以及一个最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            //结果属性转化为List集合
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            // 计算当前结果的得分
            int score = 0;
            for (String result : optionCount.keySet()) {
                if (resultProp.contains(result)) {
                    score += optionCount.get(result);
                }
            }

            //如果当前得分大于最高得分,则更新最高得分和对应的评分结果
            if (score > maxScore) {
                maxScore = score;
                maxScoringResult = scoringResult;
            }
        }
        //构造返回值,填充答案的属性信息
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
