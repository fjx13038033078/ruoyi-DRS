// pages/login/login.js
const authApi = require('../../api/auth')
const { processUserAvatar } = require('../../utils/avatar')
const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    captcha: '',
    showPassword: false,
    needCaptcha: false,
    captchaImg: '',
    captchaUuid: '',
    loading: false
  },

  onLoad(options) {
    // 检查是否已登录
    this.checkLoginStatus()
    
    // 获取验证码
    this.getCaptcha()
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      // 已登录，跳转到主页
      wx.reLaunch({
        url: '/pages/index/index'
      })
    }
  },

  // 获取验证码
  async getCaptcha() {
    try {
      const res = await authApi.getCaptcha()
      if (res.code === 200) {
        this.setData({
          needCaptcha: res.captchaEnabled || false,
          captchaImg: res.img ? `data:image/jpeg;base64,${res.img}` : '',
          captchaUuid: res.uuid || ''
        })
      }
    } catch (error) {
      console.error('获取验证码失败:', error)
      // 如果获取验证码失败，可能不需要验证码
      this.setData({
        needCaptcha: false
      })
    }
  },

  // 刷新验证码
  refreshCaptcha() {
    this.getCaptcha()
  },

  // 用户名输入
  onUsernameInput(e) {
    this.setData({
      username: e.detail.value.trim()
    })
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    })
  },

  // 验证码输入
  onCaptchaInput(e) {
    this.setData({
      captcha: e.detail.value.trim()
    })
  },

  // 切换密码显示
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    })
  },

  // 表单验证
  validateForm() {
    const { username, password, needCaptcha, captcha } = this.data
    
    if (!username) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none'
      })
      return false
    }

    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      })
      return false
    }

    if (needCaptcha && !captcha) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'none'
      })
      return false
    }

    return true
  },

  // 处理登录
  async handleLogin() {
    if (!this.validateForm()) {
      return
    }

    const { username, password, captcha, captchaUuid, needCaptcha } = this.data

    this.setData({ loading: true })

    try {
      // 调用登录接口
      const loginRes = await authApi.login(
        username, 
        password, 
        needCaptcha ? captcha : '', 
        needCaptcha ? captchaUuid : ''
      )

      if (loginRes.code === 200) {
        // 保存token
        const token = loginRes.token || loginRes.data?.token
        
        if (!token) {
          throw new Error('登录成功但未获取到token')
        }
        
        wx.setStorageSync('token', token)

        // 获取用户信息
        const userInfoRes = await authApi.getUserInfo()
        
        if (userInfoRes.code === 200) {
          // 处理用户头像URL
          const userInfo = processUserAvatar(userInfoRes.user)
          const roles = userInfoRes.roles || []
          
          // 检查用户角色，确保是患者
          if (!roles.includes('patient') && !roles.includes('common')) {
            wx.showModal({
              title: '登录失败',
              content: '此账号不是患者账号，请使用网页端登录',
              showCancel: false
            })
            // 清除token
            wx.removeStorageSync('token')
            this.setData({ loading: false })
            return
          }

          // 保存用户信息
          wx.setStorageSync('userInfo', userInfo)
          wx.setStorageSync('userRoles', roles)

          // 登录成功提示
          wx.showToast({
            title: '登录成功',
            icon: 'success'
          })

          // 跳转到主页
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/index/index'
            })
          }, 1500)

        } else {
          throw new Error(userInfoRes.msg || '获取用户信息失败')
        }
      } else {
        throw new Error(loginRes.msg || '登录失败')
      }

    } catch (error) {
      console.error('登录失败:', error)
      
      // 如果是验证码错误，刷新验证码
      if (error.msg && error.msg.includes('验证码')) {
        this.refreshCaptcha()
        this.setData({ captcha: '' })
      }

      wx.showToast({
        title: error.msg || error.message || '登录失败',
        icon: 'none',
        duration: 2000
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 清除缓存
  clearStorage() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除所有缓存数据吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorageSync()
          wx.showToast({
            title: '缓存已清除',
            icon: 'success'
          })
        }
      }
    })
  },

  // 刷新页面
  refreshPage() {
    wx.showModal({
      title: '刷新页面',
      content: '确定要刷新页面吗？',
      success: (res) => {
        if (res.confirm) {
          // 重新获取验证码
          this.getCaptcha()
          // 清空表单
          this.setData({
            username: '',
            password: '',
            captcha: ''
          })
          wx.showToast({
            title: '页面已刷新',
            icon: 'success'
          })
        }
      }
    })
  },

  // 跳转到调试页面
  goToDebug() {
    wx.navigateTo({
      url: '/pages/debug/debug'
    })
  }
})
