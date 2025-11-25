import request from '@/utils/request'

// 查询所有纪录片列表
export function listAllDocumentaries(query) {
  return request({
    url: '/documentary/documentary/listAll',
    method: 'get',
    params: query
  })
}

// 查询纪录片详细信息
export function getDocumentaryById(documentaryId) {
  return request({
    url: '/documentary/documentary/detail',
    method: 'get',
    params: { documentaryId }
  })
}

// 添加纪录片
export function addDocumentary(data) {
  return request({
    url: '/documentary/documentary/add',
    method: 'post',
    data: data
  })
}

// 更新纪录片信息
export function updateDocumentary(data) {
  return request({
    url: '/documentary/documentary/update',
    method: 'post',
    data: data
  })
}

// 删除纪录片
export function deleteDocumentary(documentaryId) {
  return request({
    url: '/documentary/documentary/delete',
    method: 'get',
    params: { documentaryId }
  })
}

// 审核通过纪录片
export function approveDocumentary(documentaryId) {
  return request({
    url: '/documentary/documentary/approve',
    method: 'get',
    params: { documentaryId }
  })
}

// 审核不通过纪录片
export function rejectDocumentary(documentaryId) {
  return request({
    url: '/documentary/documentary/reject',
    method: 'get',
    params: { documentaryId }
  })
}

