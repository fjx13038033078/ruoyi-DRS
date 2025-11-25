import request from '@/utils/request'

// 查询所有评论列表（分页）
export function listAllComments(query) {
  return request({
    url: '/documentary/comment/list',
    method: 'get',
    params: query
  })
}

// 根据纪录片ID获取评论列表
export function listCommentsByDocumentary(documentaryId) {
  return request({
    url: '/documentary/comment/listByDocumentary',
    method: 'get',
    params: { documentaryId }
  })
}

// 根据用户ID获取评论列表
export function listCommentsByUser(userId) {
  return request({
    url: '/documentary/comment/listByUser',
    method: 'get',
    params: { userId }
  })
}

// 查询评论详细信息
export function getCommentById(commentId) {
  return request({
    url: '/documentary/comment/detail',
    method: 'get',
    params: { commentId }
  })
}

// 添加评论
export function addComment(data) {
  return request({
    url: '/documentary/comment/add',
    method: 'post',
    data: data
  })
}

// 更新评论信息
export function updateComment(data) {
  return request({
    url: '/documentary/comment/update',
    method: 'post',
    data: data
  })
}

// 删除评论（逻辑删除）
export function deleteComment(commentId) {
  return request({
    url: '/documentary/comment/delete',
    method: 'get',
    params: { commentId }
  })
}

// 物理删除评论
export function removeComment(commentId) {
  return request({
    url: '/documentary/comment/remove',
    method: 'get',
    params: { commentId }
  })
}

// 点赞评论
export function likeComment(commentId) {
  return request({
    url: '/documentary/comment/like',
    method: 'post',
    params: { commentId }
  })
}

// 检查用户是否已评论
export function checkUserCommentExists(userId, documentaryId) {
  return request({
    url: '/documentary/comment/checkExists',
    method: 'get',
    params: { userId, documentaryId }
  })
}

// 获取纪录片的平均评分
export function getAverageRating(documentaryId) {
  return request({
    url: '/documentary/comment/averageRating',
    method: 'get',
    params: { documentaryId }
  })
}

// 获取纪录片的评论数量
export function getCommentCount(documentaryId) {
  return request({
    url: '/documentary/comment/count',
    method: 'get',
    params: { documentaryId }
  })
}

