<template>
  <div class="app-container">
    <!-- 纪录片审核管理页面 -->
    <div>
      <!-- 页面标题 -->
      <el-row :gutter="20" class="mb-20" style="margin-bottom: 20px; margin-top: 10px">
        <el-col>
          <h2 style="margin: 0; color: #303133;">
            <i class="el-icon-document-checked" style="color: #409eff;"></i>
            纪录片审核管理
          </h2>
          <p style="color: #909399; font-size: 14px; margin-top: 5px;">管理待审核和审核不通过的纪录片</p>
        </el-col>
      </el-row>

      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px" style="margin-bottom: 10px;">
        <el-form-item label="纪录片名" prop="documentaryName">
          <el-input
            v-model="queryParams.documentaryName"
            placeholder="请输入纪录片名称"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="审核状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="选择状态" clearable size="small" style="width: 150px" @change="handleQuery">
            <el-option label="未审核" :value="1"></el-option>
            <el-option label="审核不通过" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

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
        <el-table-column label="纪录片名称" prop="documentaryName" min-width="180" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="类型" prop="documentaryType" width="100" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="年份" prop="releaseYear" width="80" align="center"></el-table-column>
        <el-table-column label="评分" prop="rating" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="getRatingType(scope.row.rating)" size="medium">
              {{ scope.row.rating ? scope.row.rating.toFixed(1) : 'N/A' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" prop="status" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="warning" size="small">未审核</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="danger" size="small">审核不通过</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="拒绝理由" prop="rejectReason" min-width="150" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.rejectReason" style="color: #f56c6c;">{{ scope.row.rejectReason }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="审核人" prop="reviewBy" width="100" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.reviewBy">{{ scope.row.reviewBy }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-view"
              size="mini"
              @click="handleView(scope.row)">
              查看详情
            </el-button>
            <el-button
              type="text"
              icon="el-icon-check"
              size="mini"
              @click="handleApprove(scope.row)"
              v-hasPermi="['documentary:documentary:approve']"
              style="color: #67c23a;">
              审核通过
            </el-button>
            <el-button
              v-if="scope.row.status !== 3"
              type="text"
              icon="el-icon-close"
              size="mini"
              @click="handleReject(scope.row)"
              v-hasPermi="['documentary:documentary:reject']"
              style="color: #e6a23c;">
              审核拒绝
            </el-button>
            <el-button
              type="text"
              icon="el-icon-delete"
              size="mini"
              @click="handleDelete(scope.row)"
              v-hasPermi="['documentary:documentary:delete']"
              style="color: #f56c6c;">
              删除
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
            <el-tag v-if="documentaryDetail.status === 1" type="warning">未审核</el-tag>
            <el-tag v-else-if="documentaryDetail.status === 3" type="danger">审核不通过</el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="documentaryDetail.rejectReason" label="拒绝理由" :span="2">
            <span style="color: #f56c6c;">{{ documentaryDetail.rejectReason }}</span>
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
        <el-button type="success" @click="handleApproveFromDialog" v-hasPermi="['documentary:documentary:approve']">
          <i class="el-icon-check"></i> 审核通过
        </el-button>
        <el-button v-if="documentaryDetail && documentaryDetail.status !== 3" type="warning" @click="handleRejectFromDialog" v-hasPermi="['documentary:documentary:reject']">
          <i class="el-icon-close"></i> 审核拒绝
        </el-button>
      </div>
    </el-dialog>

    <!-- 审核拒绝理由对话框 -->
    <el-dialog
      :visible.sync="rejectDialogVisible"
      title="审核拒绝"
      width="500px"
      @close="handleCloseRejectDialog">
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectForm" label-width="100px">
        <el-form-item label="纪录片名称">
          <span style="font-weight: bold;">{{ rejectForm.documentaryName }}</span>
        </el-form-item>
        <el-form-item label="拒绝理由" prop="rejectReason">
          <el-input
            v-model="rejectForm.rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝理由（必填）"
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rejectDialogVisible = false">取 消</el-button>
        <el-button type="warning" @click="submitReject">确认拒绝</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAllDocumentaries,
  deleteDocumentary,
  getDocumentaryById,
  approveDocumentary,
  rejectDocumentary
} from '@/api/documentary/documentary'
import defaultCoverImage from '@/assets/images/documentary-background.jpg'
import dayjs from 'dayjs'

export default {
  name: 'DocumentaryApprove',
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
        pageSize: 10,
        documentaryName: undefined,
        status: undefined, // 具体状态值
        params: {
          showPendingAndRejected: 'yes' // 显示未审核和审核不通过的
        }
      },
      // 拒绝对话框
      rejectDialogVisible: false,
      rejectForm: {
        documentaryId: null,
        documentaryName: '',
        rejectReason: ''
      },
      rejectRules: {
        rejectReason: [
          { required: true, message: '请输入拒绝理由', trigger: 'blur' },
          { min: 5, message: '拒绝理由至少5个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchDocumentaries()
  },
  methods: {
    // 获取纪录片列表
    fetchDocumentaries() {
      this.loading = true
      // 根据是否选择了具体状态来决定查询条件
      if (this.queryParams.status) {
        // 如果选择了具体状态，则只查询该状态
        this.queryParams.params.showPendingAndRejected = undefined
      } else {
        // 如果没有选择，则查询未审核和审核不通过的
        this.queryParams.params.showPendingAndRejected = 'yes'
      }
      
      listAllDocumentaries(this.queryParams).then(response => {
        this.documentaryList = response.rows
        this.totalDocumentaries = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
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
    // 删除纪录片
    handleDelete(row) {
      this.$confirm('确认删除该纪录片吗？删除后将无法恢复！', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteDocumentary(row.documentaryId).then(() => {
          this.$message.success('删除成功')
          this.fetchDocumentaries()
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    // 关闭对话框
    handleCloseDialog() {
      this.dialogVisible = false
      this.documentaryDetail = null
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1
      this.fetchDocumentaries()
    },
    // 重置按钮操作
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        documentaryName: undefined,
        status: undefined,
        params: {
          showPendingAndRejected: 'yes'
        }
      }
      this.handleQuery()
    },
    // 审核通过
    handleApprove(row) {
      this.$confirm('确认审核通过该纪录片吗？通过后用户即可查看。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        approveDocumentary(row.documentaryId).then(() => {
          this.$message.success('审核通过成功')
          this.fetchDocumentaries()
        })
      }).catch(() => {
        this.$message.info('已取消审核')
      })
    },
    // 打开审核拒绝对话框
    handleReject(row) {
      this.rejectForm = {
        documentaryId: row.documentaryId,
        documentaryName: row.documentaryName,
        rejectReason: ''
      }
      this.rejectDialogVisible = true
    },
    // 提交审核拒绝
    submitReject() {
      this.$refs.rejectForm.validate(valid => {
        if (valid) {
          rejectDocumentary(this.rejectForm.documentaryId, this.rejectForm.rejectReason).then(() => {
            this.$message.success('已标记为审核不通过')
            this.rejectDialogVisible = false
            this.fetchDocumentaries()
          }).catch(() => {
            this.$message.error('操作失败')
          })
        }
      })
    },
    // 关闭拒绝对话框
    handleCloseRejectDialog() {
      this.rejectDialogVisible = false
      if (this.$refs.rejectForm) {
        this.$refs.rejectForm.resetFields()
      }
    },
    // 从详情对话框审核通过
    handleApproveFromDialog() {
      if (this.documentaryDetail) {
        this.handleApprove(this.documentaryDetail)
        this.dialogVisible = false
      }
    },
    // 从详情对话框审核拒绝
    handleRejectFromDialog() {
      if (this.documentaryDetail) {
        this.handleReject(this.documentaryDetail)
        this.dialogVisible = false
      }
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

/* 表格样式优化 */
::v-deep .el-table th {
  background-color: #f5f7fa;
  font-weight: bold;
}

::v-deep .el-table--striped .el-table__body tr.el-table__row--striped td {
  background: #fafafa;
}
</style>
