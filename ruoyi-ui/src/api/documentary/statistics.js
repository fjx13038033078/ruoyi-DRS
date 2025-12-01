import request from '@/utils/request'

// 获取纪录片发布年份统计
export function getYearStatistics() {
  return request({
    url: '/documentary/statistics/year',
    method: 'get'
  })
}

// 获取用户行为时间分布统计
export function getTimePeriodStatistics() {
  return request({
    url: '/documentary/statistics/timePeriod',
    method: 'get'
  })
}

