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
      <!-- 通知公告 -->
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
            <h3 slot="header">健康小贴士</h3>
            <el-carousel :interval="5000" arrow="always">
              <el-carousel-item>
                <a href="https://www.baidu.com" target="_blank">
                  <img src="../assets/images/01.jpg" alt="Image 1" style="width: 100%;">
                </a>
              </el-carousel-item>
              <el-carousel-item>
                <a href="https://www.jd.com" target="_blank">
                  <img src="../assets/images/02.jpg" alt="Image 2" style="width: 100%;">
                </a>
              </el-carousel-item>
              <el-carousel-item>
                <a href="https://www.taobao.com" target="_blank">
                  <img src="../assets/images/03.jpg" alt="Image 3" style="width: 100%;">
                </a>
              </el-carousel-item>
            </el-carousel>
          </el-card>
        </el-col>
      </el-row>
<!--      <el-row style="margin-top: 20px;">-->
<!--        <el-col :span="12">-->
<!--          <el-card style="margin-right: 20px; height: 420px;">-->
<!--            <h3 slot="header">学科挂科率</h3>-->
<!--            <div id="failureRateChart" style="height: 300px;"></div> &lt;!&ndash; echarts 柱状图 &ndash;&gt;-->
<!--          </el-card>-->
<!--        </el-col>-->
<!--        <el-col :span="12">-->
<!--          <el-card style="margin-right: 20px; height: 420px;">-->
<!--            <h3 slot="header">学科平均分</h3>-->
<!--            <div id="averageScoreChart" style="height: 300px;"></div> &lt;!&ndash; echarts 柱状图 &ndash;&gt;-->
<!--          </el-card>-->
<!--        </el-col>-->
<!--      </el-row>-->
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
import * as echarts from 'echarts'
import {parseTime} from "../utils/ruoyi";


export default {
  name: "Notice",
  dicts: ['sys_notice_status', 'sys_notice_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
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
      }
    };
  },
  created() {
    this.getList();
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
</style>
