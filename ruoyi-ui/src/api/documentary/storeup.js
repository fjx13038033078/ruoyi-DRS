import request from '@/utils/request'

// 查询所有行为记录列表
export function listAllStoreups(query) {
  return request({
    url: '/documentary/storeup/listAll',
    method: 'get',
    params: query
  })
}

// 查询行为记录详细信息
export function getStoreupById(storeupId) {
  return request({
    url: '/documentary/storeup/detail',
    method: 'get',
    params: { storeupId }
  })
}

// 根据用户ID和操作类型获取行为记录列表
export function getStoreupsByUserIdAndActionType(userId, actionType, query) {
  return request({
    url: '/documentary/storeup/listByUserAndType',
    method: 'get',
    params: {
      userId,
      actionType,
      ...query
    }
  })
}

// 根据用户ID获取收藏列表
export function getCollectionsByUserId(userId, query) {
  return request({
    url: '/documentary/storeup/collections',
    method: 'get',
    params: {
      userId,
      ...query
    }
  })
}

// 添加行为记录
export function addStoreup(data) {
  return request({
    url: '/documentary/storeup/add',
    method: 'post',
    data: data
  })
}

// 更新行为记录信息
export function updateStoreup(data) {
  return request({
    url: '/documentary/storeup/update',
    method: 'post',
    data: data
  })
}

// 删除行为记录
export function deleteStoreup(storeupId) {
  return request({
    url: '/documentary/storeup/delete',
    method: 'get',
    params: { storeupId }
  })
}

// 取消收藏
export function cancelCollection(userId, documentaryId) {
  return request({
    url: '/documentary/storeup/cancelCollection',
    method: 'get',
    params: { userId, documentaryId }
  })
}

