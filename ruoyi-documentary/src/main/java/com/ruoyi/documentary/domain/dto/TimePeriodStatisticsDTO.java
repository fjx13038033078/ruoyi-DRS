package com.ruoyi.documentary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 时段统计数据传输对象
 *
 * @Author 范佳兴
 * @date 2024/12/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodStatisticsDTO implements Serializable {
    /**
     * 时段（00:00-06:00, 06:00-12:00, 12:00-18:00, 18:00-24:00）
     */
    private String timePeriod;

    /**
     * 行为数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}

