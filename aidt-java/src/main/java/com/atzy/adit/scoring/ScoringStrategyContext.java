package com.atzy.adit.scoring;

import com.atzy.adit.common.ErrorCode;
import com.atzy.adit.exception.BusinessException;
import com.atzy.adit.model.entity.App;
import com.atzy.adit.model.entity.UserAnswer;
import com.atzy.adit.model.enums.AppTypeEnum;
import com.atzy.adit.model.enums.ScoringStrategyEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:根据不同的应用类别和评分策略,选择对应的策略执行 编程时方式
 * @param: 
 */
@Service
@Deprecated
public class ScoringStrategyContext {
    @Resource
    private CustomScoreScoringStrategy customScoreScoringStrategy;
    @Resource
    private CustomTestScoringStrategy customTestScoringStrategy;

    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //获取传入的应用类型
        Integer appType = app.getAppType();
        AppTypeEnum appTypeEnum = AppTypeEnum.getEnumByValue(appType);
        //获取传入的评分策略
        Integer scoringStrategy = app.getScoringStrategy();
        ScoringStrategyEnum scoringStrategyEnum = ScoringStrategyEnum.getEnumByValue(scoringStrategy);
        //校验
        if (appTypeEnum == null || scoringStrategyEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用类型或评分策略不存在");
        }
        //根据不同的应用类型和评分策略,选择对应的策略去执行
        switch (appTypeEnum) {
            case SCORE:
                switch (scoringStrategyEnum) {
                    case CUSTOM:
                        return customScoreScoringStrategy.doScore(choices, app);
                    case AI:
                        break;
                }
                break;
            case TEST:
                switch (scoringStrategyEnum) {
                    case CUSTOM:
                        return customTestScoringStrategy.doScore(choices, app);
                    case AI:
                        break;
                }
                break;
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未找到对应的匹配策略");
    }
}
