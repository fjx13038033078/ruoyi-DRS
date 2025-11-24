<template>
  <div class="app-container">
    <!-- 纪录片管理页面 -->
    <div>
      <!-- 操作按钮区域 -->
      <el-row :gutter="20" class="mb-20" style="margin-bottom: 10px; margin-top: 10px">
        <el-col>
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="medium"
            @click="handleAddDocumentary"
            v-hasPermi="['documentary:documentary:add']">
            新增纪录片
          </el-button>
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
        <el-table-column label="纪录片名称" prop="documentaryName" min-width="200" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="类型" prop="documentaryType" width="120" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="年份" prop="releaseYear" width="100" align="center"></el-table-column>
        <el-table-column label="评分" prop="rating" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getRatingType(scope.row.rating)" size="medium">
              {{ scope.row.rating ? scope.row.rating.toFixed(1) : 'N/A' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="播放量" prop="playCount" width="120" align="center">
          <template slot-scope="scope">
            {{ formatNumber(scope.row.playCount) }}
          </template>
        </el-table-column>
        <el-table-column label="点赞数" prop="likeCount" width="120" align="center">
          <template slot-scope="scope">
            {{ formatNumber(scope.row.likeCount) }}
          </template>
        </el-table-column>
        <el-table-column label="导演/制作人" prop="director" width="150" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="播放时段" prop="broadcastTime" width="120" align="center"></el-table-column>
        <el-table-column label="操作" align="center" width="230" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-view"
              size="mini"
              @click="handleView(scope.row)"
              v-hasPermi="['documentary:documentary:view']">
              查看
            </el-button>
            <el-button
              type="text"
              icon="el-icon-edit"
              size="mini"
              @click="handleEdit(scope.row)"
              v-hasPermi="['documentary:documentary:edit']">
              编辑
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

    <!-- 添加/编辑/查看纪录片对话框 -->
    <el-dialog
      :visible.sync="dialogVisible"
      :title="dialogTitle"
      width="50%"
      @close="handleCloseDialog"
      :close-on-click-modal="false">
      <!-- 对话框内容 -->
      <div>
        <el-form :model="documentaryForm" :rules="formRules" ref="documentaryForm" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="纪录片名称" prop="documentaryName">
                <el-input v-model="documentaryForm.documentaryName" :disabled="isReadOnly" placeholder="请输入纪录片名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="类型" prop="documentaryType">
                <el-input v-model="documentaryForm.documentaryType" :disabled="isReadOnly" placeholder="请输入类型"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="年份" prop="releaseYear">
                <el-input-number
                  v-model="documentaryForm.releaseYear"
                  :disabled="isReadOnly"
                  :min="1900"
                  :max="2100"
                  controls-position="right"
                  style="width: 100%"
                  placeholder="请输入年份">
                </el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="总集数" prop="totalEpisodes">
                <el-input-number
                  v-model="documentaryForm.totalEpisodes"
                  :disabled="isReadOnly"
                  :min="1"
                  controls-position="right"
                  style="width: 100%"
                  placeholder="请输入总集数">
                </el-input-number>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="评分" prop="rating">
                <el-input-number
                  v-model="documentaryForm.rating"
                  :disabled="isReadOnly"
                  :min="0"
                  :max="10"
                  :precision="1"
                  :step="0.1"
                  controls-position="right"
                  style="width: 100%"
                  placeholder="请输入评分">
                </el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="播放时段" prop="broadcastTime">
                <el-select v-model="documentaryForm.broadcastTime" :disabled="isReadOnly" placeholder="请选择播放时段" style="width: 100%">
                  <el-option label="00:00-06:00" value="00:00-06:00"></el-option>
                  <el-option label="06:00-12:00" value="06:00-12:00"></el-option>
                  <el-option label="12:00-18:00" value="12:00-18:00"></el-option>
                  <el-option label="18:00-24:00" value="18:00-24:00"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="播放量" prop="playCount">
                <el-input-number
                  v-model="documentaryForm.playCount"
                  :disabled="isReadOnly"
                  :min="0"
                  controls-position="right"
                  style="width: 100%"
                  placeholder="请输入播放量">
                </el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="点赞数" prop="likeCount">
                <el-input-number
                  v-model="documentaryForm.likeCount"
                  :disabled="isReadOnly"
                  :min="0"
                  controls-position="right"
                  style="width: 100%"
                  placeholder="请输入点赞数">
                </el-input-number>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="导演/制作人" prop="director">
            <el-input v-model="documentaryForm.director" :disabled="isReadOnly" placeholder="请输入导演/制作人"></el-input>
          </el-form-item>

          <el-form-item label="详情页链接" prop="detailUrl">
            <el-input v-model="documentaryForm.detailUrl" :disabled="isReadOnly" placeholder="请输入详情页链接">
              <template slot="append" v-if="documentaryForm.detailUrl && !isReadOnly">
                <el-button icon="el-icon-link" @click="openUrl(documentaryForm.detailUrl)">访问</el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="简介" prop="description">
            <el-input
              v-model="documentaryForm.description"
              :disabled="isReadOnly"
              type="textarea"
              :rows="4"
              placeholder="请输入纪录片简介"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <!-- 对话框按钮 -->
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button @click="handleCloseDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="!isReadOnly">{{ dialogButtonText }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAllDocumentaries,
  addDocumentary,
  updateDocumentary,
  deleteDocumentary,
  getDocumentaryById
} from '@/api/documentary/documentary'

export default {
  name: 'Documentary',
  data() {
    return {
      loading: false,
      documentaryList: [],
      dialogVisible: false,
      dialogTitle: '',
      dialogButtonText: '',
      totalDocumentaries: 0,
      isReadOnly: false, // 是否只读模式
      documentaryForm: {
        documentaryName: '',
        detailUrl: '',
        playCount: 0,
        documentaryType: '',
        releaseYear: null,
        totalEpisodes: null,
        rating: null,
        likeCount: 0,
        director: '',
        description: '',
        broadcastTime: ''
      },
      // 表单验证规则
      formRules: {
        documentaryName: [
          { required: true, message: '请输入纪录片名称', trigger: 'blur' }
        ],
        documentaryType: [
          { required: true, message: '请输入类型', trigger: 'blur' }
        ]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
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
      this.dialogTitle = '查看纪录片'
      this.dialogButtonText = '关闭'
      this.isReadOnly = true
      getDocumentaryById(row.documentaryId).then(response => {
        this.documentaryForm = response.data
        this.dialogVisible = true
      })
    },
    // 新增纪录片
    handleAddDocumentary() {
      this.isReadOnly = false
      this.dialogTitle = '新增纪录片'
      this.dialogButtonText = '添加'
      this.dialogVisible = true
    },
    // 编辑纪录片
    handleEdit(row) {
      this.isReadOnly = false
      this.dialogTitle = '编辑纪录片'
      this.dialogButtonText = '更新'
      this.documentaryForm = Object.assign({}, row)
      this.dialogVisible = true
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
      this.clearForm()
    },
    // 清空表单
    clearForm() {
      this.documentaryForm = {
        documentaryName: '',
        detailUrl: '',
        playCount: 0,
        documentaryType: '',
        releaseYear: null,
        totalEpisodes: null,
        rating: null,
        likeCount: 0,
        director: '',
        description: '',
        broadcastTime: ''
      }
      // 重置表单验证
      if (this.$refs.documentaryForm) {
        this.$refs.documentaryForm.resetFields()
      }
    },
    // 提交表单
    handleSubmit() {
      this.$refs.documentaryForm.validate((valid) => {
        if (valid) {
          if (this.dialogButtonText === '添加') {
            addDocumentary(this.documentaryForm).then(() => {
              this.$message.success('添加成功')
              this.dialogVisible = false
              this.clearForm()
              this.fetchDocumentaries()
            })
          } else if (this.dialogButtonText === '更新') {
            updateDocumentary(this.documentaryForm).then(() => {
              this.$message.success('更新成功')
              this.dialogVisible = false
              this.clearForm()
              this.fetchDocumentaries()
            })
          }
        } else {
          this.$message.error('请填写必填项')
          return false
        }
      })
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
    // 打开链接
    openUrl(url) {
      if (url) {
        window.open(url, '_blank')
      }
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
</style>

