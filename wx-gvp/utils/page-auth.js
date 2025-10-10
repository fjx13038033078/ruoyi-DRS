// 页面认证工具 - 提供可复用的登录状态检查和认证失败处理

const app = getApp()

/**
 * 页面认证混入对象
 * 使用方法：在页面的 Page() 配置中通过 Object.assign 混入
 * 
 * 示例：
 * Page(Object.assign({
 *   // 原有的页面配置
 * }, pageAuthMixin))
 */
const pageAuthMixin = {
  data: {
    showAuthTip: false, // 是否显示认证失败提示
  },

  /**
   * 检查本地登录状态
   * @returns {boolean} 是否已登录
   */
  checkLoginStatus() {
    const isLogin = app.checkLoginStatus()
    
    if (!isLogin) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        showCancel: false,
        success: () => {
          wx.reLaunch({
            url: '/pages/login/login'
          })
        }
      })
      return false
    }
    return true
  },

  /**
   * 处理 API 请求错误
   * 如果是 401 错误，显示认证失败提示
   * @param {Error} error - 错误对象
   * @returns {boolean} 是否是认证失败错误
   */
  handleApiError(error) {
    console.error('API请求失败:', error)

    // 如果是 401 认证失败，显示认证失败提示
    if (error.code === 401 || error.statusCode === 401) {
      this.setData({
        loading: false,
        showAuthTip: true,
        error: null
      })
      return true
    }

    // 其他错误
    this.setData({
      loading: false,
      error: error.msg || error.message || '网络请求失败，请检查网络连接'
    })
    return false
  },

  /**
   * 处理重新登录
   */
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
  },

  /**
   * 隐藏认证提示
   */
  hideAuthTip() {
    this.setData({
      showAuthTip: false
    })
  }
}

/**
 * 为页面添加认证功能
 * 这个函数会将认证相关的方法和数据混入到页面配置中
 * 
 * @param {Object} pageConfig - 页面配置对象
 * @returns {Object} 混入认证功能后的页面配置
 */
function withAuth(pageConfig) {
  // 合并 data
  const originalData = pageConfig.data || {}
  pageConfig.data = {
    ...originalData,
    ...pageAuthMixin.data
  }

  // 混入方法
  Object.keys(pageAuthMixin).forEach(key => {
    if (key !== 'data' && typeof pageAuthMixin[key] === 'function') {
      // 如果页面已有同名方法，保留原方法
      if (!pageConfig[key]) {
        pageConfig[key] = pageAuthMixin[key]
      }
    }
  })

  return pageConfig
}

/**
 * 创建一个包装过的异步数据加载方法
 * 自动处理认证失败错误
 * 
 * @param {Page} page - 页面实例
 * @param {Function} loadFn - 异步加载函数
 * @returns {Function} 包装后的函数
 */
function withAuthErrorHandler(page, loadFn) {
  return async function(...args) {
    try {
      return await loadFn.apply(page, args)
    } catch (error) {
      // 使用混入的错误处理方法
      if (page.handleApiError) {
        page.handleApiError(error)
      } else {
        console.error('加载数据失败:', error)
      }
      throw error
    }
  }
}

module.exports = {
  pageAuthMixin,
  withAuth,
  withAuthErrorHandler
}

