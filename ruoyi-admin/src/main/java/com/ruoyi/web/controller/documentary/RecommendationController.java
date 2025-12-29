package com.ruoyi.web.controller.documentary;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.documentary.domain.Documentary;
import com.ruoyi.documentary.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 纪录片推荐控制器
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
@RestController
@RequestMapping("/documentary/recommendation")
@RequiredArgsConstructor
public class RecommendationController extends BaseController {

    private final RecommendationService recommendationService;

    /**
     * 为当前登录用户推荐纪录片（TOP 100）
     *
     * @return 推荐的纪录片列表
     */
    @GetMapping("/forMe")
    public AjaxResult getRecommendationsForCurrentUser() {
        Long userId = getUserId();
        if (userId == null) {
            return AjaxResult.error("请先登录");
        }
        List<Documentary> recommendations = recommendationService.recommendDocumentaries(userId);
        return AjaxResult.success(recommendations);
    }

    /**
     * 为指定用户推荐纪录片（TOP 100）
     *
     * @param userId 用户ID
     * @return 推荐的纪录片列表
     */
    @GetMapping("/forUser")
    public AjaxResult getRecommendationsForUser(@RequestParam Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        List<Documentary> recommendations = recommendationService.recommendDocumentaries(userId);
        return AjaxResult.success(recommendations);
    }

    /**
     * 为指定用户推荐纪录片（自定义数量）
     *
     * @param userId 用户ID
     * @param topN 推荐数量
     * @return 推荐的纪录片列表
     */
    @GetMapping("/forUserTopN")
    public AjaxResult getTopNRecommendationsForUser(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "100") int topN) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (topN <= 0 || topN > 200) {
            return AjaxResult.error("推荐数量必须在1-200之间");
        }
        List<Documentary> recommendations = recommendationService.recommendDocumentaries(userId, topN);
        return AjaxResult.success(recommendations);
    }

    /**
     * 刷新推荐模型（管理员功能）
     * 当用户行为数据有大量更新时，可以手动刷新推荐模型
     *
     * @return 刷新结果
     */
    @Log(title = "推荐模型", businessType = BusinessType.OTHER)
    @PostMapping("/refreshModel")
    public AjaxResult refreshRecommendationModel() {
        boolean success = recommendationService.refreshRecommendationModel();
        if (success) {
            return AjaxResult.success("推荐模型刷新成功");
        } else {
            return AjaxResult.error("推荐模型刷新失败，请查看日志");
        }
    }

    /**
     * 获取"其他人此时在看"的推荐（基于当前时段）
     *
     * @return 推荐的纪录片列表（前30条）
     */
    @GetMapping("/watchingNow")
    public AjaxResult getWatchingNowRecommendations() {
        Long userId = getUserId();
        if (userId == null) {
            return AjaxResult.error("请先登录");
        }
        List<Documentary> recommendations = recommendationService.getWatchingNowRecommendations(userId);
        return AjaxResult.success(recommendations);
    }

    /**
     * 获取"猜你此时想看"的推荐（基于评分）
     *
     * @return 推荐的纪录片列表（前30条）
     */
    @GetMapping("/topRated")
    public AjaxResult getTopRatedRecommendations() {
        Long userId = getUserId();
        if (userId == null) {
            return AjaxResult.error("请先登录");
        }
        List<Documentary> recommendations = recommendationService.getTopRatedRecommendations(userId);
        return AjaxResult.success(recommendations);
    }
}

