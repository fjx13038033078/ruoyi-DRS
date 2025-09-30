// pages/debug/debug.js
const patientApi = require('../../api/patient')
const authApi = require('../../api/auth')
const app = getApp()

Page({
  data: {
    isLogin: false,
    token: '',
    tokenDisplay: '',
    userInfo: null,
    userInfoDisplay: '',
    currentEnv: '',
    baseUrl: '',
    logs: []
  },

  onLoad() {
    this.loadDebugInfo()
  },

  onShow() {
    this.loadDebugInfo()
  },

  // 加载调试信息
  loadDebugInfo() {
    try {
      // 获取账号信息
      const accountInfo = wx.getAccountInfoSync()
      const envVersion = accountInfo.miniProgram.envVersion
      
      // 获取环境配置
      const ENV_CONFIG = {
        development: 'http://localhost:8080',
        production: 'https://your-domain.com',
        test: 'https://test-api.your-domain.com'
      }
      
      const getCurrentEnv = () => {
        switch (envVersion) {
          case 'develop': return 'development'
          case 'trial': return 'test'
          case 'release': return 'production'
          default: return 'development'
        }
      }
      
      const currentEnv = getCurrentEnv()
      const baseUrl = ENV_CONFIG[currentEnv]
      
      // 获取登录状态
      const isLogin = app.checkLoginStatus()
      const token = wx.getStorageSync('token') || ''
      const userInfo = wx.getStorageSync('userInfo')
      
      this.setData({
        currentEnv,
        baseUrl,
        isLogin,
        token,
        tokenDisplay: token ? token.substring(0, 30) + '...' : '无',
        userInfo,
        userInfoDisplay: userInfo ? JSON.stringify(userInfo, null, 2) : '无'
      })
      
      this.addLog('调试信息已加载')
      
    } catch (error) {
      this.addLog('加载调试信息失败: ' + error.message)
    }
  },

  // 添加日志
  addLog(content) {
    const time = new Date().toLocaleTimeString()
    const logs = this.data.logs
    logs.unshift({ time, content })
    
    // 只保留最新的20条日志
    if (logs.length > 20) {
      logs.pop()
    }
    
    this.setData({ logs })
    console.log(`[${time}] ${content}`)
  },

  // 解析Token内容
  parseToken() {
    if (!this.data.token) {
      this.addLog('❌ 没有Token，无法解析')
      return
    }
    
    try {
      // JWT Token由三部分组成：header.payload.signature
      const parts = this.data.token.split('.')
      if (parts.length !== 3) {
        this.addLog('❌ Token格式不正确')
        return
      }
      
      // 解析header
      const header = JSON.parse(atob(parts[0]))
      this.addLog('📋 Token Header: ' + JSON.stringify(header))
      
      // 解析payload
      const payload = JSON.parse(atob(parts[1]))
      this.addLog('📋 Token Payload: ' + JSON.stringify(payload))
      
      // 检查过期时间
      if (payload.exp) {
        const expDate = new Date(payload.exp * 1000)
        const now = new Date()
        const isExpired = now > expDate
        this.addLog(`⏰ Token过期时间: ${expDate.toLocaleString()}`)
        this.addLog(`⏰ 当前时间: ${now.toLocaleString()}`)
        this.addLog(`⏰ Token状态: ${isExpired ? '已过期' : '未过期'}`)
      }
      
    } catch (error) {
      this.addLog('❌ Token解析失败: ' + error.message)
    }
  },

  // 测试Token有效性
  async testToken() {
    this.addLog('开始测试Token有效性...')
    
    if (!this.data.token) {
      this.addLog('❌ 没有Token，无法测试')
      return
    }
    
    // 先解析token内容
    this.parseToken()
    
    try {
      const res = await authApi.getUserInfo()
      this.addLog('✅ Token有效，用户信息: ' + JSON.stringify(res))
    } catch (error) {
      this.addLog('❌ Token无效: ' + JSON.stringify(error))
    }
  },

  // 测试患者API
  async testPatientApi() {
    this.addLog('开始测试患者API...')
    
    try {
      const res = await patientApi.getPatientInfo()
      this.addLog('✅ 患者API调用成功: ' + JSON.stringify(res))
    } catch (error) {
      this.addLog('❌ 患者API调用失败: ' + JSON.stringify(error))
      
      // 如果是401错误，提供更详细的诊断
      if (error.code === 401) {
        this.addLog('🔍 401错误诊断:')
        this.addLog('- 检查Token是否过期')
        this.addLog('- 检查用户角色权限')
        this.addLog('- 检查后端认证配置')
      }
    }
  },

  // 测试所有API
  async testAllApis() {
    this.addLog('开始测试所有API...')
    
    // 测试获取用户信息
    try {
      const userRes = await authApi.getUserInfo()
      this.addLog('✅ 获取用户信息成功: ' + JSON.stringify(userRes))
    } catch (error) {
      this.addLog('❌ 获取用户信息失败: ' + JSON.stringify(error))
    }
    
    // 测试患者API
    try {
      const patientRes = await patientApi.getPatientInfo()
      this.addLog('✅ 患者API成功: ' + JSON.stringify(patientRes))
    } catch (error) {
      this.addLog('❌ 患者API失败: ' + JSON.stringify(error))
    }
  },

  // 清除所有数据
  clearAllData() {
    wx.showModal({
      title: '确认清除',
      content: '确定要清除所有数据吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorageSync()
          app.clearLoginStatus()
          this.loadDebugInfo()
          this.addLog('✅ 所有数据已清除')
        }
      }
    })
  },

  // 强制退出登录
  forceLogout() {
    wx.showModal({
      title: '强制退出',
      content: '确定要强制退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
          this.addLog('✅ 已强制退出登录')
        }
      }
    })
  },

  // 清空日志
  clearLogs() {
    this.setData({ logs: [] })
  }
})
