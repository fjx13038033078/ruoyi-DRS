// 认证相关API
const request = require('../utils/request')

// 登录
const login = (username, password, code = '', uuid = '') => {
  return request({
    url: '/login',
    method: 'POST',
    data: {
      username,
      password,
      code,
      uuid
    },
    header: {
      isToken: false,
      repeatSubmit: false
    }
  })
}

// 获取用户信息
const getUserInfo = () => {
  return request({
    url: '/getInfo',
    method: 'GET'
  })
}

// 退出登录
const logout = () => {
  return request({
    url: '/logout',
    method: 'POST'
  })
}

// 获取验证码
const getCaptcha = () => {
  return request({
    url: '/captchaImage',
    method: 'GET',
    header: {
      isToken: false
    }
  })
}

module.exports = {
  login,
  getUserInfo, 
  logout,
  getCaptcha
}

