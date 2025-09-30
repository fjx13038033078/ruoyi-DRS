// 网络请求工具

// 环境配置
const ENV_CONFIG = {
  // 开发环境（微信开发者工具中需要开启"不校验合法域名"）
  development: 'http://localhost:8080',

  // 生产环境（需要在微信公众平台配置合法域名）
  production: 'https://your-domain.com',  // 替换为实际的生产环境域名

  // 测试环境
  test: 'https://test-api.your-domain.com'  // 替换为实际的测试环境域名
}

// 获取当前环境
const getCurrentEnv = () => {
  // 在微信开发者工具中，可以通过这种方式判断环境
  const accountInfo = wx.getAccountInfoSync()
  const envVersion = accountInfo.miniProgram.envVersion

  // envVersion: 'develop' | 'trial' | 'release'
  switch (envVersion) {
    case 'develop':
      return 'development'
    case 'trial':
      return 'test'
    case 'release':
      return 'production'
    default:
      return 'development'
  }
}

const BASE_URL = ENV_CONFIG[getCurrentEnv()]

// 请求拦截器
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 从缓存中获取token
    const token = wx.getStorageSync('token')

    const {
      url,
      data = {},
      method = 'GET',
      header = {},
      ...otherOptions
    } = options

    const defaultHeader = {
      'Content-Type': 'application/json',
      ...header
    }

    // 如果有token且不是无需token的请求，则添加Authorization头
    if (token && header.isToken !== false) {
      defaultHeader['Authorization'] = `Bearer ${token}`
    }

    wx.request({
      url: BASE_URL + url,
      data,
      method: method.toUpperCase(),
      header: defaultHeader,
      success: (res) => {
        const { statusCode, data: responseData } = res

        if (statusCode === 200) {
          // 请求成功
          if (responseData.code === 200) {
            resolve(responseData)
          } else {
            // 业务错误
            wx.showToast({
              title: responseData.msg || '请求失败',
              icon: 'none'
            })
            reject(responseData)
          }
        } else if (statusCode === 401) {
          // token过期或无效，调用全局认证失败处理
          const app = getApp()
          if (app && app.handleAuthFailure) {
            app.handleAuthFailure()
          } else {
            // 备用处理方案
            wx.removeStorageSync('token')
            wx.removeStorageSync('userInfo')
            wx.showModal({
              title: '提示',
              content: '登录已过期，请重新登录',
              showCancel: false,
              success: () => {
                wx.reLaunch({
                  url: '/pages/login/login'
                })
              }
            })
          }
          reject(res)
        } else {
          // 其他HTTP错误
          wx.showToast({
            title: `请求失败(${statusCode})`,
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        // 检查是否是域名问题
        if (err.errMsg && err.errMsg.includes('request:fail')) {
          let errorMessage = '网络请求失败'

          if (err.errMsg.includes('不在以下 request 合法域名列表中')) {
            errorMessage = '开发环境请在微信开发者工具中开启"不校验合法域名"选项'
          } else if (err.errMsg.includes('超时')) {
            errorMessage = '请求超时，请检查网络连接'
          } else if (err.errMsg.includes('fail url not in domain list')) {
            errorMessage = '请在微信开发者工具中开启"不校验合法域名"选项'
          }

          wx.showModal({
            title: '网络请求失败',
            content: errorMessage,
            showCancel: false,
            confirmText: '知道了'
          })
        } else {
          wx.showToast({
            title: '网络请求失败',
            icon: 'none'
          })
        }

        reject(err)
      },
      ...otherOptions
    })
  })
}

module.exports = request