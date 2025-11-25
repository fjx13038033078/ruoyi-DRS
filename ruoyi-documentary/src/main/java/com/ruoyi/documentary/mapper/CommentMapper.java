package com.ruoyi.documentary.mapper;

import com.ruoyi.documentary.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论评分管理 Mapper 接口
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
@Mapper
public interface CommentMapper {
    /**
     * 获取所有评论（支持条件查询，关联用户和纪录片信息）
     *
     * @param comment 查询条件
     * @return 评论列表
     */
    List<Comment> getAllComments(Comment comment);

    /**
     * 根据纪录片ID获取评论列表（用户可见的正常评论）
     *
     * @param documentaryId 纪录片ID
     * @return 评论列表
     */
    List<Comment> getCommentsByDocumentaryId(@Param("documentaryId") Long documentaryId);

    /**
     * 根据用户ID获取评论列表
     *
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> getCommentsByUserId(@Param("userId") Long userId);

    /**
     * 根据评论ID获取评论信息
     *
     * @param commentId 评论ID
     * @return 评论信息
     */
    Comment getCommentById(@Param("commentId") Long commentId);

    /**
     * 添加评论
     *
     * @param comment 待添加的评论信息
     * @return 添加成功返回影响的行数，否则返回0
     */
    int addComment(Comment comment);

    /**
     * 更新评论信息
     *
     * @param comment 待更新的评论信息
     * @return 更新成功返回影响的行数，否则返回0
     */
    int updateComment(Comment comment);

    /**
     * 删除评论（逻辑删除，修改status为2）
     *
     * @param commentId 待删除的评论ID
     * @return 删除成功返回影响的行数，否则返回0
     */
    int deleteComment(@Param("commentId") Long commentId);

    /**
     * 物理删除评论
     *
     * @param commentId 待删除的评论ID
     * @return 删除成功返回影响的行数，否则返回0
     */
    int removeComment(@Param("commentId") Long commentId);

    /**
     * 点赞评论（增加点赞数）
     *
     * @param commentId 评论ID
     * @return 更新成功返回影响的行数，否则返回0
     */
    int likeComment(@Param("commentId") Long commentId);

    /**
     * 检查用户是否已经评论过该纪录片
     *
     * @param userId 用户ID
     * @param documentaryId 纪录片ID
     * @return 评论ID，如果不存在则返回null
     */
    Long checkUserCommentExists(@Param("userId") Long userId, @Param("documentaryId") Long documentaryId);

    /**
     * 获取纪录片的平均评分
     *
     * @param documentaryId 纪录片ID
     * @return 平均评分
     */
    Double getAverageRating(@Param("documentaryId") Long documentaryId);

    /**
     * 获取纪录片的评论数量
     *
     * @param documentaryId 纪录片ID
     * @return 评论数量
     */
    Integer getCommentCount(@Param("documentaryId") Long documentaryId);
}

