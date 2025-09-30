// 不良反应相关API
const request = require('../utils/request')

// 获取所有不良反应记录
const getAllAdverseReactions = () => {
  return request({
    url: '/adverseReaction/listAll',
    method: 'GET'
  })
}

// 根据患者ID获取不良反应列表
const getAdverseReactionsByPatientId = (patientId) => {
  return request({
    url: '/adverseReaction/listByPatientId',
    method: 'GET',
    data: { patientId }
  })
}

// 根据药品ID获取不良反应列表
const getAdverseReactionsByDrugId = (drugId) => {
  return request({
    url: '/adverseReaction/listByDrugId',
    method: 'GET',
    data: { drugId }
  })
}

// 添加不良反应记录
const addAdverseReaction = (adverseReaction) => {
  return request({
    url: '/adverseReaction/add',
    method: 'POST',
    data: adverseReaction
  })
}

// 更新不良反应记录
const updateAdverseReaction = (adverseReaction) => {
  return request({
    url: '/adverseReaction/update',
    method: 'POST',
    data: adverseReaction
  })
}

// 根据ID获取单条不良反应记录
const getAdverseReactionById = (id) => {
  return request({
    url: '/adverseReaction/detail',
    method: 'GET',
    data: { id }
  })
}

module.exports = {
  getAllAdverseReactions,
  getAdverseReactionsByPatientId,
  getAdverseReactionsByDrugId,
  addAdverseReaction,
  updateAdverseReaction,
  getAdverseReactionById
}
