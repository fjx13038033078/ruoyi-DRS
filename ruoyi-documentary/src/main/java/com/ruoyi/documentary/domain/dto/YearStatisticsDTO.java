package com.ruoyi.documentary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 年份统计数据传输对象
 *
 * @Author 范佳兴
 * @date 2024/12/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearStatisticsDTO implements Serializable {
    /**
     * 发布年份
     */
    private Integer year;

    /**
     * 纪录片数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}

