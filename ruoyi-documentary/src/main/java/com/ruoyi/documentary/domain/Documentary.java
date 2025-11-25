package com.ruoyi.documentary.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 纪录片实体类
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TableName("drs_documentary")
public class Documentary implements Serializable {
    /**
     * 主键，纪录片ID
     */
    @TableId(type = IdType.AUTO)
    private Long documentaryId;

    /**
     * 纪录片名称
     */
    private String documentaryName;

    /**
     * 纪录片详情页链接
     */
    private String detailUrl;

    /**
     * 播放量
     */
    private Long playCount;

    /**
     * 类型
     */
    private String documentaryType;

    /**
     * 年份
     */
    private Integer releaseYear;

    /**
     * 总集数
     */
    private Integer totalEpisodes;

    /**
     * 评分
     */
    private Double rating;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 导演/制作人
     */
    private String director;

    /**
     * 简介
     */
    private String description;

    /**
     * 主要播放时段
     */
    private String broadcastTime;

    /**
     * 审核状态（1-未审核，2-审核通过，3-审核不通过）
     */
    private Integer status;

    /**
     * 请求参数（用于额外的查询条件）
     */
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;
}

