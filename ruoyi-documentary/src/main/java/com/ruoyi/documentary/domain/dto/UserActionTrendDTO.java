package com.ruoyi.documentary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为趋势统计 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActionTrendDTO implements Serializable {
    
    /**
     * 日期
     */
    private Date actionDate;
    
    /**
     * 查看次数
     */
    private Long viewCount;
    
    /**
     * 访问次数
     */
    private Long accessCount;
    
    /**
     * 收藏次数
     */
    private Long collectCount;
    
    private static final long serialVersionUID = 1L;
}


