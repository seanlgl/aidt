package com.atzy.adit.model.dto.app;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建应用请求
 *这个app包主要是前端页面数据的增删改查操作
 */
@Data
public class AppAddRequest implements Serializable {


    /**
     * 应用名
     */
    private String appName;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 应用图标
     */
    private String appIcon;

    /**
     * 应用类型（0-得分类，1-测评类）
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}