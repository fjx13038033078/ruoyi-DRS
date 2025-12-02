package com.ruoyi.documentary.service;

import com.ruoyi.documentary.domain.Documentary;

import java.util.List;

/**
 * 推荐服务接口
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
public interface RecommendationService {
    /**
     * 为用户推荐纪录片（TOP 100）
     *
     * @param userId 用户ID
     * @return 推荐的纪录片列表
     */
    List<Documentary> recommendDocumentaries(Long userId);

    /**
     * 为用户推荐纪录片（自定义数量）
     *
     * @param userId 用户ID
     * @param topN 推荐数量
     * @return 推荐的纪录片列表
     */
    List<Documentary> recommendDocumentaries(Long userId, int topN);

    /**
     * 刷新推荐模型（重新构建协同过滤模型）
     *
     * @return 是否刷新成功
     */
    boolean refreshRecommendationModel();

    /**
     * 获取"其他人此时在看"的推荐（基于当前时段）
     *
     * @param userId 用户ID
     * @return 推荐的纪录片列表（前30条）
     */
    List<Documentary> getWatchingNowRecommendations(Long userId);

    /**
     * 获取"猜你此时想看"的推荐（基于评分）
     *
     * @param userId 用户ID
     * @return 推荐的纪录片列表（前30条）
     */
    List<Documentary> getTopRatedRecommendations(Long userId);
}

