<template>
  <div>
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-wrapper">
          <i class="el-icon-film header-icon"></i>
          <h1 class="main-title">纪录片个性化推荐系统</h1>
        </div>
        <p class="subtitle">Documentary Recommendation System</p>
      </div>
    </div>
    <div>
      <el-row style="margin-top: 10px;">
        <el-col :span="12">
          <el-card style="margin-right: 20px; height: 420px;">
            <h3 slot="header">通知公告</h3>
            <el-table v-loading="loading" :data="noticeList">
              <el-table-column label="序号" align="center" prop="noticeId" width="100"/>
              <el-table-column
                label="公告标题"
                align="center"
                prop="noticeTitle"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <span @click="showNoticeContent(scope.row)">{{ scope.row.noticeTitle }}</span>
                </template>
              </el-table-column>
              <el-table-column label="公告类型" align="center" prop="noticeType" width="100">
                <template slot-scope="scope">
                  <dict-tag :options="dict.type.sys_notice_type" :value="scope.row.noticeType"/>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" align="center" prop="createTime" width="100">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card style="margin-right: 20px; height: 420px;">
            <h3 slot="header">经典纪录片</h3>
            <div v-loading="classicLoading">
              <el-carousel :interval="5000" arrow="always" height="360px">
                <el-carousel-item v-for="doc in classicDocumentaries" :key="doc.documentaryId">
                  <div class="carousel-item-content" @click="openDocumentaryLink(doc)">
                    <el-image
                      :src="doc.coverImageUrl || defaultCover"
                      fit="cover"
                      class="carousel-image">
                      <div slot="error">
                        <el-image :src="defaultCover" fit="cover" class="carousel-image"></el-image>
                      </div>
                    </el-image>
                    <div class="carousel-overlay">
                      <h3 class="carousel-title">{{ doc.documentaryName }}</h3>
                    </div>
                  </div>
                </el-carousel-item>
                <!-- 如果没有数据，显示默认轮播 -->
                <el-carousel-item v-if="classicDocumentaries.length === 0 && !classicLoading">
                  <div class="carousel-item-content carousel-empty">
                    <el-empty description="暂无经典纪录片" :image-size="100"></el-empty>
                  </div>
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 推荐区域 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card style="margin-right: 20px;">
            <div slot="header" style="display: flex; align-items: center;">
              <i class="el-icon-view" style="font-size: 20px; color: #409eff; margin-right: 10px;"></i>
              <h3 style="margin: 0; color: #303133;">其他人此时在看</h3>
              <span style="margin-left: 10px; font-size: 12px; color: #909399;">基于当前时段的个性化推荐</span>
            </div>
            <div v-loading="watchingNowLoading">
              <el-empty v-if="watchingNowList.length === 0" description="暂无推荐" :image-size="100"></el-empty>
              <div v-else class="recommendation-grid">
                <el-card
                  v-for="doc in watchingNowList"
                  :key="doc.documentaryId"
                  shadow="hover"
                  class="recommendation-card"
                  @click.native="viewDocumentaryDetail(doc)">
                  <div class="card-cover">
                    <el-image
                      :src="doc.coverImageUrl || defaultCover"
                      fit="cover"
                      style="width: 100%; height: 180px; border-radius: 4px;">
                      <div slot="error">
                        <el-image :src="defaultCover" fit="cover" style="width: 100%; height: 180px; border-radius: 4px;"></el-image>
                      </div>
                    </el-image>
                  </div>
                  <div class="card-header">
                    <el-tag type="primary" size="small">{{ doc.documentaryType }}</el-tag>
                    <el-tag v-if="doc.rating" :type="getRatingType(doc.rating)" size="small">
                      {{ doc.rating.toFixed(1) }}分
                    </el-tag>
                  </div>
                  <h4 class="doc-title">{{ doc.documentaryName }}</h4>
                  <div class="doc-info">
                    <div class="info-item">
                      <i class="el-icon-user"></i>
                      <span>{{ doc.director || '未知' }}</span>
                    </div>
                    <div class="info-item">
                      <i class="el-icon-date"></i>
                      <span>{{ doc.releaseYear || '未知' }}</span>
                    </div>
                  </div>
                  <div class="doc-stats">
                    <span><i class="el-icon-view"></i> {{ formatNumber(doc.playCount) }}</span>
                    <span><i class="el-icon-star-off"></i> {{ formatNumber(doc.likeCount) }}</span>
                  </div>
                </el-card>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card style="margin-right: 20px;">
            <div slot="header" style="display: flex; align-items: center;">
              <i class="el-icon-star-on" style="font-size: 20px; color: #f56c6c; margin-right: 10px;"></i>
              <h3 style="margin: 0; color: #303133;">猜你此时想看</h3>
              <span style="margin-left: 10px; font-size: 12px; color: #909399;">基于高评分的个性化推荐</span>
            </div>
            <div v-loading="topRatedLoading">
              <el-empty v-if="topRatedList.length === 0" description="暂无推荐" :image-size="100"></el-empty>
              <div v-else class="recommendation-grid">
                <el-card
                  v-for="doc in topRatedList"
                  :key="doc.documentaryId"
                  shadow="hover"
                  class="recommendation-card"
                  @click.native="viewDocumentaryDetail(doc)">
                  <div class="card-cover">
                    <el-image
                      :src="doc.coverImageUrl || defaultCover"
                      fit="cover"
                      style="width: 100%; height: 180px; border-radius: 4px;">
                      <div slot="error">
                        <el-image :src="defaultCover" fit="cover" style="width: 100%; height: 180px; border-radius: 4px;"></el-image>
                      </div>
                    </el-image>
                  </div>
                  <div class="card-header">
                    <el-tag type="success" size="small">{{ doc.documentaryType }}</el-tag>
                    <el-tag v-if="doc.rating" :type="getRatingType(doc.rating)" size="small">
                      {{ doc.rating.toFixed(1) }}分
                    </el-tag>
                  </div>
                  <h4 class="doc-title">{{ doc.documentaryName }}</h4>
                  <div class="doc-info">
                    <div class="info-item">
                      <i class="el-icon-user"></i>
                      <span>{{ doc.director || '未知' }}</span>
                    </div>
                    <div class="info-item">
                      <i class="el-icon-date"></i>
                      <span>{{ doc.releaseYear || '未知' }}</span>
                    </div>
                  </div>
                  <div class="doc-stats">
                    <span><i class="el-icon-view"></i> {{ formatNumber(doc.playCount) }}</span>
                    <span><i class="el-icon-star-off"></i> {{ formatNumber(doc.likeCount) }}</span>
                  </div>
                </el-card>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <!-- 弹出的公告内容卡片 -->
      <el-dialog :title="selectedNotice.title" :visible.sync="showNoticeDialog" width="780px" append-to-body>
        <div slot="title" style="text-align: center;">{{ selectedNotice.title }}</div>
        <div v-html="selectedNotice.content" class="notice-content"></div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import {listNotice, getNotice} from "@/api/system/notice";
import { getWatchingNowRecommendations, getTopRatedRecommendations } from "@/api/documentary/recommendation";
import { listAllDocumentaries } from "@/api/documentary/documentary";
import {parseTime} from "../utils/ruoyi";
import defaultCoverImage from '@/assets/images/documentary-background.jpg'


export default {
  name: "Notice",
  dicts: ['sys_notice_status', 'sys_notice_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 兜底图片
      defaultCover: defaultCoverImage,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      noticeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      failureRateByCourseData: {},
      averageScoreByCourseData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: undefined,
        createBy: undefined,
        status: undefined
      },
      selectedNotice: {
        title: '',
        content: ''
      },
      showNoticeDialog: false,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeTitle: [
          {required: true, message: "公告标题不能为空", trigger: "blur"}
        ],
        noticeType: [
          {required: true, message: "公告类型不能为空", trigger: "change"}
        ]
      },
      // 推荐数据
      watchingNowList: [],
      watchingNowLoading: false,
      topRatedList: [],
      topRatedLoading: false,
      // 经典纪录片轮播数据
      classicDocumentaries: [],
      classicLoading: false
    };
  },
  created() {
    this.getList();
    this.loadClassicDocumentaries();
    this.loadWatchingNowRecommendations();
    this.loadTopRatedRecommendations();
  },
  methods: {
    parseTime,
    /** 查询公告列表 */
    getList() {
      this.loading = true;
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    showNoticeContent(row) {
      this.loading = true;
      getNotice(row.noticeId).then((response) => {
        this.selectedNotice.title = response.data.noticeTitle;
        this.selectedNotice.content = response.data.noticeContent;
        this.showNoticeDialog = true;
        this.loading = false;
      });
    },
    // 加载经典纪录片（轮播图）
    loadClassicDocumentaries() {
      this.classicLoading = true;
      // 获取评分最高的4部纪录片作为经典纪录片
      listAllDocumentaries({ pageNum: 1, pageSize: 4, status: 2 }).then(response => {
        this.classicDocumentaries = response.rows || [];
        this.classicLoading = false;
      }).catch(() => {
        this.classicDocumentaries = [];
        this.classicLoading = false;
      });
    },
    // 打开纪录片链接
    openDocumentaryLink(doc) {
      if (doc.detailUrl) {
        window.open(doc.detailUrl, '_blank');
      } else {
        this.$message.warning('该纪录片暂无详情链接');
      }
    },
    // 加载"其他人此时在看"推荐
    loadWatchingNowRecommendations() {
      this.watchingNowLoading = true;
      getWatchingNowRecommendations().then(response => {
        this.watchingNowList = response.data || [];
        this.watchingNowLoading = false;
      }).catch(() => {
        this.watchingNowList = [];
        this.watchingNowLoading = false;
      });
    },
    // 加载"猜你此时想看"推荐
    loadTopRatedRecommendations() {
      this.topRatedLoading = true;
      getTopRatedRecommendations().then(response => {
        this.topRatedList = response.data || [];
        this.topRatedLoading = false;
      }).catch(() => {
        this.topRatedList = [];
        this.topRatedLoading = false;
      });
    },
    // 查看纪录片详情
    viewDocumentaryDetail(doc) {
      this.$alert(
        `<div style="text-align: left;">
          <p><strong>类型：</strong>${doc.documentaryType || '未知'}</p>
          <p><strong>年份：</strong>${doc.releaseYear || '未知'}</p>
          <p><strong>评分：</strong>${doc.rating ? doc.rating.toFixed(1) : '暂无'}</p>
          <p><strong>导演/制作人：</strong>${doc.director || '未知'}</p>
          <p><strong>播放时段：</strong>${doc.broadcastTime || '未知'}</p>
          <p><strong>简介：</strong></p>
          <p style="text-indent: 2em; line-height: 1.6;">${doc.description || '暂无简介'}</p>
          ${doc.detailUrl ? `<p><a href="${doc.detailUrl}" target="_blank" style="color: #409eff;">查看详情页 →</a></p>` : ''}
        </div>`,
        doc.documentaryName,
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '关闭'
        }
      );
    },
    // 根据评分获取标签类型
    getRatingType(rating) {
      if (!rating) return 'info';
      if (rating >= 9) return 'success';
      if (rating >= 8) return 'primary';
      if (rating >= 7) return 'warning';
      return 'danger';
    },
    // 格式化数字
    formatNumber(num) {
      if (!num) return '0';
      if (num >= 100000000) {
        return (num / 100000000).toFixed(2) + '亿';
      }
      if (num >= 10000) {
        return (num / 10000).toFixed(2) + '万';
      }
      return num.toString();
    }
  }
};
</script>

<style scoped lang="scss">
.notice-content::v-deep img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
}

/* 页面标题样式 */
.page-header {
  margin-top: 10px;
  margin-bottom: 20px;
  padding: 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.header-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.title-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  margin-bottom: 10px;
}

.header-icon {
  font-size: 48px;
  color: #ffffff;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.main-title {
  margin: 0;
  font-size: 42px;
  font-weight: 700;
  color: #ffffff;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 2px;
  animation: fadeInDown 1s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.subtitle {
  margin: 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  letter-spacing: 3px;
  font-weight: 300;
  animation: fadeInUp 1s ease-out 0.3s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .page-header {
    padding: 20px 15px;
  }

  .header-icon {
    font-size: 36px;
  }

  .main-title {
    font-size: 28px;
  }

  .subtitle {
    font-size: 14px;
  }
}

/* 推荐区域样式 */
.recommendation-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
  margin-top: 10px;
}

@media screen and (max-width: 1400px) {
  .recommendation-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media screen and (max-width: 1200px) {
  .recommendation-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .recommendation-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.recommendation-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

.recommendation-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.recommendation-card .card-cover {
  margin-bottom: 10px;
  overflow: hidden;
  border-radius: 4px;
}

.recommendation-card .image-error,
.recommendation-card .image-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 180px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  font-size: 50px;
}

.recommendation-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.recommendation-card .doc-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin: 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 40px;
  line-height: 1.4;
}

.recommendation-card .doc-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin: 10px 0;
  font-size: 12px;
  color: #606266;
}

.recommendation-card .info-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.recommendation-card .info-item i {
  color: #909399;
}

.recommendation-card .info-item span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommendation-card .doc-stats {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
  font-size: 12px;
  color: #909399;
}

.recommendation-card .doc-stats span {
  display: flex;
  align-items: center;
  gap: 3px;
}

/* 经典纪录片轮播图样式 */
.carousel-item-content {
  position: relative;
  width: 100%;
  height: 360px;
  cursor: pointer;
  overflow: hidden;
  border-radius: 8px;
}

.carousel-image {
  width: 100%;
  height: 100%;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 100%);
  padding: 20px;
}

.carousel-title {
  font-size: 20px;
  font-weight: bold;
  color: #ffffff;
  margin: 0;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.carousel-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

::v-deep .el-carousel__arrow {
  background-color: rgba(0, 0, 0, 0.4);
}

::v-deep .el-carousel__arrow:hover {
  background-color: rgba(0, 0, 0, 0.6);
}
</style>
