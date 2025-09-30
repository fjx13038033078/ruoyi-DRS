// pages/adverse-reaction/adverse-reaction.js
const app = getApp()
const adverseReactionApi = require('../../api/adverseReaction')
const drugApi = require('../../api/drug')

Page({
  data: {
    // 不良反应记录列表
    reportList: [],
    // 所有药品列表（用于选择）
    drugList: [],
    // 严重程度选项
    severityOptions: [
      { value: '轻微', label: '轻微', color: '#52c41a' },
      { value: '中度', label: '中度', color: '#faad14' },
      { value: '严重', label: '严重', color: '#ff4d4f' }
    ],
    // 当前用户信息
    userInfo: null,
    patientInfo: null,
    // 加载状态
    loading: false,
    error: null,
    // 显示状态
    showAddForm: false,
    // 表单数据
    formData: {
      drugId: null,
      drugName: '',
      description: '',
      severity: '轻微',
      occurTime: ''
    }
  },

  onLoad() {
    // 检查登录状态
    this.checkLoginStatus()
    // 加载数据
    this.loadData()
  },

  onShow() {
    this.checkLoginStatus()
  },

  onPullDownRefresh() {
    this.loadData().finally(() => {
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

  // 加载数据
  async loadData() {
    if (!this.checkLoginStatus()) {
      return
    }

    this.setData({ loading: true, error: null })

    try {
      // 获取用户信息
      const userInfo = app.getUserInfo()
      this.setData({ userInfo })

      // 获取患者信息
      const patientInfo = await this.getPatientInfo()
      this.setData({ patientInfo })

      // 加载不良反应记录
      await this.loadAdverseReactions()

      // 加载药品列表
      await this.loadDrugList()

    } catch (error) {
      console.error('加载数据失败:', error)
      this.setData({
        error: error.msg || error.message || '加载失败，请检查网络连接'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 获取患者信息
  async getPatientInfo() {
    try {
      const patientApi = require('../../api/patient')
      const res = await patientApi.getPatientInfo()
      if (res.code === 200) {
        const patients = res.rows || res.data || []
        return patients.length > 0 ? patients[0] : null
      }
      return null
    } catch (error) {
      console.error('获取患者信息失败:', error)
      return null
    }
  },

  // 加载不良反应记录
  async loadAdverseReactions() {
    try {
      const res = await adverseReactionApi.getAllAdverseReactions()
      if (res.code === 200) {
        const reactions = res.rows || res.data || []
        // 格式化数据
        const formattedReactions = reactions.map(reaction => ({
          ...reaction,
          statusIcon: reaction.doctorConfirmed === 1 ? '✓' : '⏳',
          statusText: reaction.doctorConfirmed === 1 ? '已确认' : '待确认',
          statusColor: reaction.doctorConfirmed === 1 ? '#52c41a' : '#faad14'
        }))
        this.setData({ reportList: formattedReactions })
      }
    } catch (error) {
      console.error('加载不良反应记录失败:', error)
      throw error
    }
  },

  // 加载药品列表
  async loadDrugList() {
    try {
      const res = await drugApi.getAllDrugs()
      if (res.code === 200) {
        const drugs = res.rows || res.data || []
        this.setData({ drugList: drugs })
      }
    } catch (error) {
      console.error('加载药品列表失败:', error)
      throw error
    }
  },

  // 显示添加表单
  showAddForm() {
    // 设置默认发生时间为当前时间，格式：YYYY-MM-DD HH:mm:ss
    const now = new Date()
    const timeString = now.getFullYear() + '-' + 
      String(now.getMonth() + 1).padStart(2, '0') + '-' + 
      String(now.getDate()).padStart(2, '0') + ' ' + 
      String(now.getHours()).padStart(2, '0') + ':' + 
      String(now.getMinutes()).padStart(2, '0') + ':00'
    
    this.setData({
      showAddForm: true,
      'formData.occurTime': timeString
    })
  },

  // 隐藏添加表单
  hideAddForm() {
    this.setData({
      showAddForm: false,
      formData: {
        drugId: null,
        drugName: '',
        description: '',
        severity: '轻微',
        occurTime: ''
      }
    })
  },

  // 选择药品
  selectDrug() {
    if (this.data.drugList.length === 0) {
      wx.showToast({
        title: '暂无药品数据',
        icon: 'none'
      })
      return
    }

    const drugNames = this.data.drugList.map(drug => drug.drugName || drug.name)
    wx.showActionSheet({
      itemList: drugNames,
      success: (res) => {
        const selectedDrug = this.data.drugList[res.tapIndex]
        this.setData({
          'formData.drugId': selectedDrug.id,
          'formData.drugName': selectedDrug.drugName || selectedDrug.name
        })
      }
    })
  },

  // 选择严重程度
  selectSeverity() {
    const severityLabels = this.data.severityOptions.map(option => option.label)
    wx.showActionSheet({
      itemList: severityLabels,
      success: (res) => {
        const selectedSeverity = this.data.severityOptions[res.tapIndex]
        this.setData({
          'formData.severity': selectedSeverity.value
        })
      }
    })
  },

  // 选择发生时间
  selectOccurTime() {
    const now = new Date()
    const currentTime = now.getFullYear() + '-' + 
      String(now.getMonth() + 1).padStart(2, '0') + '-' + 
      String(now.getDate()).padStart(2, '0') + ' ' + 
      String(now.getHours()).padStart(2, '0') + ':' + 
      String(now.getMinutes()).padStart(2, '0') + ':00'
    
    wx.showModal({
      title: '选择发生时间',
      editable: true,
      placeholderText: '请输入发生时间，格式：YYYY-MM-DD HH:mm:ss',
      content: currentTime,
      success: (res) => {
        if (res.confirm && res.content) {
          // 验证时间格式
          const timeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}(:\d{2})?$/
          if (timeRegex.test(res.content)) {
            let formattedTime = res.content
            // 如果没有秒，添加秒
            if (!formattedTime.includes(':')) {
              formattedTime += ':00'
            } else if (formattedTime.split(':').length === 2) {
              formattedTime += ':00'
            }
            this.setData({
              'formData.occurTime': formattedTime
            })
          } else {
            wx.showToast({
              title: '时间格式不正确',
              icon: 'none'
            })
          }
        }
      }
    })
  },

  // 输入反应描述
  onDescriptionInput(e) {
    this.setData({
      'formData.description': e.detail.value
    })
  },

  // 提交不良反应报告
  async submitReport() {
    const { formData, patientInfo } = this.data

    // 验证表单
    if (!formData.drugId) {
      wx.showToast({
        title: '请选择药品',
        icon: 'none'
      })
      return
    }

    if (!formData.description.trim()) {
      wx.showToast({
        title: '请输入反应描述',
        icon: 'none'
      })
      return
    }

    if (!formData.occurTime) {
      wx.showToast({
        title: '请选择发生时间',
        icon: 'none'
      })
      return
    }

    try {
      wx.showLoading({ title: '提交中...' })

      // 格式化时间，确保后端能正确解析
      let formattedTime = formData.occurTime
      // 如果时间格式不包含秒，添加秒
      if (formattedTime && !formattedTime.includes(':')) {
        formattedTime += ':00'
      } else if (formattedTime && formattedTime.split(':').length === 2) {
        formattedTime += ':00'
      }

      const reportData = {
        patientId: patientInfo ? patientInfo.id : null,
        drugId: formData.drugId,
        description: formData.description.trim(),
        severity: formData.severity,
        occurTime: formattedTime,
        doctorConfirmed: 0 // 默认未确认
      }

      console.log('提交的不良反应数据:', reportData)

      const res = await adverseReactionApi.addAdverseReaction(reportData)
      
      if (res.code === 200) {
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        })
        
        // 隐藏表单并重新加载数据
        this.hideAddForm()
        await this.loadAdverseReactions()
      } else {
        throw new Error(res.msg || '提交失败')
      }

    } catch (error) {
      console.error('提交不良反应报告失败:', error)
      wx.showToast({
        title: error.msg || error.message || '提交失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 查看上报详情
  viewReportDetail(e) {
    const report = e.currentTarget.dataset.report
    const content = `药品：${report.drugName || '未知'}\n` +
                   `反应描述：${report.description}\n` +
                   `严重程度：${report.severity}\n` +
                   `发生时间：${report.occurTime}\n` +
                   `状态：${report.statusText}`
    
    wx.showModal({
      title: '不良反应详情',
      content: content,
      showCancel: false,
      confirmText: '知道了'
    })
  },

})
