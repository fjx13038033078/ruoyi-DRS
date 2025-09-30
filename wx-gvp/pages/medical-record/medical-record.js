// pages/medical-record/medical-record.js
const app = getApp()
const patientApi = require('../../api/patient')

Page({
  data: {
    patientInfo: null,
    loading: true,
    error: null,
    showAuthTip: false
  },

  onLoad() {
    this.loadPatientInfo()
  },

  onShow() {
    // 每次显示页面时检查登录状态，如果未登录则跳转到登录页
    if (!app.checkLoginStatus()) {
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }
  },

  onPullDownRefresh() {
    this.loadPatientInfo().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 检查登录状态，如果未登录则跳转到登录页
  checkLoginStatus() {
    if (!app.checkLoginStatus()) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      wx.reLaunch({
        url: '/pages/login/login'
      })
      return false
    }
    return true
  },

  // 加载患者信息
  async loadPatientInfo() {
    if (!this.checkLoginStatus()) {
      return
    }

    this.setData({
      loading: true,
      error: null
    })

    try {
      const res = await patientApi.getPatientInfo()

      if (res.code === 200) {
        // 后端返回的是列表，患者登录时应该只返回自己的信息
        const patients = res.rows || res.data || []

        if (patients.length > 0) {
          this.setData({
            patientInfo: patients[0], // 假设患者只对应一条病历
            isEmpty: false,
            loading: false
          })
        } else {
          this.setData({
            patientInfo: null,
            isEmpty: true,
            loading: false
          })
        }
      } else {
        throw new Error(res.msg || '获取病历信息失败')
      }
    } catch (error) {
      console.error('获取病历信息失败:', error)

      // 如果是401认证失败，显示认证失败提示
      if (error.code === 401) {
        this.setData({
          loading: false,
          showAuthTip: true,
          error: null
        })
        return
      }

      this.setData({
        loading: false,
        error: error.msg || error.message || '网络请求失败，请检查网络连接'
      })
    }
  },

  // 重新加载
  retryLoad() {
    this.loadPatientInfo()
  },

  // 退出登录
  handleLogout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
        }
      }
    })
  },

  // 处理重新登录
  handleReLogin() {
    wx.showModal({
      title: '重新登录',
      content: '确定要重新登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除当前状态
          this.setData({
            showAuthTip: false,
            loading: false,
            error: null
          })
          // 调用全局退出登录
          app.logout()
        }
      }
    })
  }
})