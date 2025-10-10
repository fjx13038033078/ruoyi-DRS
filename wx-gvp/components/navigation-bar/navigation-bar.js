Component({
  options: {
    multipleSlots: true // 在组件定义时的选项中启用多slot支持
  },
  /**
   * 组件的属性列表
   */
  properties: {
    extClass: {
      type: String,
      value: ''
    },
    title: {
      type: String,
      value: ''
    },
    background: {
      type: String,
      value: ''
    },
    color: {
      type: String,
      value: ''
    },
    back: {
      type: Boolean,
      value: true
    },
    loading: {
      type: Boolean,
      value: false
    },
    homeButton: {
      type: Boolean,
      value: false,
    },
    animated: {
      // 显示隐藏的时候opacity动画效果
      type: Boolean,
      value: true
    },
    show: {
      // 显示隐藏导航，隐藏的时候navigation-bar的高度占位还在
      type: Boolean,
      value: true,
      observer: '_showChange'
    },
    // back为true的时候，返回的页面深度
    delta: {
      type: Number,
      value: 1
    },
  },
  /**
   * 组件的初始数据
   */
  data: {
    displayStyle: ''
  },
  lifetimes: {
    attached() {
      try {
        // 获取胶囊按钮位置信息
        const rect = wx.getMenuButtonBoundingClientRect() || { left: 0 }
        
        // 获取系统信息（兼容所有版本）
        const systemInfo = wx.getSystemInfoSync() || {}
        const platform = systemInfo.platform || 'ios'
        const isAndroid = platform === 'android'
        const isDevtools = platform === 'devtools'
        
        // 获取窗口宽度和安全区域
        const windowWidth = systemInfo.windowWidth || 375
        const safeArea = systemInfo.safeArea || {}
        const top = safeArea.top || 0
        
        this.setData({
          ios: !isAndroid,
          innerPaddingRight: `padding-right: ${windowWidth - rect.left}px`,
          leftWidth: `width: ${windowWidth - rect.left}px`,
          safeAreaTop: isDevtools || isAndroid ? `height: calc(var(--height) + ${top}px); padding-top: ${top}px` : ``
        })
      } catch (error) {
        console.error('导航栏初始化失败:', error)
        // 设置默认值
        this.setData({
          ios: true,
          innerPaddingRight: 'padding-right: 87px',
          leftWidth: 'width: 87px',
          safeAreaTop: ''
        })
      }
    },
  },
  /**
   * 组件的方法列表
   */
  methods: {
    _showChange(show) {
      const animated = this.data.animated
      let displayStyle = ''
      if (animated) {
        displayStyle = `opacity: ${show ? '1' : '0'
          };transition:opacity 0.5s;`
      } else {
        displayStyle = `display: ${show ? '' : 'none'}`
      }
      this.setData({
        displayStyle
      })
    },
    back() {
      const data = this.data
      // 对于特定页面，返回按钮应该返回首页
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      
      if (currentPage.route === 'pages/medical-record/medical-record' || 
          currentPage.route === 'pages/drug-info/drug-info' ||
          currentPage.route === 'pages/drug-instruction/drug-instruction' ||
          currentPage.route === 'pages/medication-check/medication-check' ||
          currentPage.route === 'pages/adverse-reaction/adverse-reaction') {
        // 这些页面的返回按钮返回首页
        wx.switchTab({
          url: '/pages/index/index'
        })
      } else if (data.delta) {
        wx.navigateBack({
          delta: data.delta
        })
      }
      this.triggerEvent('back', { delta: data.delta }, {})
    },
    home() {
      // 返回首页
      wx.reLaunch({
        url: '/pages/index/index'
      })
      this.triggerEvent('home', {}, {})
    }
  },
})
