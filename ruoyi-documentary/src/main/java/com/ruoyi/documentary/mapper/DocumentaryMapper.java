package com.ruoyi.documentary.mapper;

import com.ruoyi.documentary.domain.Documentary;
import com.ruoyi.documentary.domain.dto.TypeStatisticsDTO;
import com.ruoyi.documentary.domain.dto.YearStatisticsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 纪录片管理 Mapper 接口
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */
@Mapper
public interface DocumentaryMapper {
    /**
     * 获取所有纪录片（支持条件查询）
     *
     * @param documentary 查询条件
     * @return 纪录片列表
     */
    List<Documentary> getAllDocumentaries(Documentary documentary);

    /**
     * 根据纪录片ID获取纪录片信息
     *
     * @param documentaryId 纪录片ID
     * @return 纪录片信息
     */
    Documentary getDocumentaryById(Long documentaryId);

    /**
     * 添加纪录片
     *
     * @param documentary 待添加的纪录片信息
     * @return 添加成功返回影响的行数，否则返回0
     */
    int addDocumentary(Documentary documentary);

    /**
     * 更新纪录片信息
     *
     * @param documentary 待更新的纪录片信息
     * @return 更新成功返回影响的行数，否则返回0
     */
    int updateDocumentary(Documentary documentary);

    /**
     * 删除纪录片
     *
     * @param documentaryId 待删除的纪录片ID
     * @return 删除成功返回影响的行数，否则返回0
     */
    int deleteDocumentary(Long documentaryId);

    /**
     * 更新纪录片审核状态（审核通过）
     *
     * @param documentaryId 纪录片ID
     * @param status 审核状态（1-未审核，2-审核通过，3-审核不通过）
     * @param reviewBy 审核人
     * @return 更新成功返回影响的行数，否则返回0
     */
    int updateDocumentaryStatus(@Param("documentaryId") Long documentaryId, @Param("status") Integer status, @Param("reviewBy") String reviewBy);

    /**
     * 审核拒绝纪录片（含拒绝理由）
     *
     * @param documentaryId 纪录片ID
     * @param rejectReason 拒绝理由
     * @param reviewBy 审核人
     * @return 更新成功返回影响的行数，否则返回0
     */
    int rejectDocumentaryWithReason(@Param("documentaryId") Long documentaryId, @Param("rejectReason") String rejectReason, @Param("reviewBy") String reviewBy);

    /**
     * 根据用户ID获取上传的纪录片列表
     *
     * @param userId 用户ID
     * @return 纪录片列表
     */
    List<Documentary> getDocumentariesByUserId(Long userId);

    /**
     * 统计各年份纪录片数量
     *
     * @return 年份统计列表
     */
    List<YearStatisticsDTO> getYearStatistics();

    /**
     * 统计各类型纪录片数量
     *
     * @return 类型统计列表
     */
    List<TypeStatisticsDTO> getTypeStatistics();
}

