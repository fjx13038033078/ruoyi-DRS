package com.ruoyi.documentary.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 纪录片用户行为记录实体类
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TableName("drs_storeup")
public class Storeup implements Serializable {
    /**
     * 主键，行为记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long storeupId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 纪录片ID
     */
    private Long documentaryId;

    /**
     * 纪录片名称（不存在于数据库，用于显示）
     */
    @TableField(exist = false)
    private String documentaryName;

    /**
     * 封面图片URL（不存在于数据库，用于显示）
     */
    @TableField(exist = false)
    private String coverImageUrl;

    /**
     * 封面图片路径（不存在于数据库，用于显示）
     */
    @TableField(exist = false)
    private String imagePath;

    /**
     * 操作类型（1-查看，2-访问，3-收藏）
     */
    private Integer actionType;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime actionTime;

    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;
}

