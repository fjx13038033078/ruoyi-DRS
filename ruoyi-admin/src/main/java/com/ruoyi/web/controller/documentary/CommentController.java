package com.ruoyi.web.controller.documentary;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.documentary.domain.Comment;
import com.ruoyi.documentary.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论评分管理 Controller
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
@RestController
@RequestMapping("/documentary/comment")
@RequiredArgsConstructor
public class CommentController extends BaseController {
    private final CommentService commentService;

    /**
     * 查询所有评论列表（分页）
     *
     * @param comment 查询条件
     * @return 评论列表
     */
    @PreAuthorize("@ss.hasPermi('documentary:comment:list')")
    @GetMapping("/list")
    public TableDataInfo listAllComments(Comment comment) {
        startPage();
        List<Comment> comments = commentService.getAllComments(comment);
        return getDataTable(comments);
    }

    /**
     * 根据纪录片ID获取评论列表（不分页，用于前台显示）
     *
     * @param documentaryId 纪录片ID
     * @return 评论列表
     */
    @GetMapping("/listByDocumentary")
    public AjaxResult listCommentsByDocumentary(@RequestParam Long documentaryId) {
        List<Comment> comments = commentService.getCommentsByDocumentaryId(documentaryId);
        return AjaxResult.success(comments);
    }

    /**
     * 根据用户ID获取评论列表
     *
     * @param userId 用户ID
     * @return 评论列表
     */
    @GetMapping("/listByUser")
    public AjaxResult listCommentsByUser(@RequestParam Long userId) {
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        return AjaxResult.success(comments);
    }

    /**
     * 根据评论ID获取评论详情
     *
     * @param commentId 评论ID
     * @return 评论信息
     */
    @GetMapping("/detail")
    public AjaxResult getCommentById(@RequestParam Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return AjaxResult.success(comment);
    }

    /**
     * 添加评论
     *
     * @param comment 评论信息
     * @return 操作结果
     */
    @Log(title = "评论管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addComment(@RequestBody Comment comment) {
        boolean success = commentService.addComment(comment);
        if (success) {
            return AjaxResult.success("评论成功");
        } else {
            return AjaxResult.error("您已经评论过该纪录片，不能重复评论");
        }
    }

    /**
     * 更新评论信息
     *
     * @param comment 评论信息
     * @return 操作结果
     */
    @Log(title = "评论管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult updateComment(@RequestBody Comment comment) {
        return toAjax(commentService.updateComment(comment));
    }

    /**
     * 删除评论（逻辑删除）
     *
     * @param commentId 评论ID
     * @return 操作结果
     */
    @Log(title = "评论管理", businessType = BusinessType.DELETE)
//    @PreAuthorize("@ss.hasPermi('documentary:comment:remove')")
    @GetMapping("/delete")
    public AjaxResult deleteComment(@RequestParam Long commentId) {
        return toAjax(commentService.deleteComment(commentId));
    }

    /**
     * 物理删除评论
     *
     * @param commentId 评论ID
     * @return 操作结果
     */
    @Log(title = "评论管理", businessType = BusinessType.DELETE)
//    @PreAuthorize("@ss.hasPermi('documentary:comment:remove')")
    @GetMapping("/remove")
    public AjaxResult removeComment(@RequestParam Long commentId) {
        return toAjax(commentService.removeComment(commentId));
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return 操作结果
     */
    @Log(title = "评论点赞", businessType = BusinessType.UPDATE)
    @PostMapping("/like")
    public AjaxResult likeComment(@RequestParam Long commentId) {
        return toAjax(commentService.likeComment(commentId));
    }

    /**
     * 检查用户是否已评论
     *
     * @param userId 用户ID
     * @param documentaryId 纪录片ID
     * @return 是否已评论
     */
    @GetMapping("/checkExists")
    public AjaxResult checkUserCommentExists(@RequestParam Long userId, @RequestParam Long documentaryId) {
        boolean exists = commentService.checkUserCommentExists(userId, documentaryId);
        return AjaxResult.success(exists);
    }

    /**
     * 获取纪录片的平均评分
     *
     * @param documentaryId 纪录片ID
     * @return 平均评分
     */
    @GetMapping("/averageRating")
    public AjaxResult getAverageRating(@RequestParam Long documentaryId) {
        Double avgRating = commentService.getAverageRating(documentaryId);
        return AjaxResult.success(avgRating);
    }

    /**
     * 获取纪录片的评论数量
     *
     * @param documentaryId 纪录片ID
     * @return 评论数量
     */
    @GetMapping("/count")
    public AjaxResult getCommentCount(@RequestParam Long documentaryId) {
        Integer count = commentService.getCommentCount(documentaryId);
        return AjaxResult.success(count);
    }
}

