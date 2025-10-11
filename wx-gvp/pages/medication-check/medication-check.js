// pages/medication-check/medication-check.js
const app = getApp()
const medicationRecordApi = require('../../api/medicationRecord')
const medicationLogApi = require('../../api/medicationLog')
const { withAuth } = require('../../utils/page-auth')

Page(withAuth({
  data: {
    // 当前日期
    todayDate: '',
    // 用药记录列表
    medicationRecords: [],
    // 打卡记录列表
    medicationLogs: [],
    // 当前用户信息
    userInfo: null,
    patientInfo: null,
    // 加载状态
    loading: false,
    error: null,
    // 显示状态
    showAddLogForm: false,
    // 表单数据
    formData: {
      recordId: null,
      drugName: '',
      takeTime: '',
      isTaken: 1
    }
  },

  onLoad() {
    // 检查登录状态
    this.checkLoginStatus()
    // 初始化数据
    this.initData()
    // 加载数据
    this.loadData()
  },

  onShow() {
    // 检查登录状态并重新加载数据
    if (this.checkLoginStatus()) {
      // 如果有错误或显示认证提示，重新加载
      if (this.data.error || this.data.showAuthTip) {
        this.loadData()
      } else {
        // 正常刷新数据
        this.loadData()
      }
    }
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

  // 初始化数据
  initData() {
    const today = new Date()
    const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    
    this.setData({
      todayDate: todayStr
    })
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

      // 加载用药记录
      await this.loadMedicationRecords()

      // 加载打卡记录
      await this.loadMedicationLogs()

    } catch (error) {
      // 使用统一的错误处理
      this.handleApiError(error)
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

  // 加载用药记录
  async loadMedicationRecords() {
    try {
      const res = await medicationRecordApi.getAllMedicationRecords()
      
      if (res.code === 200) {
        const records = res.rows || res.data || []
        // 只显示使用中的用药记录，如果没有status字段则显示所有记录
        const activeRecords = records.filter(record => {
          return record.status === 1 || record.status === undefined || record.status === null
        })
        
        this.setData({ medicationRecords: activeRecords })
      } else {
        this.setData({ medicationRecords: [] })
      }
    } catch (error) {
      console.error('加载用药记录失败:', error)
      this.setData({ medicationRecords: [] })
      throw error
    }
  },

  // 加载打卡记录
  async loadMedicationLogs() {
    try {
      const res = await medicationLogApi.getAllMedicationLogs()
      
      if (res.code === 200) {
        const logs = res.rows || res.data || []
        this.setData({ medicationLogs: logs })
        
        // 将打卡记录按用药记录分组
        this.groupLogsByRecord(logs)
      } else {
        this.setData({ medicationLogs: [] })
      }
    } catch (error) {
      console.error('加载打卡记录失败:', error)
      this.setData({ medicationLogs: [] })
      throw error
    }
  },

  // 将打卡记录按用药记录分组
  groupLogsByRecord(logs) {
    const { medicationRecords } = this.data
    
    // 为每个用药记录添加对应的打卡记录
    const recordsWithLogs = medicationRecords.map(record => {
      const recordLogs = logs.filter(log => log.recordId === record.id)
      return {
        ...record,
        logs: recordLogs,
        showLogs: false // 默认折叠
      }
    })
    
    this.setData({ medicationRecords: recordsWithLogs })
  },

  // 切换打卡记录展开/收起
  toggleLogs(e) {
    const recordId = e.currentTarget.dataset.recordId
    const { medicationRecords } = this.data
    
    const updatedRecords = medicationRecords.map(record => {
      if (record.id === recordId) {
        return {
          ...record,
          showLogs: !record.showLogs
        }
      }
      return record
    })
    
    this.setData({ medicationRecords: updatedRecords })
  },

  // 显示添加打卡表单
  showAddLogForm(e) {
    const record = e.currentTarget.dataset.record
    const now = new Date()
    const timeString = now.getFullYear() + '-' + 
      String(now.getMonth() + 1).padStart(2, '0') + '-' + 
      String(now.getDate()).padStart(2, '0')
    
    this.setData({
      showAddLogForm: true,
      formData: {
        recordId: record.id,
        drugName: record.drugName || '未知药品',
        takeTime: timeString,
        isTaken: 1
      }
    })
  },

  // 隐藏添加打卡表单
  hideAddLogForm() {
    this.setData({
      showAddLogForm: false,
      formData: {
        recordId: null,
        drugName: '',
        takeTime: '',
        isTaken: 1
      }
    })
  },

  // 选择服药状态
  selectTakenStatus() {
    wx.showActionSheet({
      itemList: ['已服药', '未服药'],
      success: (res) => {
        this.setData({
          'formData.isTaken': res.tapIndex === 0 ? 1 : 0
        })
      }
    })
  },

  // 选择服药时间
  selectTakeTime() {
    const now = new Date()
    const currentTime = now.getFullYear() + '-' + 
      String(now.getMonth() + 1).padStart(2, '0') + '-' + 
      String(now.getDate()).padStart(2, '0')
    
    wx.showModal({
      title: '选择服药时间',
      editable: true,
      placeholderText: '请输入服药时间，格式：YYYY-MM-DD',
      content: currentTime,
      success: (res) => {
        if (res.confirm && res.content) {
          // 验证日期格式
          const dateRegex = /^\d{4}-\d{2}-\d{2}$/
          if (dateRegex.test(res.content)) {
            this.setData({
              'formData.takeTime': res.content
            })
          } else {
            wx.showToast({
              title: '日期格式不正确',
              icon: 'none'
            })
          }
        }
      }
    })
  },

  // 提交打卡记录
  async submitLog() {
    const { formData } = this.data

    // 验证表单
    if (!formData.recordId) {
      wx.showToast({
        title: '请选择用药记录',
        icon: 'none'
      })
      return
    }

    if (!formData.takeTime) {
      wx.showToast({
        title: '请选择服药时间',
        icon: 'none'
      })
      return
    }

    try {
      wx.showLoading({ title: '提交中...' })

      const logData = {
        recordId: formData.recordId,
        takeTime: formData.takeTime,
        isTaken: formData.isTaken
      }

      const res = await medicationLogApi.addMedicationLog(logData)
      
      if (res.code === 200) {
        wx.showToast({
          title: '打卡成功',
          icon: 'success'
        })
        
        // 隐藏表单并重新加载数据
        this.hideAddLogForm()
        // 重新加载打卡记录并分组
        await this.loadMedicationLogs()
      } else {
        throw new Error(res.msg || '提交失败')
      }

    } catch (error) {
      console.error('提交打卡记录失败:', error)
      wx.showToast({
        title: error.msg || error.message || '提交失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 查看打卡详情
  viewLogDetail(e) {
    const log = e.currentTarget.dataset.log
    const content = `用药记录：${log.recordId}\n` +
                   `服药时间：${log.takeTime}\n` +
                   `服药状态：${log.isTaken === 1 ? '已服药' : '未服药'}`
    
    wx.showModal({
      title: '打卡详情',
      content: content,
      showCancel: false,
      confirmText: '知道了'
    })
  },

  // 重新加载
  retryLoad() {
    this.loadData()
  }

}))
