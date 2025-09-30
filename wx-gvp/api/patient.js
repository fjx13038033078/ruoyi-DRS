// 患者相关API
const request = require('../utils/request')

// 获取患者病历信息
const getPatientInfo = () => {
  return request({
    url: '/patient/listAll',
    method: 'GET'
  })
}

// 根据ID获取患者详细信息
const getPatientById = (patientId) => {
  return request({
    url: '/patient/detail',
    method: 'GET',
    data: { patientId }
  })
}

module.exports = {
  getPatientInfo,
  getPatientById
}
