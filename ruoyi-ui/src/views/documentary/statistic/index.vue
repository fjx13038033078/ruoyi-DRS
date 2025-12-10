<template>
  <div class="app-container">
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

    <!-- 第二行图表（新增） -->
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
</template>

<script>
import * as echarts from 'echarts'
import { getYearStatistics, getTimePeriodStatistics, getTypeStatistics, getActionFunnelStatistics } from '@/api/documentary/statistics'

export default {
  name: 'DocumentaryStatistics',
  data() {
    return {
      // 加载状态
      yearLoading: false,
      timePeriodLoading: false,
      typeLoading: false,
      funnelLoading: false,
      // 图表实例
      yearChart: null,
      timePeriodChart: null,
      typeChart: null,
      funnelChart: null,
      // 表格数据
      yearTableData: [],
      timePeriodTableData: [],
      typeTableData: [],
      funnelTableData: []
    }
  },
  mounted() {
    // 初始化图表
    this.initCharts()
    // 加载数据
    this.loadAllStatistics()
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
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    /**
     * 初始化图表实例
     */
    initCharts() {
      // 初始化年份趋势图
      const yearChartDom = document.getElementById('yearChart')
      this.yearChart = echarts.init(yearChartDom)

      // 初始化时段分布图
      const timePeriodChartDom = document.getElementById('timePeriodChart')
      this.timePeriodChart = echarts.init(timePeriodChartDom)

      // 初始化类型分布饼图
      const typeChartDom = document.getElementById('typeChart')
      this.typeChart = echarts.init(typeChartDom)

      // 初始化漏斗图
      const funnelChartDom = document.getElementById('funnelChart')
      this.funnelChart = echarts.init(funnelChartDom)

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },

    /**
     * 加载所有统计数据
     */
    loadAllStatistics() {
      this.loadYearStatistics()
      this.loadTimePeriodStatistics()
      this.loadTypeStatistics()
      this.loadActionFunnelStatistics()
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

          this.yearChart.setOption(option)
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

          this.timePeriodChart.setOption(option)
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
            // 类型数量不超过10个，全部显示
            pieData = data.map(item => ({
              name: item.type,
              value: item.count
            }))
          } else {
            // 取前10个类型
            const top10 = data.slice(0, 10)
            pieData = top10.map(item => ({
              name: item.type,
              value: item.count
            }))
            
            // 计算剩余类型的总数
            const others = data.slice(10)
            const othersCount = others.reduce((sum, item) => sum + item.count, 0)
            
            // 添加"其他"分类
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
                  '#95a5a6'  // 最后一个颜色用于"其他"分类（灰色）
                ]
              }
            ]
          }

          this.typeChart.setOption(option)
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

          // 准备漏斗图数据
          const funnelData = data.map(item => ({
            name: item.actionName,
            value: item.count
          }))

          // 配置漏斗图
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

          this.funnelChart.setOption(option)
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
        1: 'primary',  // 查看
        2: 'warning',  // 访问
        3: 'danger'    // 收藏
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
      
      // 找到查看行为的数量作为基准
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

::v-deep .el-card__header {
  background-color: #f5f7fa;
  border-bottom: 2px solid #409EFF;
}

::v-deep .el-table th {
  background-color: #f5f7fa !important;
}
</style>
