<template>
  <div class="app-container">
    <!-- 我的收藏页面 -->
    <div>
      <!-- 页面标题 -->
      <el-row :gutter="20" class="mb-20" style="margin-bottom: 20px; margin-top: 10px">
        <el-col>
          <h2 style="margin: 0; color: #303133;">
            <i class="el-icon-star-on" style="color: #f56c6c;"></i>
            我的收藏
          </h2>
        </el-col>
      </el-row>

      <!-- 收藏列表 -->
      <el-table
        :data="collectionList"
        v-loading="loading"
        style="width: 100%"
        border
        stripe
        highlight-current-row
        empty-text="暂无收藏，快去收藏喜欢的纪录片吧~">
        <el-table-column label="封面" prop="coverImageUrl" width="100" align="center">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.coverImageUrl"
              :src="scope.row.coverImageUrl"
              :preview-src-list="[scope.row.coverImageUrl]"
              fit="cover"
              style="width: 60px; height: 80px; border-radius: 4px; cursor: pointer;">
              <div slot="error" style="display: flex; justify-content: center; align-items: center; width: 60px; height: 80px; background: #f5f7fa; color: #909399; font-size: 12px;">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div v-else style="display: flex; justify-content: center; align-items: center; width: 60px; height: 80px; background: #f5f7fa; color: #909399; font-size: 12px;">
              <i class="el-icon-picture-outline"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="纪录片名称" prop="documentaryName" min-width="250" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <span style="font-weight: bold; color: #409eff; cursor: pointer;" @click="handleViewDetails(scope.row)">
              {{ scope.row.documentaryName }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="收藏时间" prop="actionTime" width="180" align="center">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            {{ formatDateTime(scope.row.actionTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-delete"
              size="mini"
              @click="handleCancelCollection(scope.row)"
              style="color: #f56c6c;">
              取消收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <pagination
      v-show="totalCollections > 0"
      :total="totalCollections"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchCollections"
    />

    <!-- 纪录片详情对话框 -->
    <el-dialog
      :visible.sync="detailDialogVisible"
      title="纪录片详情"
      width="50%"
      @close="handleCloseDialog">
      <div v-if="documentaryDetail">
        <!-- 封面图片预览 -->
        <div v-if="documentaryDetail.coverImageUrl" style="text-align: center; margin-bottom: 20px;">
          <el-image
            :src="documentaryDetail.coverImageUrl"
            :preview-src-list="[documentaryDetail.coverImageUrl]"
            fit="contain"
            style="max-width: 300px; max-height: 400px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);">
            <div slot="error" style="display: flex; justify-content: center; align-items: center; width: 300px; height: 400px; background: #f5f7fa; color: #909399;">
              <i class="el-icon-picture-outline" style="font-size: 50px;"></i>
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
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCollectionsByUserId, cancelCollection } from '@/api/documentary/storeup'
import { getDocumentaryById } from '@/api/documentary/documentary'

export default {
  name: 'Collection',
  data() {
    return {
      loading: false,
      collectionList: [],
      totalCollections: 0,
      detailDialogVisible: false,
      documentaryDetail: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  computed: {
    // 获取当前登录用户ID
    currentUserId() {
      return this.$store.state.user.id
    }
  },
  created() {
    this.fetchCollections()
  },
  methods: {
    // 获取收藏列表
    fetchCollections() {
      this.loading = true
      getCollectionsByUserId(this.currentUserId, this.queryParams).then(response => {
        this.collectionList = response.rows
        this.totalCollections = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 查看纪录片详情
    handleViewDetails(row) {
      this.loading = true
      getDocumentaryById(row.documentaryId).then(response => {
        this.documentaryDetail = response.data
        this.detailDialogVisible = true
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 取消收藏
    handleCancelCollection(row) {
      this.$confirm('确认取消收藏该纪录片吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cancelCollection(this.currentUserId, row.documentaryId).then(() => {
          this.$message.success('取消收藏成功')
          this.fetchCollections()
        })
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    // 关闭对话框
    handleCloseDialog() {
      this.detailDialogVisible = false
      this.documentaryDetail = null
    },
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return 'N/A'
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
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

/* 表格样式优化 */
::v-deep .el-table th {
  background-color: #f5f7fa;
  font-weight: bold;
}

::v-deep .el-table--striped .el-table__body tr.el-table__row--striped td {
  background: #fafafa;
}

/* 对话框样式优化 */
::v-deep .el-dialog__header {
  background-color: #409eff;
  padding: 15px 20px;
}

::v-deep .el-dialog__title {
  color: #ffffff;
  font-weight: bold;
}

::v-deep .el-dialog__headerbtn .el-dialog__close {
  color: #ffffff;
}

::v-deep .el-dialog__headerbtn .el-dialog__close:hover {
  color: #f0f0f0;
}

/* 收藏列表特殊样式 */
::v-deep .el-table__empty-text {
  color: #909399;
  font-size: 14px;
}
</style>

