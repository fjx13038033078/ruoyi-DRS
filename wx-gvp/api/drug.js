// 药品相关API
const request = require('../utils/request')

// 获取所有药品列表
const getAllDrugs = () => {
  return request({
    url: '/drug/listAll',
    method: 'GET'
  })
}

// 根据药品ID获取详情
const getDrugById = (drugId) => {
  return request({
    url: '/drug/detail',
    method: 'GET',
    data: {
      drugId: drugId
    }
  })
}

// 根据药品ID获取说明书
const getDrugInstruction = (drugId) => {
  return request({
    url: '/drugInstruction/listByDrugId',
    method: 'GET',
    data: {
      drugId: drugId
    }
  })
}

module.exports = {
  getAllDrugs,
  getDrugById,
  getDrugInstruction
}
