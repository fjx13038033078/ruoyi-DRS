<template>
  <div>
    <!-- 不良反应管理页面内容 -->
    <div>
      <!-- 添加不良反应按钮 -->
      <el-row :gutter="20" class="mb-20" style="margin-bottom: 10px; margin-left: 10px; margin-top: 10px">
        <el-col>
          <el-button type="primary"
                     plain
                     icon="el-icon-plus"
                     size="medium"
                     @click="handleAddAdverseReaction"
                     v-hasPermi="['drug:adverseReaction:add']">新增不良反应
          </el-button>
        </el-col>
      </el-row>

      <!-- 不良反应列表 -->
      <el-table :data="adverseReactionList" v-loading="loading" style="width: 100%" border>
        <el-table-column label="记录ID" prop="id" align="center" width="80"></el-table-column>
        <el-table-column label="患者姓名" prop="patientName" align="center" min-width="120">
          <template slot-scope="scope">
            {{ getPatientName(scope.row.patientId) }}
          </template>
        </el-table-column>
        <el-table-column label="药品名称" prop="drugName" align="center" min-width="150">
          <template slot-scope="scope">
            {{ getDrugName(scope.row.drugId) }}
          </template>
        </el-table-column>
        <el-table-column label="反应描述" prop="description" align="center" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column label="严重程度" prop="severity" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="getSeverityType(scope.row.severity)">
              {{ scope.row.severity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发生时间" prop="occurTime" align="center" width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.occurTime) }}
          </template>
        </el-table-column>
        <el-table-column label="责任医师" prop="doctorName" align="center" min-width="120">
          <template slot-scope="scope">
            {{ getDoctorName(scope.row.doctorId) }}
          </template>
        </el-table-column>
        <el-table-column label="医生确认" prop="doctorConfirmed" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.doctorConfirmed === 1 ? 'success' : 'warning'">
              {{ scope.row.doctorConfirmed === 1 ? '已确认' : '待确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="280px">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" size="mini" @click="handleView(scope.row)"
                       v-hasPermi="['drug:adverseReaction:view']">查看
            </el-button>
            <el-button type="text" icon="el-icon-edit" size="mini" @click="handleEdit(scope.row)"
                       v-hasPermi="['drug:adverseReaction:edit']">编辑
            </el-button>
            <el-button type="text" icon="el-icon-check" size="mini" @click="handleDoctorConfirm(scope.row)"
                       v-hasPermi="['drug:adverseReaction:confirm']"
                       v-if="scope.row.doctorConfirmed === 0">医生确认
            </el-button>
            <el-button type="text" icon="el-icon-delete" size="mini" @click="handleDelete(scope.row)"
                       v-hasPermi="['drug:adverseReaction:delete']">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <pagination
      v-show="totalAdverseReactions > 0"
      :total="totalAdverseReactions"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchAdverseReactions"
    />

    <!-- 添加/编辑不良反应对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="dialogTitle" width="60%" @close="handleCloseDialog">
      <!-- 对话框内容 -->
      <div>
        <el-form :model="adverseReactionForm" label-width="120px" :rules="formRules" ref="adverseReactionForm">
          <el-form-item label="患者" prop="patientId" v-if="!isPatientRole">
            <el-select v-model="adverseReactionForm.patientId" :disabled="isReadOnly" placeholder="请选择患者" clearable filterable>
              <el-option
                v-for="patient in patientOptions"
                :key="patient.id"
                :label="`${patient.patientName} (${patient.medicalRecordNo})`"
                :value="patient.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="患者" v-if="isPatientRole && isReadOnly">
            <span>{{ getCurrentPatientName() }}</span>
          </el-form-item>
          <el-form-item label="药品" prop="drugId">
            <el-select v-model="adverseReactionForm.drugId" :disabled="isReadOnly" placeholder="请选择药品" clearable filterable>
              <el-option
                v-for="drug in drugOptions"
                :key="drug.id"
                :label="drug.drugName"
                :value="drug.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="反应描述" prop="description">
            <el-input v-model="adverseReactionForm.description" :disabled="isReadOnly" type="textarea"
                      :rows="4" placeholder="请详细描述不良反应症状"></el-input>
          </el-form-item>
          <el-form-item label="严重程度" prop="severity">
            <el-select v-model="adverseReactionForm.severity" :disabled="isReadOnly" placeholder="请选择严重程度">
              <el-option label="轻微" value="轻微"></el-option>
              <el-option label="中度" value="中度"></el-option>
              <el-option label="严重" value="严重"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发生时间" prop="occurTime">
            <el-date-picker
              v-model="adverseReactionForm.occurTime"
              type="datetime"
              placeholder="选择发生时间"
              :disabled="isReadOnly"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="医生确认" v-if="isReadOnly">
            <el-tag :type="adverseReactionForm.doctorConfirmed === 1 ? 'success' : 'warning'">
              {{ adverseReactionForm.doctorConfirmed === 1 ? '已确认' : '待确认' }}
            </el-tag>
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
  listAllAdverseReactions,
  addAdverseReaction,
  updateAdverseReaction,
  deleteAdverseReaction,
  getAdverseReactionById,
  updateDoctorConfirmed
} from '@/api/patient/adverseReaction'
import {listAllPatients} from '@/api/patient/patient'
import {listAllDrugs} from '@/api/patient/drug'
import {doctorList} from '@/api/system/user'

export default {
  data() {
    return {
      loading: false,
      adverseReactionList: [],
      patientOptions: [], // 患者下拉选项
      drugOptions: [], // 药品下拉选项
      doctorOptions: [], // 医生下拉选项
      dialogVisible: false,
      dialogTitle: '',
      dialogButtonText: '',
      totalAdverseReactions: 0,
      isReadOnly: false, // 是否只读模式
      adverseReactionForm: {
        id: null,
        patientId: null,
        drugId: null,
        description: '',
        severity: '',
        occurTime: ''
      },
      // 表单验证规则
      formRules: {
        patientId: [
          { required: false, message: '请选择患者', trigger: 'change' }
        ],
        drugId: [
          { required: true, message: '请选择药品', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入反应描述', trigger: 'blur' },
          { min: 5, message: '反应描述至少5个字符', trigger: 'blur' },
          { max: 500, message: '反应描述不能超过500个字符', trigger: 'blur' }
        ],
        severity: [
          { required: true, message: '请选择严重程度', trigger: 'change' }
        ],
        occurTime: [
          { required: true, message: '请选择发生时间', trigger: 'change' }
        ]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 当前用户角色
      isPatientRole: false,
    }
  },
  created() {
    // 检查当前用户角色
    this.checkUserRole()
    // 先加载基础数据，再加载不良反应列表
    Promise.all([
      this.fetchPatientOptions(),
      this.fetchDrugOptions(),
      this.fetchDoctorOptions()
    ]).then(() => {
      this.fetchAdverseReactions()
    })
  },
  methods: {
    fetchAdverseReactions() {
      this.loading = true
      listAllAdverseReactions(this.queryParams).then(response => {
        this.adverseReactionList = response.rows
        this.totalAdverseReactions = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    fetchPatientOptions() {
      return listAllPatients({pageNum: 1, pageSize: 1000}).then(response => {
        this.patientOptions = response.rows.map(patient => ({
          id: patient.userId,
          patientName: patient.patientName || `患者${patient.id}`,
          medicalRecordNo: patient.medicalRecordNo
        }))
      })
    },
    fetchDrugOptions() {
      return listAllDrugs({pageNum: 1, pageSize: 1000}).then(response => {
        this.drugOptions = response.rows.map(drug => ({
          id: drug.id,
          drugName: drug.drugName
        }))
      })
    },
    fetchDoctorOptions() {
      return doctorList({pageNum: 1, pageSize: 1000}).then(response => {
        this.doctorOptions = response.rows.map(doctor => ({
          userId: doctor.userId,
          nickName: doctor.nickName
        }))
      })
    },
    getPatientName(patientId) {
      if (!patientId) return '-'
      const patient = this.patientOptions.find(p => p.id === patientId)
      return patient ? patient.patientName : `患者ID: ${patientId}`
    },
    getDrugName(drugId) {
      if (!drugId) return '-'
      const drug = this.drugOptions.find(d => d.id === drugId)
      return drug ? drug.drugName : `药品ID: ${drugId}`
    },
    getDoctorName(doctorId) {
      if (!doctorId) return '-'
      const doctor = this.doctorOptions.find(d => d.userId === doctorId)
      return doctor ? doctor.nickName : (this.doctorOptions.length === 0 ? '加载中...' : `医生ID: ${doctorId}`)
    },
    checkUserRole() {
      // 从store中获取当前用户角色
      const roles = this.$store.getters.roles || []
      this.isPatientRole = roles.includes('patient')
      // 根据角色动态设置验证规则
      this.updateFormRules()
    },
    updateFormRules() {
      // 动态更新表单验证规则
      if (this.isPatientRole) {
        // 患者角色不需要验证patientId
        this.formRules.patientId = [
          { required: false, message: '请选择患者', trigger: 'change' }
        ]
      } else {
        // 管理员和医生角色需要验证patientId
        this.formRules.patientId = [
          { required: true, message: '请选择患者', trigger: 'change' }
        ]
      }
    },
    getCurrentPatientName() {
      // 获取当前登录用户的姓名
      const user = this.$store.getters.user || {}
      return user.nickName || user.userName || '当前用户'
    },
    getSeverityType(severity) {
      switch (severity) {
        case '轻微': return 'info'
        case '中度': return 'warning'
        case '严重': return 'danger'
        default: return 'info'
      }
    },
    handleView(row) {
      this.dialogTitle = '查看不良反应'
      this.dialogButtonText = '关闭'
      this.isReadOnly = true
      getAdverseReactionById(row.id).then(response => {
        this.adverseReactionForm = response.data
        this.dialogVisible = true
      })
    },
    handleAddAdverseReaction() {
      this.isReadOnly = false
      this.dialogTitle = '新增不良反应'
      this.dialogButtonText = '添加'
      // 如果是患者角色，不需要设置patientId，后端会自动处理
      if (!this.isPatientRole) {
        this.adverseReactionForm.patientId = null
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isReadOnly = false
      this.dialogTitle = '编辑不良反应'
      this.dialogButtonText = '更新'
      this.adverseReactionForm = Object.assign({}, row)
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该不良反应记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteAdverseReaction(row.id).then(() => {
          this.$message.success('删除成功')
          this.fetchAdverseReactions()
        })
      })
    },
    handleDoctorConfirm(row) {
      const patientName = this.getPatientName(row.patientId)
      const drugName = this.getDrugName(row.drugId)
      this.$confirm(`确认要确认患者"${patientName}"使用"${drugName}"的不良反应记录吗？确认后将无法撤销。`, '医生确认', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const confirmData = {
          id: row.id,
          doctorConfirmed: 1
        }
        updateDoctorConfirmed(confirmData).then(() => {
          this.$message.success('医生确认成功')
          this.fetchAdverseReactions()
        })
      })
    },
    handleCloseDialog() {
      this.dialogVisible = false
      this.clearForm()
      // 清除表单验证
      if (this.$refs.adverseReactionForm) {
        this.$refs.adverseReactionForm.clearValidate()
      }
    },
    clearForm() {
      this.adverseReactionForm = {
        id: null,
        patientId: this.isPatientRole ? undefined : null, // 患者角色时不设置patientId
        drugId: null,
        description: '',
        severity: '',
        occurTime: ''
      }
    },
    handleSubmit() {
      this.$refs.adverseReactionForm.validate((valid) => {
        if (valid) {
          // 准备提交数据
          const submitData = { ...this.adverseReactionForm }
          
          // 如果是患者角色，不发送patientId，让后端自动处理
          if (this.isPatientRole) {
            delete submitData.patientId
          }
          
          if (this.dialogButtonText === '添加') {
            addAdverseReaction(submitData).then(() => {
              this.$message.success('添加成功')
              this.dialogVisible = false
              this.clearForm()
              this.fetchAdverseReactions()
            }).catch(error => {
              console.error('添加失败:', error)
              this.$message.error('添加失败，请检查数据')
            })
          } else if (this.dialogButtonText === '更新') {
            updateAdverseReaction(submitData).then(() => {
              this.$message.success('更新成功')
              this.dialogVisible = false
              this.clearForm()
              this.fetchAdverseReactions()
            }).catch(error => {
              console.error('更新失败:', error)
              this.$message.error('更新失败，请检查数据')
            })
          }
        } else {
          this.$message.error('请检查表单信息')
          return false
        }
      })
    },
    // 工具方法
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
/* 样式可以根据需求调整 */
</style>
