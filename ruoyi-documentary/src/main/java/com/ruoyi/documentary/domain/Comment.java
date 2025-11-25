package com.ruoyi.documentary.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 评论评分实体类
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TableName("drs_comment")
public class Comment implements Serializable {

    /**
     * 评论ID，主键，自增
     */
    @TableId
    private Long commentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 纪录片ID
     */
    private Long documentaryId;

    /**
     * 评分（0.0-10.0，可为空表示只评论不评分）
     */
    private BigDecimal rating;

    /**
     * 评论内容（可为空表示只评分不评论）
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态（1-正常，2-已删除，3-待审核）
     */
    private Integer status;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户名称（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户昵称（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private String nickName;

    /**
     * 纪录片名称（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private String documentaryName;

    /**
     * 请求参数（用于额外的查询条件）
     */
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

    private static final long serialVersionUID = 1L;
}

