<template>
  <div class="app-container">
    <!-- 个人统计区域（仅普通用户可见） -->
    <div v-if="!isAdmin" class="personal-section">
      <el-divider content-position="left">
        <span class="section-title"><i class="el-icon-user"></i> 我的个人统计</span>
      </el-divider>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 我的收藏类型分布饼图 -->
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card personal-card">
            <div slot="header" class="card-header personal-header">
              <span><i class="el-icon-collection-tag"></i> 我的收藏类型分布</span>
              <el-button
                type="text"
                icon="el-icon-refresh"
                @click="loadMyCollectionTypeStatistics"
                :loading="myCollectionTypeLoading"
              >刷新</el-button>
            </div>
            <div
              v-loading="myCollectionTypeLoading"
              element-loading-text="加载中..."
              element-loading-spinner="el-icon-loading"
            >
              <div id="myCollectionTypeChart" :style="{width: '100%', height: '350px'}"></div>
              <div v-if="myCollectionTypeData.length === 0 && !myCollectionTypeLoading" class="empty-tip">
                <el-empty description="暂无收藏数据，快去收藏喜欢的纪录片吧！" :image-size="80"></el-empty>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 我的近30天行为趋势图 -->
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card personal-card">
            <div slot="header" class="card-header personal-header">
              <span><i class="el-icon-data-line"></i> 我的近30天行为趋势</span>
              <el-button
                type="text"
                icon="el-icon-refresh"
                @click="loadMyActionTrend"
                :loading="myActionTrendLoading"
              >刷新</el-button>
            </div>
            <div
              v-loading="myActionTrendLoading"
              element-loading-text="加载中..."
              element-loading-spinner="el-icon-loading"
            >
              <div id="myActionTrendChart" :style="{width: '100%', height: '350px'}"></div>
              <div v-if="myActionTrendData.length === 0 && !myActionTrendLoading" class="empty-tip">
                <el-empty description="暂无行为数据，快去探索纪录片吧！" :image-size="80"></el-empty>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 全局统计区域（仅管理员可见） -->
    <div v-if="isAdmin" class="admin-section">
      <el-divider content-position="left">
        <span class="section-title"><i class="el-icon-s-data"></i> 平台数据统计</span>
      </el-divider>

      <!-- 第一行图表 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 纪录片发布年份趋势图 -->
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="card-header">
              <span><i class="el-icon-trending-up"></i> 纪录片发布年份趋势</span>
            <el-button
              type="text"
              icon="el-icon-refresh"
              @click="loadYearStatistics"
              :loading="yearLoading"
            >刷新</el-button>
          </div>
          <div
            v-loading="yearLoading"
            element-loading-text="加载中..."
            element-loading-spinner="el-icon-loading"
          >
            <div id="yearChart" :style="{width: '100%', height: '400px'}"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 用户行为时间分布图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-time"></i> 用户行为时间分布</span>
            <el-button
              type="text"
              icon="el-icon-refresh"
              @click="loadTimePeriodStatistics"
              :loading="timePeriodLoading"
            >刷新</el-button>
          </div>
          <div
            v-loading="timePeriodLoading"
            element-loading-text="加载中..."
            element-loading-spinner="el-icon-loading"
          >
            <div id="timePeriodChart" :style="{width: '100%', height: '400px'}"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 纪录片类型分布饼图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-pie-chart"></i> 纪录片类型分布</span>
            <el-button
              type="text"
              icon="el-icon-refresh"
              @click="loadTypeStatistics"
              :loading="typeLoading"
            >刷新</el-button>
          </div>
          <div
            v-loading="typeLoading"
            element-loading-text="加载中..."
            element-loading-spinner="el-icon-loading"
          >
            <div id="typeChart" :style="{width: '100%', height: '400px'}"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 用户行为转化漏斗图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-analysis"></i> 用户行为转化漏斗</span>
            <el-button
              type="text"
              icon="el-icon-refresh"
              @click="loadActionFunnelStatistics"
              :loading="funnelLoading"
            >刷新</el-button>
          </div>
          <div
            v-loading="funnelLoading"
            element-loading-text="加载中..."
            element-loading-spinner="el-icon-loading"
          >
            <div id="funnelChart" :style="{width: '100%', height: '400px'}"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据概览 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-document"></i> 年份分布概览</span>
          </div>
          <el-table
            :data="yearTableData"
            stripe
            max-height="300"
            style="width: 100%"
          >
            <el-table-column prop="year" label="年份" width="180" align="center"></el-table-column>
            <el-table-column prop="count" label="纪录片数量" align="center">
              <template slot-scope="scope">
                <el-tag type="success">{{ scope.row.count }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-film"></i> 类型分布概览</span>
          </div>
          <el-table
            :data="typeTableData"
            stripe
            max-height="300"
            style="width: 100%"
          >
            <el-table-column prop="type" label="类型" width="180" align="center"></el-table-column>
            <el-table-column prop="count" label="纪录片数量" align="center">
              <template slot-scope="scope">
                <el-tag :type="getTagType(scope.$index)">{{ scope.row.count }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三行数据概览 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-time"></i> 时段分布概览</span>
          </div>
          <el-table
            :data="timePeriodTableData"
            stripe
            max-height="300"
            style="width: 100%"
          >
            <el-table-column prop="timePeriod" label="时段" width="180" align="center"></el-table-column>
            <el-table-column prop="count" label="行为数量" align="center">
              <template slot-scope="scope">
                <el-tag :type="getTagType(scope.$index)">{{ scope.row.count }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-data-analysis"></i> 行为转化概览</span>
          </div>
          <el-table
            :data="funnelTableData"
            stripe
            max-height="300"
            style="width: 100%"
          >
            <el-table-column prop="actionName" label="行为类型" width="180" align="center"></el-table-column>
            <el-table-column prop="count" label="行为数量" align="center">
              <template slot-scope="scope">
                <el-tag :type="getFunnelTagType(scope.row.actionType)">{{ scope.row.count }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="转化率" align="center">
              <template slot-scope="scope">
                <el-tag type="info">{{ getConversionRate(scope.row) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { 
  getYearStatistics, 
  getTimePeriodStatistics, 
  getTypeStatistics, 
  getActionFunnelStatistics,
  getMyCollectionTypeStatistics,
  getMyActionTrend
} from '@/api/documentary/statistics'
import dayjs from 'dayjs'

export default {
  name: 'DocumentaryStatistics',
  data() {
    return {
      // 加载状态
      yearLoading: false,
      timePeriodLoading: false,
      typeLoading: false,
      funnelLoading: false,
      myCollectionTypeLoading: false,
      myActionTrendLoading: false,
      // 图表实例
      yearChart: null,
      timePeriodChart: null,
      typeChart: null,
      funnelChart: null,
      myCollectionTypeChart: null,
      myActionTrendChart: null,
      // 表格数据
      yearTableData: [],
      timePeriodTableData: [],
      typeTableData: [],
      funnelTableData: [],
      // 个人统计数据
      myCollectionTypeData: [],
      myActionTrendData: []
    }
  },
  computed: {
    // 判断是否为管理员
    isAdmin() {
      const roles = this.$store.state.user.roles || []
      return roles.includes('admin')
    }
  },
  mounted() {
    // 初始化图表
    this.$nextTick(() => {
      this.initCharts()
      // 加载数据
      this.loadAllStatistics()
    })
  },
  beforeDestroy() {
    // 销毁图表实例
    if (this.yearChart) {
      this.yearChart.dispose()
    }
    if (this.timePeriodChart) {
      this.timePeriodChart.dispose()
    }
    if (this.typeChart) {
      this.typeChart.dispose()
    }
    if (this.funnelChart) {
      this.funnelChart.dispose()
    }
    if (this.myCollectionTypeChart) {
      this.myCollectionTypeChart.dispose()
    }
    if (this.myActionTrendChart) {
      this.myActionTrendChart.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    /**
     * 初始化图表实例
     */
    initCharts() {
      // 管理员：初始化平台统计图表
      if (this.isAdmin) {
        // 初始化年份趋势图
        const yearChartDom = document.getElementById('yearChart')
        if (yearChartDom) {
          this.yearChart = echarts.init(yearChartDom)
        }

        // 初始化时段分布图
        const timePeriodChartDom = document.getElementById('timePeriodChart')
        if (timePeriodChartDom) {
          this.timePeriodChart = echarts.init(timePeriodChartDom)
        }

        // 初始化类型分布饼图
        const typeChartDom = document.getElementById('typeChart')
        if (typeChartDom) {
          this.typeChart = echarts.init(typeChartDom)
        }

        // 初始化漏斗图
        const funnelChartDom = document.getElementById('funnelChart')
        if (funnelChartDom) {
          this.funnelChart = echarts.init(funnelChartDom)
        }
      }

      // 普通用户：初始化个人统计图表
      if (!this.isAdmin) {
        const myCollectionTypeChartDom = document.getElementById('myCollectionTypeChart')
        if (myCollectionTypeChartDom) {
          this.myCollectionTypeChart = echarts.init(myCollectionTypeChartDom)
        }

        const myActionTrendChartDom = document.getElementById('myActionTrendChart')
        if (myActionTrendChartDom) {
          this.myActionTrendChart = echarts.init(myActionTrendChartDom)
        }
      }

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },

    /**
     * 加载所有统计数据
     */
    loadAllStatistics() {
      // 管理员加载平台统计
      if (this.isAdmin) {
        this.loadYearStatistics()
        this.loadTimePeriodStatistics()
        this.loadTypeStatistics()
        this.loadActionFunnelStatistics()
      }
      
      // 普通用户加载个人统计
      if (!this.isAdmin) {
        this.loadMyCollectionTypeStatistics()
        this.loadMyActionTrend()
      }
    },

    /**
     * 加载我的收藏类型分布
     */
    loadMyCollectionTypeStatistics() {
      this.myCollectionTypeLoading = true
      getMyCollectionTypeStatistics()
        .then(response => {
          const data = response.data || []
          this.myCollectionTypeData = data

          if (data.length === 0) {
            this.myCollectionTypeLoading = false
            return
          }

          // 准备饼图数据
          const pieData = data.map(item => ({
            name: item.type,
            value: item.count
          }))

          // 配置饼图
          const option = {
            title: {
              text: '我的收藏偏好',
              left: 'center',
              top: 10,
              textStyle: {
                fontSize: 14,
                fontWeight: 'normal',
                color: '#606266'
              }
            },
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} 部 ({d}%)'
            },
            legend: {
              type: 'scroll',
              orient: 'vertical',
              right: 10,
              top: 50,
              bottom: 20,
              textStyle: {
                fontSize: 11
              }
            },
            series: [
              {
                name: '收藏类型',
                type: 'pie',
                radius: ['30%', '60%'],
                center: ['40%', '55%'],
                roseType: 'radius',  // 南丁格尔玫瑰图
                itemStyle: {
                  borderRadius: 6,
                  borderColor: '#fff',
                  borderWidth: 2
                },
                label: {
                  show: true,
                  formatter: '{b}\n{d}%',
                  fontSize: 10
                },
                emphasis: {
                  label: {
                    show: true,
                    fontSize: 12,
                    fontWeight: 'bold'
                  }
                },
                data: pieData,
                color: [
                  '#ff6b6b', '#feca57', '#48dbfb', '#ff9ff3', '#54a0ff',
                  '#5f27cd', '#00d2d3', '#1dd1a1', '#ff9f43', '#ee5253'
                ]
              }
            ]
          }

          if (this.myCollectionTypeChart) {
            this.myCollectionTypeChart.setOption(option)
          }
          this.myCollectionTypeLoading = false
        })
        .catch(error => {
          console.error('加载收藏类型统计失败:', error)
          this.myCollectionTypeLoading = false
        })
    },

    /**
     * 加载我的近30天行为趋势
     */
    loadMyActionTrend() {
      this.myActionTrendLoading = true
      getMyActionTrend()
        .then(response => {
          const data = response.data || []
          this.myActionTrendData = data

          if (data.length === 0) {
            this.myActionTrendLoading = false
            return
          }

          // 准备数据
          const dates = data.map(item => dayjs(item.actionDate).format('MM-DD'))
          const viewCounts = data.map(item => item.viewCount || 0)
          const accessCounts = data.map(item => item.accessCount || 0)
          const collectCounts = data.map(item => item.collectCount || 0)

          // 配置折线图
          const option = {
            title: {
              text: '我的活跃度趋势',
              left: 'center',
              top: 10,
              textStyle: {
                fontSize: 14,
                fontWeight: 'normal',
                color: '#606266'
              }
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross'
              }
            },
            legend: {
              data: ['查看', '访问', '收藏'],
              bottom: 5
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '15%',
              top: '20%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              boundaryGap: false,
              data: dates,
              axisLabel: {
                rotate: 45,
                fontSize: 10
              }
            },
            yAxis: {
              type: 'value',
              name: '次数',
              minInterval: 1
            },
            series: [
              {
                name: '查看',
                type: 'line',
                data: viewCounts,
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                lineStyle: {
                  width: 2,
                  color: '#409EFF'
                },
                itemStyle: {
                  color: '#409EFF'
                },
                areaStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                      { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                      { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                    ]
                  }
                }
              },
              {
                name: '访问',
                type: 'line',
                data: accessCounts,
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                lineStyle: {
                  width: 2,
                  color: '#E6A23C'
                },
                itemStyle: {
                  color: '#E6A23C'
                },
                areaStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                      { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
                      { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
                    ]
                  }
                }
              },
              {
                name: '收藏',
                type: 'line',
                data: collectCounts,
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                lineStyle: {
                  width: 2,
                  color: '#F56C6C'
                },
                itemStyle: {
                  color: '#F56C6C'
                },
                areaStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                      { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
                      { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
                    ]
                  }
                }
              }
            ]
          }

          if (this.myActionTrendChart) {
            this.myActionTrendChart.setOption(option)
          }
          this.myActionTrendLoading = false
        })
        .catch(error => {
          console.error('加载行为趋势失败:', error)
          this.myActionTrendLoading = false
        })
    },

    /**
     * 加载年份统计数据
     */
    loadYearStatistics() {
      this.yearLoading = true
      getYearStatistics()
        .then(response => {
          const data = response.data || []
          this.yearTableData = data

          // 准备图表数据
          const years = data.map(item => item.year)
          const counts = data.map(item => item.count)

          // 配置图表
          const option = {
            title: {
              text: '纪录片发布趋势',
              left: 'center',
              textStyle: {
                fontSize: 16,
                fontWeight: 'normal'
              }
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              },
              formatter: '{b}年<br/>发布数量: {c} 部'
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              data: years,
              axisLabel: {
                rotate: 45
              },
              name: '年份'
            },
            yAxis: {
              type: 'value',
              name: '数量'
            },
            series: [
              {
                name: '纪录片数量',
                type: 'line',
                data: counts,
                smooth: true,
                lineStyle: {
                  width: 3,
                  color: '#409EFF'
                },
                itemStyle: {
                  color: '#409EFF'
                },
                areaStyle: {
                  color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 1,
                    colorStops: [
                      {
                        offset: 0,
                        color: 'rgba(64, 158, 255, 0.3)'
                      },
                      {
                        offset: 1,
                        color: 'rgba(64, 158, 255, 0.05)'
                      }
                    ]
                  }
                }
              }
            ]
          }

          if (this.yearChart) {
            this.yearChart.setOption(option)
          }
          this.yearLoading = false
        })
        .catch(error => {
          console.error('加载年份统计失败:', error)
          this.$message.error('加载年份统计失败')
          this.yearLoading = false
        })
    },

    /**
     * 加载时段统计数据
     */
    loadTimePeriodStatistics() {
      this.timePeriodLoading = true
      getTimePeriodStatistics()
        .then(response => {
          const data = response.data || []
          this.timePeriodTableData = data

          // 准备图表数据
          const timePeriods = data.map(item => item.timePeriod)
          const counts = data.map(item => item.count)

          // 配置图表
          const option = {
            title: {
              text: '用户行为时段分布',
              left: 'center',
              textStyle: {
                fontSize: 16,
                fontWeight: 'normal'
              }
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              },
              formatter: '{b}<br/>行为数量: {c} 次'
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              data: timePeriods,
              name: '时段'
            },
            yAxis: {
              type: 'value',
              name: '数量'
            },
            series: [
              {
                name: '行为数量',
                type: 'bar',
                data: counts,
                barWidth: '60%',
                itemStyle: {
                  color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 1,
                    colorStops: [
                      {
                        offset: 0,
                        color: '#67C23A'
                      },
                      {
                        offset: 1,
                        color: '#85CE61'
                      }
                    ]
                  },
                  borderRadius: [5, 5, 0, 0]
                },
                label: {
                  show: true,
                  position: 'top',
                  formatter: '{c}'
                }
              }
            ]
          }

          if (this.timePeriodChart) {
            this.timePeriodChart.setOption(option)
          }
          this.timePeriodLoading = false
        })
        .catch(error => {
          console.error('加载时段统计失败:', error)
          this.$message.error('加载时段统计失败')
          this.timePeriodLoading = false
        })
    },

    /**
     * 加载类型统计数据
     */
    loadTypeStatistics() {
      this.typeLoading = true
      getTypeStatistics()
        .then(response => {
          const data = response.data || []
          this.typeTableData = data

          // 准备饼图数据（只显示前10个类型，其余归类到"其他"）
          let pieData = []
          if (data.length <= 10) {
            pieData = data.map(item => ({
              name: item.type,
              value: item.count
            }))
          } else {
            const top10 = data.slice(0, 10)
            pieData = top10.map(item => ({
              name: item.type,
              value: item.count
            }))
            
            const others = data.slice(10)
            const othersCount = others.reduce((sum, item) => sum + item.count, 0)
            
            if (othersCount > 0) {
              pieData.push({
                name: '其他 (' + others.length + '种类型)',
                value: othersCount
              })
            }
          }

          // 配置饼图
          const option = {
            title: {
              text: '纪录片类型分布',
              left: 'center',
              textStyle: {
                fontSize: 16,
                fontWeight: 'normal'
              }
            },
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} 部 ({d}%)'
            },
            legend: {
              type: 'scroll',
              orient: 'vertical',
              right: 10,
              top: 60,
              bottom: 20,
              textStyle: {
                fontSize: 12
              }
            },
            series: [
              {
                name: '纪录片类型',
                type: 'pie',
                radius: ['35%', '65%'],
                center: ['40%', '55%'],
                avoidLabelOverlap: true,
                itemStyle: {
                  borderRadius: 8,
                  borderColor: '#fff',
                  borderWidth: 2
                },
                label: {
                  show: true,
                  formatter: '{b}\n{d}%',
                  fontSize: 11
                },
                emphasis: {
                  label: {
                    show: true,
                    fontSize: 14,
                    fontWeight: 'bold'
                  },
                  itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                },
                data: pieData,
                color: [
                  '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
                  '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#48b8d0',
                  '#95a5a6'
                ]
              }
            ]
          }

          if (this.typeChart) {
            this.typeChart.setOption(option)
          }
          this.typeLoading = false
        })
        .catch(error => {
          console.error('加载类型统计失败:', error)
          this.$message.error('加载类型统计失败')
          this.typeLoading = false
        })
    },

    /**
     * 加载行为漏斗统计数据
     */
    loadActionFunnelStatistics() {
      this.funnelLoading = true
      getActionFunnelStatistics()
        .then(response => {
          const data = response.data || []
          this.funnelTableData = data

          const funnelData = data.map(item => ({
            name: item.actionName,
            value: item.count
          }))

          const option = {
            title: {
              text: '用户行为转化漏斗',
              left: 'center',
              textStyle: {
                fontSize: 16,
                fontWeight: 'normal'
              }
            },
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} 次'
            },
            legend: {
              data: funnelData.map(item => item.name),
              bottom: 10
            },
            series: [
              {
                name: '用户行为',
                type: 'funnel',
                left: '10%',
                top: 60,
                bottom: 60,
                width: '80%',
                min: 0,
                max: Math.max(...funnelData.map(item => item.value), 100),
                minSize: '10%',
                maxSize: '100%',
                sort: 'descending',
                gap: 8,
                label: {
                  show: true,
                  position: 'inside',
                  formatter: '{b}\n{c} 次',
                  fontSize: 13,
                  color: '#fff'
                },
                labelLine: {
                  length: 10,
                  lineStyle: {
                    width: 1,
                    type: 'solid'
                  }
                },
                itemStyle: {
                  borderColor: '#fff',
                  borderWidth: 2
                },
                emphasis: {
                  label: {
                    fontSize: 15,
                    fontWeight: 'bold'
                  }
                },
                data: funnelData,
                color: ['#409EFF', '#E6A23C', '#F56C6C']
              }
            ]
          }

          if (this.funnelChart) {
            this.funnelChart.setOption(option)
          }
          this.funnelLoading = false
        })
        .catch(error => {
          console.error('加载行为漏斗统计失败:', error)
          this.$message.error('加载行为漏斗统计失败')
          this.funnelLoading = false
        })
    },

    /**
     * 窗口大小变化处理
     */
    handleResize() {
      if (this.yearChart) {
        this.yearChart.resize()
      }
      if (this.timePeriodChart) {
        this.timePeriodChart.resize()
      }
      if (this.typeChart) {
        this.typeChart.resize()
      }
      if (this.funnelChart) {
        this.funnelChart.resize()
      }
      if (this.myCollectionTypeChart) {
        this.myCollectionTypeChart.resize()
      }
      if (this.myActionTrendChart) {
        this.myActionTrendChart.resize()
      }
    },

    /**
     * 获取标签类型
     */
    getTagType(index) {
      const types = ['', 'success', 'warning', 'danger']
      return types[index % types.length]
    },

    /**
     * 获取漏斗标签类型
     */
    getFunnelTagType(actionType) {
      const typeMap = {
        1: 'primary',
        2: 'warning',
        3: 'danger'
      }
      return typeMap[actionType] || 'info'
    },

    /**
     * 计算转化率
     */
    getConversionRate(row) {
      if (!this.funnelTableData || this.funnelTableData.length === 0) {
        return '-'
      }
      
      const viewAction = this.funnelTableData.find(item => item.actionType === 1)
      if (!viewAction || viewAction.count === 0) {
        return '100%'
      }
      
      const rate = (row.count / viewAction.count * 100).toFixed(1)
      return rate + '%'
    }
  }
}
</script>

<style scoped>
.chart-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.chart-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.card-header i {
  margin-right: 8px;
  color: #409EFF;
}

/* 个人统计区域样式 */
.personal-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.section-title i {
  margin-right: 8px;
  color: #409EFF;
}

.personal-card {
  background: linear-gradient(135deg, #fff 0%, #f0f9ff 100%);
}

.personal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #fff !important;
}

.personal-header span {
  color: #fff;
}

.personal-header i {
  color: #fff !important;
}

.personal-header .el-button {
  color: #fff !important;
}

.empty-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

::v-deep .el-card__header {
  background-color: #f5f7fa;
  border-bottom: 2px solid #409EFF;
}

::v-deep .personal-card .el-card__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: none;
}

::v-deep .el-table th {
  background-color: #f5f7fa !important;
}

::v-deep .el-divider__text {
  background-color: #fff;
  padding: 0 20px;
}
</style>
