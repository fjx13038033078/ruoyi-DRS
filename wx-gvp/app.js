// app.js
const { processUserAvatar } = require('./utils/avatar')

App({
  globalData: {
    userInfo: null,
    token: null,
    isLogin: false
  },

  onLaunch() {
    // 应用启动时检查登录状态
    this.checkLoginStatus()
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    console.log('从缓存读取的token:', token)
    console.log('从缓存读取的userInfo:', userInfo)
    
    if (token && userInfo) {
      this.globalData.token = token
      // 确保头像URL已处理
      this.globalData.userInfo = processUserAvatar(userInfo)
      this.globalData.isLogin = true
      console.log('设置到globalData的userInfo:', this.globalData.userInfo)
    } else {
      this.globalData.isLogin = false
    }
    
    return this.globalData.isLogin
  },

  // 设置登录状态
  setLoginStatus(token, userInfo) {
    this.globalData.token = token
    // 处理用户头像URL
    this.globalData.userInfo = processUserAvatar(userInfo)
    this.globalData.isLogin = true
    
    // 持久化存储
    wx.setStorageSync('token', token)
    wx.setStorageSync('userInfo', this.globalData.userInfo)
  },

  // 清除登录状态
  clearLoginStatus() {
    this.globalData.token = null
    this.globalData.userInfo = null
    this.globalData.isLogin = false
    
    // 清除存储
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('userRoles')
  },

  // 获取用户信息
  getUserInfo() {
    return this.globalData.userInfo
  },

  // 获取token
  getToken() {
    return this.globalData.token
  },

  // 全局退出登录方法
  logout() {
    // 清除全局状态
    this.clearLoginStatus()
    
    // 显示退出成功提示
    wx.showToast({
      title: '已退出登录',
      icon: 'success',
      duration: 1500
    })
    
    // 延迟跳转到登录页
    setTimeout(() => {
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }, 1500)
  },

  // 处理401认证失败
  handleAuthFailure() {
    wx.showModal({
      title: '登录已过期',
      content: '您的登录已过期，需要重新登录',
      showCancel: false,
      confirmText: '重新登录',
      success: () => {
        this.logout()
      }
    })
  },

  // 返回首页
  goHome() {
    wx.reLaunch({
      url: '/pages/index/index'
    })
  }
})
