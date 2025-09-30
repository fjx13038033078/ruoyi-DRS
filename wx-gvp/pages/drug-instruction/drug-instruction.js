// pages/drug-instruction/drug-instruction.js
const app = getApp()
const drugApi = require('../../api/drug')

Page({
  data: {
    drugId: null,
    drugName: '',
    instruction: null,
    loading: true,
    error: null
  },

  onLoad(options) {
    // 获取传递的参数
    const { drugId, drugName } = options
    if (drugId) {
      // 解码药品名称（处理URL编码）
      const decodedDrugName = drugName ? decodeURIComponent(drugName) : '未知药品'
      this.setData({
        drugId: drugId,
        drugName: decodedDrugName
      })
      this.loadInstruction()
    } else {
      this.setData({
        loading: false,
        error: '缺少药品ID参数'
      })
    }
  },

  // 加载说明书
  async loadInstruction() {
    if (!this.data.drugId) {
      return
    }

    this.setData({ 
      loading: true,
      error: null
    })

    try {
      const res = await drugApi.getDrugInstruction(this.data.drugId)
      
      if (res.code === 200) {
        const instructions = res.rows || res.data || []
        
        if (instructions.length > 0) {
          // 取最新的说明书（通常是第一个）
          const instruction = instructions[0]
          this.setData({
            instruction: instruction,
            loading: false
          })
        } else {
          this.setData({
            instruction: null,
            loading: false
          })
        }
      } else {
        throw new Error(res.msg || '获取药品说明书失败')
      }
    } catch (error) {
      console.error('获取药品说明书失败:', error)
      this.setData({
        loading: false,
        error: error.msg || error.message || '网络请求失败，请检查网络连接'
      })
    }
  }
})