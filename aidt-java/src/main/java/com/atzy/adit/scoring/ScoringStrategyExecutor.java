package com.atzy.adit.scoring;

import com.atzy.adit.common.ErrorCode;
import com.atzy.adit.exception.BusinessException;
import com.atzy.adit.model.entity.App;
import com.atzy.adit.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:评分策略执行器
 * @param:
 */
@Service
public class ScoringStrategyExecutor {
    //注入策略列表
    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    public UserAnswer doScore(List<String> choiceList, App app) throws Exception {
        //获取传入的应用类型和评分策略
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();
        //校验
        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        //根据注解获取对应的策略
        for (ScoringStrategy strategy : scoringStrategyList) {
            //反射机制,检查是否包含该注解
            if (strategy != null && strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                //包含的话就获取注解的实例
                ScoringStrategyConfig scoringStrategyConfig = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                //判断应用类型和评分策略是否匹配
                if (scoringStrategyConfig != null && scoringStrategyConfig.appType() == appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy) {
                    //匹配的话就执行策略
                    return strategy.doScore(choiceList, app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未找到匹配的策略");
    }
}
