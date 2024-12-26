package com.atzy.adit.scoring;

import com.atzy.adit.model.entity.App;
import com.atzy.adit.model.entity.UserAnswer;

import java.util.List;

/**
 * @description: 评分策略接口
 * @param: [choices, app]
 */
public interface ScoringStrategy {
    /**
     * @description:
     * @param: [choices, app]用户的答案列表，应用
     */
    UserAnswer doScore(List<String> choices, App app) throws Exception;
}
