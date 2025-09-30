// pages/drug-info/drug-info.js
const app = getApp()
const drugApi = require('../../api/drug')

Page({
  data: {
    searchKeyword: '',
    drugList: [],
    allDrugs: [], // 存储所有药品数据
    loading: false,
    error: null
  },

  onLoad() {
    // 检查登录状态
    this.checkLoginStatus()
    // 加载药品数据
    this.loadDrugList()
  },

  onShow() {
    this.checkLoginStatus()
  },

  onPullDownRefresh() {
    this.loadDrugList().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 检查登录状态
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

  // 加载药品列表
  async loadDrugList() {
    if (!this.checkLoginStatus()) {
      return
    }

    this.setData({ 
      loading: true,
      error: null
    })

    try {
      // 如果还没有加载过所有药品数据，则从后端获取
      if (this.data.allDrugs.length === 0) {
        const res = await drugApi.getAllDrugs()
        
        if (res.code === 200) {
          const drugs = res.rows || res.data || []
          this.setData({
            allDrugs: drugs
          })
        } else {
          throw new Error(res.msg || '获取药品列表失败')
        }
      }

      // 根据分类和搜索条件过滤药品
      this.filterDrugs()

    } catch (error) {
      console.error('加载药品列表失败:', error)
      this.setData({
        loading: false,
        error: error.msg || error.message || '网络请求失败，请检查网络连接'
      })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 过滤药品数据
  filterDrugs() {
    let filteredDrugs = [...this.data.allDrugs]

    // 根据搜索关键词过滤
    if (this.data.searchKeyword) {
      const keyword = this.data.searchKeyword.toLowerCase()
      filteredDrugs = filteredDrugs.filter(drug => 
        (drug.drugName && drug.drugName.toLowerCase().includes(keyword)) ||
        (drug.name && drug.name.toLowerCase().includes(keyword)) ||
        (drug.description && drug.description.toLowerCase().includes(keyword)) ||
        (drug.indications && drug.indications.toLowerCase().includes(keyword)) ||
        (drug.precautions && drug.precautions.toLowerCase().includes(keyword))
      )
    }

    this.setData({
      drugList: filteredDrugs,
      loading: false
    })
  },

  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },

  // 执行搜索
  handleSearch() {
    this.filterDrugs()
  },


  // 查看药品详情
  async viewDrugDetail(e) {
    const drug = e.currentTarget.dataset.drug
    
    try {
      // 获取药品详细信息
      const res = await drugApi.getDrugById(drug.id)
      
      if (res.code === 200) {
        const drugDetail = res.data
        let content = `药品名称：${drugDetail.drugName || drugDetail.name || '未设置'}\n\n`
        content += `描述：${drugDetail.description || '暂无描述'}\n\n`
        content += `适应症：${drugDetail.indications || '暂无适应症信息'}\n\n`
        content += `注意事项：${drugDetail.precautions || '暂无注意事项'}`
        
        wx.showModal({
          title: '药品详情',
          content: content,
          showCancel: false,
          confirmText: '知道了'
        })
      } else {
        throw new Error(res.msg || '获取药品详情失败')
      }
    } catch (error) {
      console.error('获取药品详情失败:', error)
      // 如果获取详情失败，显示基本信息
      let content = `药品名称：${drug.drugName || drug.name || '未设置'}\n\n`
      content += `描述：${drug.description || '暂无描述'}\n\n`
      content += `适应症：${drug.indications || '暂无适应症信息'}\n\n`
      content += `注意事项：${drug.precautions || '暂无注意事项'}`
      
      wx.showModal({
        title: '药品详情',
        content: content,
        showCancel: false,
        confirmText: '知道了'
      })
    }
  },

  // 查看药品说明书
  viewDrugInstruction(e) {
    const drug = e.currentTarget.dataset.drug
    
    // 跳转到说明书页面
    wx.navigateTo({
      url: `/pages/drug-instruction/drug-instruction?drugId=${drug.id}&drugName=${encodeURIComponent(drug.drugName || drug.name || '未知药品')}`
    })
  },

})
