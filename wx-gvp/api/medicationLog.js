// 用药打卡相关API
const request = require('../utils/request')

// 获取所有服药打卡记录
const getAllMedicationLogs = () => {
  return request({
    url: '/medicationLog/listAll',
    method: 'GET'
  })
}

// 根据打卡ID获取记录
const getMedicationLogById = (logId) => {
  return request({
    url: '/medicationLog/detail',
    method: 'GET',
    data: { logId }
  })
}

// 根据用药记录ID获取打卡记录列表
const getMedicationLogsByRecordId = (recordId) => {
  return request({
    url: '/medicationLog/listByRecordId',
    method: 'GET',
    data: { recordId }
  })
}

// 添加打卡记录
const addMedicationLog = (medicationLog) => {
  return request({
    url: '/medicationLog/add',
    method: 'POST',
    data: medicationLog
  })
}

// 更新打卡记录
const updateMedicationLog = (medicationLog) => {
  return request({
    url: '/medicationLog/update',
    method: 'POST',
    data: medicationLog
  })
}

// 删除打卡记录
const deleteMedicationLog = (logId) => {
  return request({
    url: '/medicationLog/delete',
    method: 'GET',
    data: { logId }
  })
}

module.exports = {
  getAllMedicationLogs,
  getMedicationLogById,
  getMedicationLogsByRecordId,
  addMedicationLog,
  updateMedicationLog,
  deleteMedicationLog
}
