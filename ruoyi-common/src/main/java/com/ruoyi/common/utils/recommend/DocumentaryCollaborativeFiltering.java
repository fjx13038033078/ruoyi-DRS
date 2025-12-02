package com.ruoyi.common.utils.recommend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 纪录片协同过滤推荐算法（基于用户的协同过滤）
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
public class DocumentaryCollaborativeFiltering {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentaryCollaborativeFiltering.class);
    
    // 推荐算法常量配置
    private static final double SIMILARITY_THRESHOLD = 0.05; // 相似度阈值，过滤掉相似度过低的用户
    private static final int DEFAULT_RECOMMENDATIONS = 100; // 默认推荐数量（TOP 100）
    private static final int MAX_SIMILAR_USERS = 30; // 最大相似用户数量，减少计算量
    
    // 用户行为评分权重配置
    private static final double VIEW_WEIGHT = 1.0;      // 查看行为权重
    private static final double ACCESS_WEIGHT = 3.0;    // 访问链接权重
    private static final double COLLECT_WEIGHT = 5.0;   // 收藏行为权重

    // 用户对纪录片的评分数据，Map<用户ID, Map<纪录片ID, 评分>>
    private Map<Long, Map<Long, Double>> userRatings;

    // 纪录片对应的用户列表，Map<纪录片ID, List<用户ID>>
    private Map<Long, List<Long>> documentaryUsers;

    // 用户到索引的映射
    private Map<Long, Integer> userIndex;

    // 索引到用户的映射
    private Map<Integer, Long> indexUser;

    // 用户共同评分矩阵（稀疏矩阵），记录用户之间共同评分的物品数量
    private Long[][] coRatingMatrix;

    /**
     * 构造函数，初始化推荐系统
     *
     * @param userRatings 用户评分数据，Map<用户ID, Map<纪录片ID, 评分>>
     */
    public DocumentaryCollaborativeFiltering(Map<Long, Map<Long, Double>> userRatings) {
        if (userRatings == null || userRatings.isEmpty()) {
            log.warn("用户评分数据为空，无法初始化推荐系统");
            this.userRatings = new HashMap<>();
            this.documentaryUsers = new HashMap<>();
            this.userIndex = new HashMap<>();
            this.indexUser = new HashMap<>();
            this.coRatingMatrix = new Long[0][0];
            return;
        }

        this.userRatings = userRatings;
        this.documentaryUsers = new HashMap<>();
        this.userIndex = new HashMap<>();
        this.indexUser = new HashMap<>();

        // 构建纪录片-用户倒排表：记录每个纪录片被哪些用户评分
        int keyIndex = 0;
        for (Long userId : userRatings.keySet()) {
            Map<Long, Double> ratings = userRatings.get(userId);
            if (ratings != null) {
                for (Long documentaryId : ratings.keySet()) {
                    documentaryUsers.computeIfAbsent(documentaryId, k -> new ArrayList<>()).add(userId);
                }
            }
            this.userIndex.put(userId, keyIndex);
            this.indexUser.put(keyIndex, userId);
            keyIndex++;
        }

        // 构建用户共同评分矩阵
        int userCount = userRatings.size();
        this.coRatingMatrix = new Long[userCount][userCount];
        for (int i = 0; i < userCount; i++) {
            for (int j = 0; j < userCount; j++) {
                this.coRatingMatrix[i][j] = 0L;
            }
        }

        // 根据纪录片-用户倒排表构建用户共同评分矩阵
        for (Long documentaryId : documentaryUsers.keySet()) {
            List<Long> userList = documentaryUsers.get(documentaryId);
            for (Long user1 : userList) {
                for (Long user2 : userList) {
                    if (!user1.equals(user2)) {
                        Integer idx1 = this.userIndex.get(user1);
                        Integer idx2 = this.userIndex.get(user2);
                        if (idx1 != null && idx2 != null) {
                            this.coRatingMatrix[idx1][idx2]++;
                        }
                    }
                }
            }
        }

        log.info("推荐系统初始化完成：用户数 = {}, 纪录片数 = {}", userCount, documentaryUsers.size());
    }

    /**
     * 计算两个用户之间的余弦相似度
     *
     * @param user1 用户1的ID
     * @param user2 用户2的ID
     * @return 相似度值（0-1之间）
     */
    public double calculateSimilarity(Long user1, Long user2) {
        Integer idx1 = this.userIndex.get(user1);
        Integer idx2 = this.userIndex.get(user2);

        // 如果用户不存在，返回相似度为0
        if (idx1 == null || idx2 == null) {
            return 0.0;
        }

        // 获取两个用户的评分数量
        Map<Long, Double> ratings1 = userRatings.get(user1);
        Map<Long, Double> ratings2 = userRatings.get(user2);

        if (ratings1 == null || ratings2 == null || ratings1.isEmpty() || ratings2.isEmpty()) {
            return 0.0;
        }

        // 计算余弦相似度：共同评分数 / sqrt(用户1评分数 * 用户2评分数)
        long coRatingCount = this.coRatingMatrix[idx1][idx2];
        double denominator = Math.sqrt(ratings1.size() * ratings2.size());

        return denominator > 0 ? coRatingCount / denominator : 0.0;
    }

    /**
     * 为目标用户推荐纪录片（TOP N）
     *
     * @param targetUserId 目标用户ID
     * @param topN 推荐数量
     * @return 推荐的纪录片ID列表（按推荐分数降序）
     */
    public List<Long> recommendDocumentaries(Long targetUserId, int topN) {
        // 获取目标用户的评分数据
        Map<Long, Double> targetUserRatings = userRatings.get(targetUserId);
        if (targetUserRatings == null || targetUserRatings.isEmpty()) {
            log.warn("用户 {} 没有行为数据，返回热门推荐", targetUserId);
            return getHotDocumentaries(topN);
        }

        // 计算目标用户与其他用户的相似度（只保留超过阈值的）
        Map<Long, Double> userSimilarities = new HashMap<>();
        for (Long userId : userRatings.keySet()) {
            if (!userId.equals(targetUserId)) {
                double similarity = calculateSimilarity(targetUserId, userId);
                if (similarity > SIMILARITY_THRESHOLD) {
                    userSimilarities.put(userId, similarity);
                }
            }
        }

        if (userSimilarities.isEmpty()) {
            log.warn("用户 {} 没有找到相似用户，返回热门推荐", targetUserId);
            return getHotDocumentaries(topN);
        }

        // 按相似度降序排序，取前MAX_SIMILAR_USERS个相似用户
        List<Long> similarUsers = userSimilarities.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(MAX_SIMILAR_USERS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.info("为用户 {} 找到 {} 个相似用户", targetUserId, similarUsers.size());

        // 基于相似用户的行为，计算推荐分数
        Map<Long, Double> recommendationScores = new HashMap<>();
        for (Long similarUserId : similarUsers) {
            Map<Long, Double> ratings = userRatings.get(similarUserId);
            if (ratings == null) {
                continue;
            }

            double userSimilarity = userSimilarities.get(similarUserId);
            for (Map.Entry<Long, Double> entry : ratings.entrySet()) {
                Long documentaryId = entry.getKey();
                Double rating = entry.getValue();

                // 只推荐用户未评分过的纪录片
                if (!targetUserRatings.containsKey(documentaryId)) {
                    // 使用相似度加权评分：推荐分数 = 评分 * 相似度
                    double weightedScore = rating * userSimilarity;
                    recommendationScores.merge(documentaryId, weightedScore, Double::sum);
                }
            }
        }

        if (recommendationScores.isEmpty()) {
            log.warn("用户 {} 没有可推荐的纪录片，返回热门推荐", targetUserId);
            return getHotDocumentaries(topN);
        }

        // 按推荐分数降序排序，取前topN个
        List<Long> recommendations = recommendationScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.info("为用户 {} 推荐了 {} 个纪录片", targetUserId, recommendations.size());
        return recommendations;
    }

    /**
     * 获取默认推荐（TOP 100）
     *
     * @param targetUserId 目标用户ID
     * @return 推荐的纪录片ID列表
     */
    public List<Long> recommendDocumentaries(Long targetUserId) {
        return recommendDocumentaries(targetUserId, DEFAULT_RECOMMENDATIONS);
    }

    /**
     * 获取热门纪录片（当用户无行为数据或无相似用户时的降级策略）
     *
     * @param topN 推荐数量
     * @return 热门纪录片ID列表
     */
    private List<Long> getHotDocumentaries(int topN) {
        // 统计每个纪录片被多少用户评分（热度）
        Map<Long, Integer> documentaryPopularity = new HashMap<>();
        for (Long documentaryId : documentaryUsers.keySet()) {
            documentaryPopularity.put(documentaryId, documentaryUsers.get(documentaryId).size());
        }

        // 按热度降序排序，取前topN个
        return documentaryPopularity.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户行为类型计算评分权重
     *
     * @param actionType 行为类型（1-查看，2-访问，3-收藏）
     * @return 评分权重
     */
    public static double getActionWeight(int actionType) {
        switch (actionType) {
            case 1: // 查看
                return VIEW_WEIGHT;
            case 2: // 访问
                return ACCESS_WEIGHT;
            case 3: // 收藏
                return COLLECT_WEIGHT;
            default:
                return 1.0;
        }
    }
}

