// 头像处理工具

// 获取API基础URL
const getBaseUrl = () => {
  const ENV_CONFIG = {
    development: 'http://localhost:8080',
    production: 'https://your-domain.com',
    test: 'https://test-api.your-domain.com'
  }

  const getCurrentEnv = () => {
    try {
      const accountInfo = wx.getAccountInfoSync()
      const envVersion = accountInfo.miniProgram.envVersion
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
    } catch (error) {
      return 'development'
    }
  }

  return ENV_CONFIG[getCurrentEnv()]
}

/**
 * 处理用户头像URL
 * @param {Object} user - 用户对象
 * @returns {Object} - 处理后的用户对象
 */
const processUserAvatar = (user) => {
  if (!user) return user

  const BASE_URL = getBaseUrl()
  
  console.log('处理头像前的用户信息:', JSON.stringify(user))
  
  // 创建新对象，避免修改原对象
  const processedUser = { ...user }
  
  // 如果用户有头像且不为空，添加完整URL
  if (processedUser.avatar && processedUser.avatar !== '') {
    // 如果已经是完整URL，不做处理
    if (processedUser.avatar.startsWith('http://') || processedUser.avatar.startsWith('https://')) {
      console.log('头像已是完整URL:', processedUser.avatar)
      return processedUser
    }
    // 添加BASE_URL前缀
    processedUser.avatar = BASE_URL + processedUser.avatar
    console.log('处理后的头像URL:', processedUser.avatar)
  } else {
    // 如果没有头像，设置为null，让前端显示默认头像
    console.log('用户没有头像，将使用默认头像')
    processedUser.avatar = null
  }

  return processedUser
}

module.exports = {
  processUserAvatar
}

