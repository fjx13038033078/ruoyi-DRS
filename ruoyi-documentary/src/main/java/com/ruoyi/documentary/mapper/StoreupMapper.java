package com.ruoyi.documentary.mapper;

import com.ruoyi.documentary.domain.Storeup;
import com.ruoyi.documentary.domain.dto.ActionFunnelDTO;
import com.ruoyi.documentary.domain.dto.TimePeriodStatisticsDTO;
import com.ruoyi.documentary.domain.dto.TypeStatisticsDTO;
import com.ruoyi.documentary.domain.dto.UserActionTrendDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 用户行为记录管理 Mapper 接口
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */
@Mapper
public interface StoreupMapper {
    /**
     * 获取所有行为记录
     *
     * @return 所有行为记录列表
     */
    List<Storeup> getAllStoreupsWithDocumentaryName();

    /**
     * 根据行为记录ID获取行为记录信息
     *
     * @param storeupId 行为记录ID
     * @return 行为记录信息
     */
    Storeup getStoreupById(Long storeupId);

    /**
     * 根据用户ID和操作类型获取行为记录列表
     *
     * @param userId 用户ID
     * @param actionType 操作类型（1-查看，2-访问，3-收藏）
     * @return 行为记录列表
     */
    List<Storeup> getStoreupsByUserIdAndActionType(@Param("userId") Long userId, @Param("actionType") Integer actionType);

    /**
     * 根据用户ID获取收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Storeup> getCollectionsByUserId(Long userId);

    /**
     * 添加行为记录
     *
     * @param storeup 待添加的行为记录信息
     * @return 添加成功返回影响的行数，否则返回0
     */
    int addStoreup(Storeup storeup);

    /**
     * 更新行为记录信息
     *
     * @param storeup 待更新的行为记录信息
     * @return 更新成功返回影响的行数，否则返回0
     */
    int updateStoreup(Storeup storeup);

    /**
     * 删除行为记录
     *
     * @param storeupId 待删除的行为记录ID
     * @return 删除成功返回影响的行数，否则返回0
     */
    int deleteStoreup(Long storeupId);

    /**
     * 检查用户是否已收藏该纪录片
     *
     * @param userId 用户ID
     * @param documentaryId 纪录片ID
     * @param actionType 操作类型
     * @return 存在返回记录ID，不存在返回null
     */
    Long checkStoreupExists(@Param("userId") Long userId, @Param("documentaryId") Long documentaryId, @Param("actionType") Integer actionType);

    /**
     * 根据用户ID和纪录片ID删除收藏
     *
     * @param userId 用户ID
     * @param documentaryId 纪录片ID
     * @return 删除成功返回影响的行数，否则返回0
     */
    int deleteStoreupByUserAndDocumentary(@Param("userId") Long userId, @Param("documentaryId") Long documentaryId);

    /**
     * 统计各时段用户行为数量
     *
     * @return 时段统计列表
     */
    List<TimePeriodStatisticsDTO> getTimePeriodStatistics();

    /**
     * 统计各类型用户行为数量（行为漏斗）
     *
     * @return 行为漏斗统计列表
     */
    List<ActionFunnelDTO> getActionFunnelStatistics();

    /**
     * 统计用户收藏的纪录片类型分布
     *
     * @param userId 用户ID
     * @return 类型统计列表
     */
    List<TypeStatisticsDTO> getUserCollectionTypeStatistics(Long userId);

    /**
     * 统计用户近30天的行为趋势
     *
     * @param userId 用户ID
     * @return 行为趋势列表
     */
    List<UserActionTrendDTO> getUserActionTrend(Long userId);
}

