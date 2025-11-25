package com.ruoyi.documentary.service.impl;

import com.ruoyi.documentary.domain.Comment;
import com.ruoyi.documentary.mapper.CommentMapper;
import com.ruoyi.documentary.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CommentService 实现类
 *
 * @Author 范佳兴
 * @date 2024/11/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

    @Override
    public List<Comment> getAllComments(Comment comment) {
        return commentMapper.getAllComments(comment);
    }

    @Override
    public List<Comment> getCommentsByDocumentaryId(Long documentaryId) {
        return commentMapper.getCommentsByDocumentaryId(documentaryId);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentMapper.getCommentsByUserId(userId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentMapper.getCommentById(commentId);
    }

    @Override
    public boolean addComment(Comment comment) {
        // 检查用户是否已经评论过该纪录片
        Long existingId = commentMapper.checkUserCommentExists(
            comment.getUserId(), 
            comment.getDocumentaryId()
        );
        
        if (existingId != null) {
            log.warn("用户{}已经评论过纪录片{}，不允许重复评论", 
                comment.getUserId(), comment.getDocumentaryId());
            return false;
        }
        
        int rows = commentMapper.addComment(comment);
        return rows > 0;
    }

    @Override
    public boolean updateComment(Comment comment) {
        int rows = commentMapper.updateComment(comment);
        return rows > 0;
    }

    @Override
    public boolean deleteComment(Long commentId) {
        int rows = commentMapper.deleteComment(commentId);
        return rows > 0;
    }

    @Override
    public boolean removeComment(Long commentId) {
        int rows = commentMapper.removeComment(commentId);
        return rows > 0;
    }

    @Override
    public boolean likeComment(Long commentId) {
        int rows = commentMapper.likeComment(commentId);
        return rows > 0;
    }

    @Override
    public boolean checkUserCommentExists(Long userId, Long documentaryId) {
        Long existingId = commentMapper.checkUserCommentExists(userId, documentaryId);
        return existingId != null;
    }

    @Override
    public Double getAverageRating(Long documentaryId) {
        return commentMapper.getAverageRating(documentaryId);
    }

    @Override
    public Integer getCommentCount(Long documentaryId) {
        return commentMapper.getCommentCount(documentaryId);
    }
}

