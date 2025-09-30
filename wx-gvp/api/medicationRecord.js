// 用药记录相关API
const request = require('../utils/request')

// 获取所有用药记录列表
const getAllMedicationRecords = () => {
  return request({
    url: '/medicationRecord/listAll',
    method: 'GET'
  })
}

// 根据用药记录ID获取详情
const getMedicationRecordById = (recordId) => {
  return request({
    url: '/medicationRecord/detail',
    method: 'GET',
    data: { recordId }
  })
}

// 根据患者ID获取用药记录列表
const getMedicationRecordsByPatientId = (patientId) => {
  return request({
    url: '/medicationRecord/listByPatientId',
    method: 'GET',
    data: { patientId }
  })
}

// 根据药品ID获取用药记录列表
const getMedicationRecordsByDrugId = (drugId) => {
  return request({
    url: '/medicationRecord/listByDrugId',
    method: 'GET',
    data: { drugId }
  })
}

// 添加用药记录
const addMedicationRecord = (medicationRecord) => {
  return request({
    url: '/medicationRecord/add',
    method: 'POST',
    data: medicationRecord
  })
}

// 更新用药记录
const updateMedicationRecord = (medicationRecord) => {
  return request({
    url: '/medicationRecord/update',
    method: 'POST',
    data: medicationRecord
  })
}

// 删除用药记录
const deleteMedicationRecord = (recordId) => {
  return request({
    url: '/medicationRecord/delete',
    method: 'GET',
    data: { recordId }
  })
}

module.exports = {
  getAllMedicationRecords,
  getMedicationRecordById,
  getMedicationRecordsByPatientId,
  getMedicationRecordsByDrugId,
  addMedicationRecord,
  updateMedicationRecord,
  deleteMedicationRecord
}
