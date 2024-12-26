package com.atzy.adit.scoring;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:根据不同的应用类别和评分策略,选择对应的策略执行 声明式
 * @param:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScoringStrategyConfig {
    /**
     * @description: 应用类别
     * @param:
     */
    int appType();

    /**
     * @description: 评分策略
     * @param:
     */
    int scoringStrategy();
}
