<template>
  <div class="app-container">
    <!-- 纪录片管理页面 -->
    <div>
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
        <el-form-item label="类型" prop="documentaryType">
          <el-input
            v-model="queryParams.documentaryType"
            placeholder="请输入类型"
            clearable
            size="small"
            style="width: 150px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="年份" prop="releaseYear">
          <el-date-picker
            v-model="queryParams.releaseYear"
            type="year"
            placeholder="选择年份"
            size="small"
            style="width: 120px"
            value-format="yyyy"
          />
        </el-form-item>
        <el-form-item label="播放时段" prop="broadcastTime">
          <el-select v-model="queryParams.broadcastTime" placeholder="选择时段" clearable size="small" style="width: 150px">
            <el-option label="00:00-06:00" value="00:00-06:00"></el-option>
            <el-option label="06:00-12:00" value="06:00-12:00"></el-option>
            <el-option label="12:00-18:00" value="12:00-18:00"></el-option>
            <el-option label="18:00-24:00" value="18:00-24:00"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 高级搜索（可折叠） -->
      <el-collapse v-model="activeCollapse" style="margin-bottom: 10px;">
        <el-collapse-item title="高级搜索" name="1">
          <el-form :model="queryParams" ref="advancedForm" :inline="true" label-width="100px">
            <el-form-item label="播放量范围">
              <el-input-number
                v-model="queryParams.params.minPlayCount"
                placeholder="最小值"
                size="small"
                :min="0"
                controls-position="right"
                style="width: 130px"
              />
              <span style="margin: 0 5px">-</span>
              <el-input-number
                v-model="queryParams.params.maxPlayCount"
                placeholder="最大值"
                size="small"
                :min="0"
                controls-position="right"
                style="width: 130px"
              />
            </el-form-item>
            <el-form-item label="点赞数范围">
              <el-input-number
                v-model="queryParams.params.minLikeCount"
                placeholder="最小值"
                size="small"
                :min="0"
                controls-position="right"
                style="width: 130px"
              />
              <span style="margin: 0 5px">-</span>
              <el-input-number
                v-model="queryParams.params.maxLikeCount"
                placeholder="最大值"
                size="small"
                :min="0"
                controls-position="right"
                style="width: 130px"
              />
            </el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>

      <!-- 操作按钮区域 -->
      <el-row :gutter="20" class="mb-20" style="margin-bottom: 10px;">
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
        <el-table-column label="用户评分" prop="averageRating" width="100" align="center">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.averageRating"
              disabled
              show-score
              text-color="#ff9900"
              :max="5"
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              style="display: inline-block;">
            </el-rate>
            <div style="font-size: 12px; color: #909399;">
              {{ scope.row.averageRating ? (scope.row.averageRating / 2).toFixed(1) : '暂无' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="评论数" prop="commentCount" width="80" align="center">
          <template slot-scope="scope">
            <el-tag type="info" size="small">{{ scope.row.commentCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="400" fixed="right">
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
              icon="el-icon-star-off"
              size="mini"
              @click="handleCollect(scope.row)"
              style="color: #f56c6c;">
              收藏
            </el-button>
            <el-button
              type="text"
              icon="el-icon-chat-line-round"
              size="mini"
              @click="handleComment(scope.row)"
              style="color: #409eff;">
              评论
            </el-button>
            <el-button
              type="text"
              icon="el-icon-tickets"
              size="mini"
              @click="handleViewComments(scope.row)"
              style="color: #67c23a;">
              查看评论
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
        <!-- 封面图片预览 -->
        <div v-if="documentaryForm.coverImageUrl" style="text-align: center; margin-bottom: 20px;">
          <el-image
            :src="documentaryForm.coverImageUrl"
            :preview-src-list="[documentaryForm.coverImageUrl]"
            fit="contain"
            style="max-width: 300px; max-height: 400px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);">
            <div slot="error" style="display: flex; justify-content: center; align-items: center; width: 300px; height: 400px; background: #f5f7fa; color: #909399;">
              <i class="el-icon-picture-outline" style="font-size: 50px;"></i>
            </div>
          </el-image>
        </div>
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
              <template slot="append" v-if="documentaryForm.detailUrl">
                <el-button icon="el-icon-link" @click="openUrlAndRecord(documentaryForm.detailUrl, documentaryForm.documentaryId)">访问</el-button>
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

          <el-form-item label="封面图片URL" prop="coverImageUrl">
            <el-input v-model="documentaryForm.coverImageUrl" :disabled="isReadOnly" placeholder="请输入封面图片URL"></el-input>
          </el-form-item>

          <el-form-item label="封面图片路径" prop="imagePath">
            <el-input v-model="documentaryForm.imagePath" :disabled="isReadOnly" placeholder="请输入封面图片路径"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <!-- 对话框按钮 -->
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button @click="handleCloseDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="!isReadOnly">{{ dialogButtonText }}</el-button>
      </div>
    </el-dialog>

    <!-- 评论对话框 -->
    <el-dialog
      :visible.sync="commentDialogVisible"
      title="发表评论"
      width="40%"
      @close="handleCloseCommentDialog">
      <el-form :model="commentForm" :rules="commentRules" ref="commentForm" label-width="80px">
        <el-form-item label="评分" prop="rating">
          <el-rate
            v-model="commentForm.rating"
            :max="5"
            show-score
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            score-template="{value} 分">
          </el-rate>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            对应10分制：{{ commentForm.rating ? (commentForm.rating * 2).toFixed(1) : '0.0' }} 分
          </div>
        </el-form-item>
        <el-form-item label="评论内容" prop="content">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入您的评论（选填）"
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="commentDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitComment">提 交</el-button>
      </div>
    </el-dialog>

    <!-- 查看评论对话框 -->
    <el-dialog
      :visible.sync="commentsDialogVisible"
      :title="commentsDialogTitle"
      width="60%"
      @close="handleCloseCommentsDialog">
      <div v-loading="commentsLoading">
        <el-empty v-if="commentsList.length === 0" description="暂无评论"></el-empty>
        <div v-else>
          <el-card v-for="comment in commentsList" :key="comment.commentId" shadow="hover" style="margin-bottom: 15px;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div style="flex: 1;">
                <div style="display: flex; align-items: center; margin-bottom: 10px;">
                  <el-avatar :size="40" style="margin-right: 10px;">
                    {{ comment.nickName ? comment.nickName.charAt(0) : 'U' }}
                  </el-avatar>
                  <div>
                    <div style="font-weight: bold; color: #303133;">{{ comment.nickName || comment.userName || '匿名用户' }}</div>
                    <div style="font-size: 12px; color: #909399;">{{ formatDateTime(comment.createTime) }}</div>
                  </div>
                </div>
                <div v-if="comment.rating" style="margin-bottom: 10px;">
                  <el-rate
                    :value="comment.rating / 2"
                    disabled
                    :max="5"
                    :colors="['#99A9BF', '#F7BA2A', '#FF9900']">
                  </el-rate>
                  <span style="color: #606266; margin-left: 10px; font-weight: bold;">{{ comment.rating.toFixed(1) }} 分</span>
                </div>
                <div v-if="comment.content" style="color: #606266; line-height: 1.6; white-space: pre-wrap;">
                  {{ comment.content }}
                </div>
              </div>
              <div style="text-align: center; min-width: 60px;">
                <el-button
                  type="text"
                  icon="el-icon-thumb"
                  @click="handleLikeComment(comment)"
                  style="font-size: 18px; color: #909399;">
                </el-button>
                <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                  {{ comment.likeCount || 0 }}
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="commentsDialogVisible = false">关 闭</el-button>
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
import { addStoreup } from '@/api/documentary/storeup'
import {
  addComment,
  listCommentsByDocumentary,
  likeComment,
  getAverageRating,
  getCommentCount
} from '@/api/documentary/comment'
import dayjs from 'dayjs'

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
      activeCollapse: [], // 高级搜索折叠面板
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
        broadcastTime: '',
        coverImageUrl: '',
        imagePath: ''
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
        pageSize: 10,
        documentaryName: undefined,
        documentaryType: undefined,
        releaseYear: undefined,
        broadcastTime: undefined,
        status: 2, // 只显示审核通过的纪录片
        params: {
          minPlayCount: undefined,
          maxPlayCount: undefined,
          minLikeCount: undefined,
          maxLikeCount: undefined
        }
      },
      // 评论对话框
      commentDialogVisible: false,
      commentForm: {
        documentaryId: null,
        rating: 0,
        content: ''
      },
      commentRules: {
        rating: [
          { required: true, message: '请给出您的评分', trigger: 'change' }
        ]
      },
      // 查看评论对话框
      commentsDialogVisible: false,
      commentsDialogTitle: '',
      commentsList: [],
      commentsLoading: false,
      currentDocumentaryForComments: null
    }
  },
  computed: {
    // 获取当前登录用户ID
    currentUserId() {
      return this.$store.state.user.id
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
        // 加载每个纪录片的平均评分和评论数
        this.loadDocumentaryRatingsAndCounts()
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 加载纪录片的评分和评论数
    loadDocumentaryRatingsAndCounts() {
      this.documentaryList.forEach(doc => {
        // 获取平均评分
        getAverageRating(doc.documentaryId).then(response => {
          this.$set(doc, 'averageRating', response.data || 0)
        })
        // 获取评论数量
        getCommentCount(doc.documentaryId).then(response => {
          this.$set(doc, 'commentCount', response.data || 0)
        })
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
        // 记录查看行为
        this.recordAction(row.documentaryId, 1)
      })
    },
    // 收藏纪录片
    handleCollect(row) {
      this.$confirm('确认收藏该纪录片吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        // 记录收藏行为
        this.recordAction(row.documentaryId, 3, true)
      }).catch(() => {
        this.$message.info('已取消收藏')
      })
    },
    // 记录用户行为
    recordAction(documentaryId, actionType, showMessage = false) {
      const storeupData = {
        userId: this.currentUserId,
        documentaryId: documentaryId,
        actionType: actionType,
        actionTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
      }
      addStoreup(storeupData).then(response => {
        if (showMessage) {
          // actionType: 1-查看, 2-访问, 3-收藏
          if (actionType === 3) {
            if (response.code === 200) {
              this.$message.success('收藏成功！')
            } else {
              this.$message.warning('您已经收藏过该纪录片了')
            }
          }
        }
      }).catch((error) => {
        if (showMessage && actionType === 3) {
          // 只对收藏操作显示错误信息
          this.$message.warning('您已经收藏过该纪录片了')
        }
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
        broadcastTime: '',
        coverImageUrl: '',
        imagePath: ''
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
    // 打开链接并记录访问行为
    openUrlAndRecord(url, documentaryId) {
      if (url) {
        // 记录访问行为
        this.recordAction(documentaryId, 2)
        // 打开链接
        window.open(url, '_blank')
      }
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
        documentaryType: undefined,
        releaseYear: undefined,
        broadcastTime: undefined,
        status: 2, // 保持只显示审核通过的
        params: {
          minPlayCount: undefined,
          maxPlayCount: undefined,
          minLikeCount: undefined,
          maxLikeCount: undefined
        }
      }
      this.handleQuery()
    },
    // 打开评论对话框
    handleComment(row) {
      this.commentForm = {
        documentaryId: row.documentaryId,
        userId: this.currentUserId,
        rating: 0,
        content: ''
      }
      this.commentDialogVisible = true
    },
    // 提交评论
    submitComment() {
      this.$refs.commentForm.validate(valid => {
        if (valid) {
          // 将5分制评分转换为10分制
          const commentData = {
            ...this.commentForm,
            rating: this.commentForm.rating * 2,
            createBy: this.$store.state.user.name
          }
          
          addComment(commentData).then(response => {
            if (response.code === 200) {
              this.$message.success('评论成功！')
              this.commentDialogVisible = false
              this.fetchDocumentaries() // 刷新列表以更新评分
            } else {
              this.$message.warning(response.msg || '评论失败')
            }
          }).catch(() => {
            this.$message.error('评论失败！')
          })
        }
      })
    },
    // 关闭评论对话框
    handleCloseCommentDialog() {
      this.commentDialogVisible = false
      if (this.$refs.commentForm) {
        this.$refs.commentForm.resetFields()
      }
    },
    // 查看评论
    handleViewComments(row) {
      this.currentDocumentaryForComments = row
      this.commentsDialogTitle = `${row.documentaryName} - 评论列表`
      this.commentsDialogVisible = true
      this.loadComments(row.documentaryId)
    },
    // 加载评论列表
    loadComments(documentaryId) {
      this.commentsLoading = true
      listCommentsByDocumentary(documentaryId).then(response => {
        this.commentsList = response.data || []
        this.commentsLoading = false
      }).catch(() => {
        this.commentsLoading = false
      })
    },
    // 点赞评论
    handleLikeComment(comment) {
      likeComment(comment.commentId).then(() => {
        this.$message.success('点赞成功！')
        comment.likeCount = (comment.likeCount || 0) + 1
      }).catch(() => {
        this.$message.error('点赞失败！')
      })
    },
    // 关闭评论列表对话框
    handleCloseCommentsDialog() {
      this.commentsDialogVisible = false
      this.commentsList = []
      this.currentDocumentaryForComments = null
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

