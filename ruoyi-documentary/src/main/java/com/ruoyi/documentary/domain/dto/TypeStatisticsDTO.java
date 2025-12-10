package com.ruoyi.documentary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 纪录片类型统计数据传输对象
 *
 * @Author 范佳兴
 * @date 2024/12/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeStatisticsDTO implements Serializable {
    /**
     * 纪录片类型
     */
    private String type;

    /**
     * 该类型纪录片数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}

