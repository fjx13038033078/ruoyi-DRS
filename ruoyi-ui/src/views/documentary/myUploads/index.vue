<template>
  <div class="app-container">
    <!-- 我的上传页面 -->
    <div>
      <!-- 页面标题 -->
      <el-row :gutter="20" class="mb-20" style="margin-bottom: 20px; margin-top: 10px">
        <el-col>
          <h2 style="margin: 0; color: #303133;">
            <i class="el-icon-upload2" style="color: #409eff;"></i>
            我的上传
          </h2>
          <p style="color: #909399; font-size: 14px; margin-top: 5px;">查看我上传的纪录片审核状态</p>
        </el-col>
      </el-row>

      <!-- 状态统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #409eff;">
                <i class="el-icon-document"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ totalCount }}</div>
                <div class="stat-label">总上传数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <i class="el-icon-time"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ pendingCount }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #67c23a;">
                <i class="el-icon-circle-check"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ approvedCount }}</div>
                <div class="stat-label">已通过</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #f56c6c;">
                <i class="el-icon-circle-close"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ rejectedCount }}</div>
                <div class="stat-label">未通过</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 纪录片列表 -->
      <el-table
        :data="documentaryList"
        v-loading="loading"
        style="width: 100%"
        border
        stripe
        highlight-current-row>
        <el-table-column label="ID" prop="documentaryId" width="80" align="center"></el-table-column>
        <el-table-column label="封面" prop="coverImageUrl" width="100" align="center">
          <template slot-scope="scope">
            <el-image
              :src="scope.row.coverImageUrl || defaultCover"
              :preview-src-list="[scope.row.coverImageUrl || defaultCover]"
              fit="cover"
              style="width: 60px; height: 80px; border-radius: 4px; cursor: pointer;">
              <div slot="error">
                <el-image :src="defaultCover" fit="cover" style="width: 60px; height: 80px; border-radius: 4px;"></el-image>
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="纪录片名称" prop="documentaryName" min-width="200" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="类型" prop="documentaryType" width="100" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="年份" prop="releaseYear" width="80" align="center"></el-table-column>
        <el-table-column label="审核状态" prop="status" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="warning" size="medium">
              <i class="el-icon-time"></i> 待审核
            </el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="success" size="medium">
              <i class="el-icon-circle-check"></i> 已通过
            </el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="danger" size="medium">
              <i class="el-icon-circle-close"></i> 未通过
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="拒绝理由" prop="rejectReason" width="180" align="center">
          <template slot-scope="scope">
            <el-popover
              v-if="scope.row.status === 3 && scope.row.rejectReason"
              placement="top"
              width="300"
              trigger="hover">
              <div style="word-break: break-word; line-height: 1.6;">{{ scope.row.rejectReason }}</div>
              <span slot="reference" class="reject-reason-text">
                {{ scope.row.rejectReason }}
              </span>
            </el-popover>
            <span v-else style="color: #c0c4cc;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="审核人" prop="reviewBy" width="100" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.reviewBy">{{ scope.row.reviewBy }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="审核时间" prop="reviewTime" width="160" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.reviewTime">{{ formatDateTime(scope.row.reviewTime) }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-view"
              size="mini"
              @click="handleView(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <pagination
      v-show="totalDocumentaries > 0"
      :total="totalDocumentaries"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchDocumentaries"
    />

    <!-- 查看纪录片详情对话框 -->
    <el-dialog
      :visible.sync="dialogVisible"
      title="纪录片详情"
      width="50%"
      @close="handleCloseDialog">
      <div v-if="documentaryDetail">
        <!-- 封面图片预览 -->
        <div style="text-align: center; margin-bottom: 20px;">
          <el-image
            :src="documentaryDetail.coverImageUrl || defaultCover"
            :preview-src-list="[documentaryDetail.coverImageUrl || defaultCover]"
            fit="contain"
            style="max-width: 300px; max-height: 400px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);">
            <div slot="error">
              <el-image :src="defaultCover" fit="contain" style="max-width: 300px; max-height: 400px; border-radius: 8px;"></el-image>
            </div>
          </el-image>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="纪录片名称" :span="2">
            <span style="font-weight: bold;">{{ documentaryDetail.documentaryName }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="类型">
            {{ documentaryDetail.documentaryType || 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="年份">
            {{ documentaryDetail.releaseYear || 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-tag :type="getRatingType(documentaryDetail.rating)" size="medium">
              {{ documentaryDetail.rating ? documentaryDetail.rating.toFixed(1) : 'N/A' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总集数">
            {{ documentaryDetail.totalEpisodes || 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="播放量">
            {{ formatNumber(documentaryDetail.playCount) }}
          </el-descriptions-item>
          <el-descriptions-item label="点赞数">
            {{ formatNumber(documentaryDetail.likeCount) }}
          </el-descriptions-item>
          <el-descriptions-item label="导演/制作人" :span="2">
            {{ documentaryDetail.director || 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="播放时段" :span="2">
            {{ documentaryDetail.broadcastTime || 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="审核状态" :span="2">
            <el-tag v-if="documentaryDetail.status === 1" type="warning">
              <i class="el-icon-time"></i> 待审核
            </el-tag>
            <el-tag v-else-if="documentaryDetail.status === 2" type="success">
              <i class="el-icon-circle-check"></i> 已通过
            </el-tag>
            <el-tag v-else-if="documentaryDetail.status === 3" type="danger">
              <i class="el-icon-circle-close"></i> 未通过
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="documentaryDetail.status === 3 && documentaryDetail.rejectReason" label="拒绝理由" :span="2">
            <el-alert
              :title="documentaryDetail.rejectReason"
              type="error"
              :closable="false"
              show-icon>
            </el-alert>
          </el-descriptions-item>
          <el-descriptions-item v-if="documentaryDetail.reviewBy" label="审核人">
            {{ documentaryDetail.reviewBy }}
          </el-descriptions-item>
          <el-descriptions-item v-if="documentaryDetail.reviewTime" label="审核时间">
            {{ formatDateTime(documentaryDetail.reviewTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="详情页链接" :span="2">
            <el-link v-if="documentaryDetail.detailUrl" :href="documentaryDetail.detailUrl" target="_blank" type="primary">
              <i class="el-icon-link"></i> 点击访问
            </el-link>
            <span v-else>N/A</span>
          </el-descriptions-item>
          <el-descriptions-item label="简介" :span="2">
            <div style="max-height: 200px; overflow-y: auto;">
              {{ documentaryDetail.description || '暂无简介' }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMyUploads,
  getDocumentaryById
} from '@/api/documentary/documentary'
import defaultCoverImage from '@/assets/images/documentary-background.jpg'
import dayjs from 'dayjs'

export default {
  name: 'MyUploads',
  data() {
    return {
      loading: false,
      documentaryList: [],
      dialogVisible: false,
      totalDocumentaries: 0,
      documentaryDetail: null,
      // 兜底图片
      defaultCover: defaultCoverImage,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 统计数据
      totalCount: 0,
      pendingCount: 0,
      approvedCount: 0,
      rejectedCount: 0
    }
  },
  created() {
    this.fetchDocumentaries()
  },
  methods: {
    // 获取我上传的纪录片列表
    fetchDocumentaries() {
      this.loading = true
      getMyUploads(this.queryParams).then(response => {
        this.documentaryList = response.rows
        this.totalDocumentaries = response.total
        // 计算统计数据
        this.calculateStats()
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 计算统计数据
    calculateStats() {
      this.totalCount = this.documentaryList.length
      this.pendingCount = this.documentaryList.filter(d => d.status === 1).length
      this.approvedCount = this.documentaryList.filter(d => d.status === 2).length
      this.rejectedCount = this.documentaryList.filter(d => d.status === 3).length
    },
    // 查看纪录片详情
    handleView(row) {
      this.loading = true
      getDocumentaryById(row.documentaryId).then(response => {
        this.documentaryDetail = response.data
        this.dialogVisible = true
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 关闭对话框
    handleCloseDialog() {
      this.dialogVisible = false
      this.documentaryDetail = null
    },
    // 根据评分返回标签类型
    getRatingType(rating) {
      if (!rating) return 'info'
      if (rating >= 9) return 'success'
      if (rating >= 8) return 'primary'
      if (rating >= 7) return 'warning'
      return 'danger'
    },
    // 格式化数字显示
    formatNumber(num) {
      if (!num) return '0'
      if (num >= 100000000) {
        return (num / 100000000).toFixed(2) + '亿'
      }
      if (num >= 10000) {
        return (num / 10000).toFixed(2) + '万'
      }
      return num.toString()
    },
    // 格式化日期时间
    formatDateTime(datetime) {
      if (!datetime) return ''
      return dayjs(datetime).format('YYYY-MM-DD HH:mm:ss')
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

/* 统计卡片样式 */
.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

/* 表格样式优化 */
::v-deep .el-table th {
  background-color: #f5f7fa;
  font-weight: bold;
}

::v-deep .el-table--striped .el-table__body tr.el-table__row--striped td {
  background: #fafafa;
}

/* 拒绝理由文本样式 */
.reject-reason-text {
  display: inline-block;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #f56c6c;
  cursor: pointer;
  font-size: 13px;
}

.reject-reason-text:hover {
  text-decoration: underline;
}
</style>

