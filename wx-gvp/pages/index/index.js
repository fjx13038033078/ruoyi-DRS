// index.js
const app = getApp()
const authApi = require('../../api/auth')

Page({
  data: {
    userInfo: null,
    isLogin: false
  },

  onLoad() {
    // 检查登录状态
    this.checkLoginStatus()
  },

  onShow() {
    // 每次显示页面时检查登录状态
    this.checkLoginStatus()
  },

  // 检查登录状态
  checkLoginStatus() {
    const isLogin = app.checkLoginStatus()
    
    if (!isLogin) {
      // 未登录，跳转到登录页
      wx.reLaunch({
        url: '/pages/login/login'
      })
      return
    }

    // 已登录，获取用户信息
    const userInfo = app.getUserInfo()
    this.setData({
      isLogin: true,
      userInfo: userInfo
    })
  },

  // 切换tab
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    const tabMap = {
      'medical-record': '/pages/medical-record/medical-record',
      'drug-info': '/pages/drug-info/drug-info',
      'medication-check': '/pages/medication-check/medication-check',
      'adverse-reaction': '/pages/adverse-reaction/adverse-reaction'
    }
    
    const url = tabMap[tab]
    if (url) {
      wx.switchTab({
        url: url
      })
    }
  },

  // 退出登录
  async handleLogout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            // 调用退出接口
            await authApi.logout()
          } catch (error) {
            console.log('退出登录接口调用失败:', error)
          } finally {
            // 使用全局退出登录方法
            app.logout()
          }
        }
      }
    })
  }
})
