import request from '@/utils/request'

// 获取当前用户的推荐列表（TOP 100）
export function getRecommendationsForMe() {
  return request({
    url: '/documentary/recommendation/forMe',
    method: 'get'
  })
}

// 获取指定用户的推荐列表（TOP 100）
export function getRecommendationsForUser(userId) {
  return request({
    url: '/documentary/recommendation/forUser',
    method: 'get',
    params: { userId }
  })
}

// 获取指定用户的推荐列表（自定义数量）
export function getTopNRecommendationsForUser(userId, topN) {
  return request({
    url: '/documentary/recommendation/forUserTopN',
    method: 'get',
    params: { userId, topN }
  })
}

// 刷新推荐模型（管理员功能）
export function refreshRecommendationModel() {
  return request({
    url: '/documentary/recommendation/refreshModel',
    method: 'post'
  })
}

// 获取"其他人此时在看"的推荐
export function getWatchingNowRecommendations() {
  return request({
    url: '/documentary/recommendation/watchingNow',
    method: 'get'
  })
}

// 获取"猜你此时想看"的推荐
export function getTopRatedRecommendations() {
  return request({
    url: '/documentary/recommendation/topRated',
    method: 'get'
  })
}

