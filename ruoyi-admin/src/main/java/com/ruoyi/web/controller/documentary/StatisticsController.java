package com.ruoyi.web.controller.documentary;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.documentary.domain.dto.ActionFunnelDTO;
import com.ruoyi.documentary.domain.dto.TimePeriodStatisticsDTO;
import com.ruoyi.documentary.domain.dto.TypeStatisticsDTO;
import com.ruoyi.documentary.domain.dto.UserActionTrendDTO;
import com.ruoyi.documentary.domain.dto.YearStatisticsDTO;
import com.ruoyi.documentary.service.DocumentaryService;
import com.ruoyi.documentary.service.StoreupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计 Controller
 *
 * @Author 范佳兴
 * @date 2024/12/01
 */

@RestController
@RequestMapping("/documentary/statistics")
@RequiredArgsConstructor
public class StatisticsController extends BaseController {
    private final DocumentaryService documentaryService;
    private final StoreupService storeupService;

    /**
     * 获取纪录片发布年份统计
     *
     * @return 年份统计数据
     */
    @GetMapping("/year")
    public AjaxResult getYearStatistics() {
        List<YearStatisticsDTO> statistics = documentaryService.getYearStatistics();
        return AjaxResult.success(statistics);
    }

    /**
     * 获取用户行为时间分布统计
     *
     * @return 时段统计数据
     */
    @GetMapping("/timePeriod")
    public AjaxResult getTimePeriodStatistics() {
        List<TimePeriodStatisticsDTO> statistics = storeupService.getTimePeriodStatistics();
        return AjaxResult.success(statistics);
    }

    /**
     * 获取纪录片类型分布统计
     *
     * @return 类型统计数据
     */
    @GetMapping("/type")
    public AjaxResult getTypeStatistics() {
        List<TypeStatisticsDTO> statistics = documentaryService.getTypeStatistics();
        return AjaxResult.success(statistics);
    }

    /**
     * 获取用户行为漏斗统计
     *
     * @return 行为漏斗统计数据
     */
    @GetMapping("/actionFunnel")
    public AjaxResult getActionFunnelStatistics() {
        List<ActionFunnelDTO> statistics = storeupService.getActionFunnelStatistics();
        return AjaxResult.success(statistics);
    }

    /**
     * 获取当前用户收藏类型分布统计（普通用户个人数据）
     *
     * @return 用户收藏类型统计数据
     */
    @GetMapping("/myCollectionType")
    public AjaxResult getMyCollectionTypeStatistics() {
        Long userId = SecurityUtils.getUserId();
        List<TypeStatisticsDTO> statistics = storeupService.getUserCollectionTypeStatistics(userId);
        return AjaxResult.success(statistics);
    }

    /**
     * 获取当前用户近30天行为趋势统计（普通用户个人数据）
     *
     * @return 用户行为趋势统计数据
     */
    @GetMapping("/myActionTrend")
    public AjaxResult getMyActionTrend() {
        Long userId = SecurityUtils.getUserId();
        List<UserActionTrendDTO> statistics = storeupService.getUserActionTrend(userId);
        return AjaxResult.success(statistics);
    }
}

