package com.ruoyi.documentary.service;

import com.ruoyi.documentary.domain.Documentary;

import java.util.List;

/**
 *
 * 纪录片管理 Service 接口
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */
public interface DocumentaryService {
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
     * @return 添加成功返回 true，否则返回 false
     */
    boolean addDocumentary(Documentary documentary);

    /**
     * 更新纪录片信息
     *
     * @param documentary 待更新的纪录片信息
     * @return 更新成功返回 true，否则返回 false
     */
    boolean updateDocumentary(Documentary documentary);

    /**
     * 删除纪录片
     *
     * @param documentaryId 待删除的纪录片ID
     * @return 删除成功返回 true，否则返回 false
     */
    boolean deleteDocumentary(Long documentaryId);

    /**
     * 审核通过纪录片
     *
     * @param documentaryId 纪录片ID
     * @return 审核成功返回 true，否则返回 false
     */
    boolean approveDocumentary(Long documentaryId);

    /**
     * 审核不通过纪录片
     *
     * @param documentaryId 纪录片ID
     * @return 审核成功返回 true，否则返回 false
     */
    boolean rejectDocumentary(Long documentaryId);
}

