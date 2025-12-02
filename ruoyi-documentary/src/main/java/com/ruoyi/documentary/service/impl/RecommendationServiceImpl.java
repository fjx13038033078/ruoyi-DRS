package com.ruoyi.documentary.service.impl;

import com.ruoyi.common.utils.recommend.DocumentaryCollaborativeFiltering;
import com.ruoyi.documentary.domain.Documentary;
import com.ruoyi.documentary.domain.Storeup;
import com.ruoyi.documentary.mapper.DocumentaryMapper;
import com.ruoyi.documentary.mapper.StoreupMapper;
import com.ruoyi.documentary.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final StoreupMapper storeupMapper;
    private final DocumentaryMapper documentaryMapper;

    // 协同过滤推荐引擎
    private DocumentaryCollaborativeFiltering recommendEngine;

    /**
     * 初始化推荐模型（应用启动时自动执行）
     */
    @PostConstruct
    public void initRecommendationModel() {
        log.info("开始初始化推荐模型...");
        try {
            refreshRecommendationModel();
            log.info("推荐模型初始化完成");
        } catch (Exception e) {
            log.error("推荐模型初始化失败", e);
        }
    }

    @Override
    public List<Documentary> recommendDocumentaries(Long userId) {
        return recommendDocumentaries(userId, 100);
    }

    @Override
    public List<Documentary> recommendDocumentaries(Long userId, int topN) {
        if (userId == null) {
            log.warn("用户ID为空，无法推荐");
            return new ArrayList<>();
        }

        // 如果推荐引擎未初始化，先初始化
        if (recommendEngine == null) {
            log.info("推荐引擎未初始化，正在初始化...");
            refreshRecommendationModel();
        }

        // 获取推荐的纪录片ID列表
        List<Long> recommendedDocumentaryIds = recommendEngine.recommendDocumentaries(userId, topN);

        if (recommendedDocumentaryIds.isEmpty()) {
            log.warn("用户 {} 没有推荐结果", userId);
            return new ArrayList<>();
        }

        // 根据ID列表获取纪录片详细信息
        List<Documentary> recommendedDocumentaries = new ArrayList<>();
        for (Long documentaryId : recommendedDocumentaryIds) {
            Documentary documentary = documentaryMapper.getDocumentaryById(documentaryId);
            if (documentary != null && documentary.getStatus() != null && documentary.getStatus() == 2) {
                // 只返回审核通过的纪录片
                recommendedDocumentaries.add(documentary);
            }
        }

        log.info("为用户 {} 推荐了 {} 部纪录片", userId, recommendedDocumentaries.size());
        return recommendedDocumentaries;
    }

    @Override
    public boolean refreshRecommendationModel() {
        try {
            log.info("开始刷新推荐模型...");

            // 1. 从数据库获取所有用户行为记录
            List<Storeup> allStoreups = storeupMapper.getAllStoreupsWithDocumentaryName();

            if (allStoreups == null || allStoreups.isEmpty()) {
                log.warn("没有用户行为数据，无法构建推荐模型");
                return false;
            }

            // 2. 构建用户-纪录片评分矩阵
            Map<Long, Map<Long, Double>> userRatings = buildUserRatingsMatrix(allStoreups);

            if (userRatings.isEmpty()) {
                log.warn("用户评分矩阵为空，无法构建推荐模型");
                return false;
            }

            // 3. 创建协同过滤推荐引擎
            this.recommendEngine = new DocumentaryCollaborativeFiltering(userRatings);

            log.info("推荐模型刷新完成，用户数：{}", userRatings.size());
            return true;
        } catch (Exception e) {
            log.error("刷新推荐模型失败", e);
            return false;
        }
    }

    /**
     * 构建用户-纪录片评分矩阵
     * 将用户的多次行为聚合为评分（不同行为类型有不同权重）
     *
     * @param storeups 用户行为记录列表
     * @return 用户-纪录片评分矩阵
     */
    private Map<Long, Map<Long, Double>> buildUserRatingsMatrix(List<Storeup> storeups) {
        // Map<用户ID, Map<纪录片ID, 评分>>
        Map<Long, Map<Long, Double>> userRatings = new HashMap<>();

        for (Storeup storeup : storeups) {
            Long userId = storeup.getUserId();
            Long documentaryId = storeup.getDocumentaryId();
            Integer actionType = storeup.getActionType();

            if (userId == null || documentaryId == null || actionType == null) {
                continue;
            }

            // 根据行为类型获取权重
            double weight = DocumentaryCollaborativeFiltering.getActionWeight(actionType);

            // 累加评分（同一用户对同一纪录片的多次行为会累加）
            userRatings
                    .computeIfAbsent(userId, k -> new HashMap<>())
                    .merge(documentaryId, weight, Double::sum);
        }

        // 对评分进行归一化处理（可选，避免某些用户评分过高）
        normalizeRatings(userRatings);

        log.info("构建用户评分矩阵：用户数 = {}, 总评分数 = {}",
                userRatings.size(),
                userRatings.values().stream().mapToInt(Map::size).sum());

        return userRatings;
    }

    /**
     * 归一化用户评分（可选）
     * 将每个用户的评分标准化到合理范围
     *
     * @param userRatings 用户评分矩阵
     */
    private void normalizeRatings(Map<Long, Map<Long, Double>> userRatings) {
        for (Map<Long, Double> ratings : userRatings.values()) {
            if (ratings.isEmpty()) {
                continue;
            }

            // 计算用户的最大评分
            double maxRating = ratings.values().stream()
                    .max(Double::compareTo)
                    .orElse(1.0);

            // 归一化到0-10的范围
            if (maxRating > 10.0) {
                for (Map.Entry<Long, Double> entry : ratings.entrySet()) {
                    entry.setValue(entry.getValue() / maxRating * 10.0);
                }
            }
        }
    }

    @Override
    public List<Documentary> getWatchingNowRecommendations(Long userId) {
        if (userId == null) {
            log.warn("用户ID为空，无法推荐");
            return new ArrayList<>();
        }

        // 获取当前北京时间并确定时段
        String currentTimePeriod = getCurrentTimePeriod();
        log.info("当前时段：{}", currentTimePeriod);

        // 获取用户的TOP 100推荐
        List<Documentary> allRecommendations = recommendDocumentaries(userId, 100);

        // 筛选出与当前时段匹配的纪录片
        List<Documentary> matchingDocumentaries = new ArrayList<>();
        if (!allRecommendations.isEmpty()) {
            matchingDocumentaries = allRecommendations.stream()
                    .filter(doc -> currentTimePeriod.equals(doc.getBroadcastTime()))
                    .limit(30)
                    .collect(Collectors.toList());
        }

        // 冷启动优化：如果匹配的纪录片不足30条，补充热门纪录片
        if (matchingDocumentaries.size() < 30) {
            log.info("当前时段匹配的推荐不足30条，补充热门纪录片");
            List<Documentary> hotDocumentaries = getHotDocumentariesByTimePeriod(currentTimePeriod, 30 - matchingDocumentaries.size());
            
            // 去重：只添加不在已有列表中的纪录片
            Set<Long> existingIds = matchingDocumentaries.stream()
                    .map(Documentary::getDocumentaryId)
                    .collect(Collectors.toSet());
            
            for (Documentary doc : hotDocumentaries) {
                if (!existingIds.contains(doc.getDocumentaryId())) {
                    matchingDocumentaries.add(doc);
                    if (matchingDocumentaries.size() >= 30) {
                        break;
                    }
                }
            }
        }

        log.info("为用户 {} 推荐了 {} 部此时在看的纪录片", userId, matchingDocumentaries.size());
        return matchingDocumentaries;
    }

    @Override
    public List<Documentary> getTopRatedRecommendations(Long userId) {
        if (userId == null) {
            log.warn("用户ID为空，无法推荐");
            return new ArrayList<>();
        }

        // 获取用户的TOP 100推荐
        List<Documentary> allRecommendations = recommendDocumentaries(userId, 100);

        // 按评分降序排序，取前30条
        List<Documentary> topRatedDocumentaries = new ArrayList<>();
        if (!allRecommendations.isEmpty()) {
            topRatedDocumentaries = allRecommendations.stream()
                    .filter(doc -> doc.getRating() != null && doc.getRating() > 0)
                    .sorted((d1, d2) -> {
                        Double rating1 = d1.getRating();
                        Double rating2 = d2.getRating();
                        return rating2.compareTo(rating1); // 降序
                    })
                    .limit(30)
                    .collect(Collectors.toList());
        }

        // 冷启动优化：如果高评分推荐不足30条，补充全局高评分或热门纪录片
        if (topRatedDocumentaries.size() < 30) {
            log.info("高评分推荐不足30条，补充全局高评分纪录片");
            List<Documentary> globalTopRated = getGlobalTopRatedDocumentaries(30 - topRatedDocumentaries.size());
            
            // 去重：只添加不在已有列表中的纪录片
            Set<Long> existingIds = topRatedDocumentaries.stream()
                    .map(Documentary::getDocumentaryId)
                    .collect(Collectors.toSet());
            
            for (Documentary doc : globalTopRated) {
                if (!existingIds.contains(doc.getDocumentaryId())) {
                    topRatedDocumentaries.add(doc);
                    if (topRatedDocumentaries.size() >= 30) {
                        break;
                    }
                }
            }
        }

        log.info("为用户 {} 推荐了 {} 部高评分纪录片", userId, topRatedDocumentaries.size());
        return topRatedDocumentaries;
    }

    /**
     * 获取当前北京时间所属的时段
     *
     * @return 时段字符串（00:00-06:00, 06:00-12:00, 12:00-18:00, 18:00-24:00）
     */
    private String getCurrentTimePeriod() {
        // 获取北京时间（东八区）
        ZonedDateTime beijingTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        LocalTime currentTime = beijingTime.toLocalTime();
        int hour = currentTime.getHour();

        // 根据小时确定时段
        if (hour >= 0 && hour < 6) {
            return "00:00-06:00";
        } else if (hour >= 6 && hour < 12) {
            return "06:00-12:00";
        } else if (hour >= 12 && hour < 18) {
            return "12:00-18:00";
        } else {
            return "18:00-24:00";
        }
    }

    /**
     * 按时段获取热门纪录片（冷启动降级策略）
     * 优先返回当前时段的热门纪录片，按播放量排序
     *
     * @param timePeriod 时段
     * @param limit 数量限制
     * @return 热门纪录片列表
     */
    private List<Documentary> getHotDocumentariesByTimePeriod(String timePeriod, int limit) {
        // 构建查询条件：只查询审核通过且匹配时段的纪录片
        Documentary query = new Documentary();
        query.setStatus(2); // 审核通过
        query.setBroadcastTime(timePeriod);
        
        // 查询所有匹配的纪录片
        List<Documentary> documentaries = documentaryMapper.getAllDocumentaries(query);
        
        if (documentaries.isEmpty()) {
            log.warn("时段 {} 没有纪录片，尝试获取其他时段的热门纪录片", timePeriod);
            // 如果当前时段没有纪录片，返回全局热门纪录片
            Documentary globalQuery = new Documentary();
            globalQuery.setStatus(2);
            documentaries = documentaryMapper.getAllDocumentaries(globalQuery);
        }
        
        // 按播放量降序排序，取前limit条
        return documentaries.stream()
                .filter(doc -> doc.getPlayCount() != null)
                .sorted((d1, d2) -> {
                    Long count1 = d1.getPlayCount();
                    Long count2 = d2.getPlayCount();
                    return count2.compareTo(count1); // 降序
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取全局高评分纪录片（冷启动降级策略）
     * 按评分降序排序，如果评分不足则按点赞数排序
     *
     * @param limit 数量限制
     * @return 高评分纪录片列表
     */
    private List<Documentary> getGlobalTopRatedDocumentaries(int limit) {
        // 构建查询条件：只查询审核通过的纪录片
        Documentary query = new Documentary();
        query.setStatus(2); // 审核通过
        
        // 查询所有审核通过的纪录片
        List<Documentary> documentaries = documentaryMapper.getAllDocumentaries(query);
        
        if (documentaries.isEmpty()) {
            log.warn("没有审核通过的纪录片");
            return new ArrayList<>();
        }
        
        // 优先按评分排序，评分相同则按点赞数排序
        return documentaries.stream()
                .sorted((d1, d2) -> {
                    // 如果都有评分，按评分降序
                    if (d1.getRating() != null && d2.getRating() != null) {
                        int ratingCompare = d2.getRating().compareTo(d1.getRating());
                        if (ratingCompare != 0) {
                            return ratingCompare;
                        }
                    } else if (d1.getRating() != null) {
                        return -1; // d1有评分，d1优先
                    } else if (d2.getRating() != null) {
                        return 1; // d2有评分，d2优先
                    }
                    
                    // 评分相同或都没有评分，按点赞数降序
                    Long like1 = d1.getLikeCount() != null ? d1.getLikeCount() : 0L;
                    Long like2 = d2.getLikeCount() != null ? d2.getLikeCount() : 0L;
                    return like2.compareTo(like1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
}

