<template>
  <div class="app-container">
    <!-- 图表容器 -->
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
            <span><i class="el-icon-pie-chart"></i> 时段分布概览</span>
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
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getYearStatistics, getTimePeriodStatistics } from '@/api/documentary/statistics'

export default {
  name: 'DocumentaryStatistics',
  data() {
    return {
      // 加载状态
      yearLoading: false,
      timePeriodLoading: false,
      // 图表实例
      yearChart: null,
      timePeriodChart: null,
      // 表格数据
      yearTableData: [],
      timePeriodTableData: []
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

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },

    /**
     * 加载所有统计数据
     */
    loadAllStatistics() {
      this.loadYearStatistics()
      this.loadTimePeriodStatistics()
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
     * 窗口大小变化处理
     */
    handleResize() {
      if (this.yearChart) {
        this.yearChart.resize()
      }
      if (this.timePeriodChart) {
        this.timePeriodChart.resize()
      }
    },

    /**
     * 获取标签类型
     */
    getTagType(index) {
      const types = ['', 'success', 'warning', 'danger']
      return types[index % types.length]
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

