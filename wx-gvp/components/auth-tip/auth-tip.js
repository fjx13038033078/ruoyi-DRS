// components/auth-tip/auth-tip.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    show: {
      type: Boolean,
      value: false
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    handleReLogin() {
      this.triggerEvent('relogin')
    }
  }
})

