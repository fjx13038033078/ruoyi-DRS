<template>
  <div>
    <!-- 用药记录管理页面内容 -->
    <div>
      <!-- 添加用药记录按钮 -->
      <el-row :gutter="20" class="mb-20" style="margin-bottom: 10px; margin-left: 10px; margin-top: 10px">
        <el-col>
          <el-button type="primary"
                     plain
                     icon="el-icon-plus"
                     size="medium"
                     @click="handleAddMedicationRecord"
                     v-hasPermi="['drug:medicationRecord:add']">新增用药记录
          </el-button>
        </el-col>
      </el-row>

      <!-- 用药记录列表 -->
      <el-table :data="medicationRecordList" v-loading="loading" style="width: 100%" border>
        <el-table-column label="记录ID" prop="id" align="center" width="80"></el-table-column>
        <el-table-column label="患者姓名" prop="patientName" align="center" min-width="120">
          <template slot-scope="scope">
            {{ getPatientName(scope.row.patientId) }}
          </template>
        </el-table-column>
        <el-table-column label="负责医生" prop="doctorName" align="center" min-width="120">
          <template slot-scope="scope">
            {{ getDoctorName(scope.row.patientId) }}
          </template>
        </el-table-column>
        <el-table-column label="药品名称" prop="drugName" align="center" min-width="150">
          <template slot-scope="scope">
            {{ getDrugName(scope.row.drugId) }}
          </template>
        </el-table-column>
        <el-table-column label="剂量" prop="dosage" align="center" min-width="100"></el-table-column>
        <el-table-column label="频率" prop="frequency" align="center" min-width="120"></el-table-column>
        <el-table-column label="开始日期" prop="startDate" align="center" min-width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column label="结束日期" prop="endDate" align="center" min-width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '使用中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="280px">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" size="mini" @click="handleView(scope.row)"
                       v-hasPermi="['drug:medicationRecord:view']">查看
            </el-button>
            <el-button type="text" icon="el-icon-edit" size="mini" @click="handleEdit(scope.row)"
                       v-hasPermi="['drug:medicationRecord:edit']">编辑
            </el-button>
            <el-button type="text" icon="el-icon-time" size="mini" @click="handleViewMedicationLogs(scope.row)"
                       v-hasPermi="['drug:medicationLog:view']">服药打卡
            </el-button>
            <el-button type="text" icon="el-icon-delete" size="mini" @click="handleDelete(scope.row)"
                       v-hasPermi="['drug:medicationRecord:delete']">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <pagination
      v-show="totalMedicationRecords > 0"
      :total="totalMedicationRecords"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchMedicationRecords"
    />

    <!-- 添加/编辑用药记录对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="dialogTitle" width="50%" @close="handleCloseDialog">
      <!-- 对话框内容 -->
      <div>
        <el-form :model="medicationRecordForm" label-width="120px" :rules="formRules" ref="medicationRecordForm">
          <el-form-item label="患者" prop="patientId">
            <el-select v-model="medicationRecordForm.patientId" :disabled="isReadOnly" placeholder="请选择患者" clearable filterable>
              <el-option
                v-for="patient in patientOptions"
                :key="patient.id"
                :label="`${patient.patientName} (${patient.medicalRecordNo})`"
                :value="patient.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="药品" prop="drugId">
            <el-select v-model="medicationRecordForm.drugId" :disabled="isReadOnly" placeholder="请选择药品" clearable filterable>
              <el-option
                v-for="drug in drugOptions"
                :key="drug.id"
                :label="drug.drugName"
                :value="drug.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="剂量" prop="dosage">
            <el-input v-model="medicationRecordForm.dosage" :disabled="isReadOnly" placeholder="请输入剂量，如：10mg"></el-input>
          </el-form-item>
          <el-form-item label="频率" prop="frequency">
            <el-input v-model="medicationRecordForm.frequency" :disabled="isReadOnly" placeholder="请输入频率，如：一日三次"></el-input>
          </el-form-item>
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker
              v-model="medicationRecordForm.startDate"
              type="date"
              placeholder="选择开始日期"
              :disabled="isReadOnly"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker
              v-model="medicationRecordForm.endDate"
              type="date"
              placeholder="选择结束日期"
              :disabled="isReadOnly"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="medicationRecordForm.status" :disabled="isReadOnly" placeholder="请选择状态">
              <el-option label="使用中" :value="1"></el-option>
              <el-option label="已结束" :value="0"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <!-- 对话框按钮 -->
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button @click="handleCloseDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="!isReadOnly">{{ dialogButtonText }}</el-button>
      </div>
    </el-dialog>

    <!-- 服药打卡管理对话框 -->
    <el-dialog :visible.sync="logDialogVisible" :title="logDialogTitle" width="70%" @close="handleCloseLogDialog">
      <!-- 打卡记录操作按钮 -->
      <div style="margin-bottom: 15px;">
        <el-button type="primary" size="small" @click="handleAddLog"
                   v-hasPermi="['drug:medicationLog:add']">新增打卡记录
        </el-button>
      </div>

      <!-- 打卡记录列表 -->
      <el-table :data="medicationLogList" v-loading="logLoading" style="width: 100%" border>
        <el-table-column label="打卡ID" prop="id" align="center" width="80"></el-table-column>
        <el-table-column label="服药时间" prop="takeTime" align="center" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.takeTime) }}
          </template>
        </el-table-column>
        <el-table-column label="服药状态" prop="isTaken" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isTaken === 1 ? 'success' : 'danger'">
              {{ scope.row.isTaken === 1 ? '已服药' : '未服药' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.isTaken === 1" style="color: #67C23A;">按时服药</span>
            <span v-else style="color: #F56C6C;">未按时服药</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180px">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" size="mini" @click="handleViewLog(scope.row)">查看</el-button>
            <el-button type="text" icon="el-icon-edit" size="mini" @click="handleEditLog(scope.row)"
                       v-hasPermi="['drug:medicationLog:edit']">编辑
            </el-button>
            <el-button type="text" icon="el-icon-delete" size="mini" @click="handleDeleteLog(scope.row)"
                       v-hasPermi="['drug:medicationLog:delete']">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加/编辑打卡记录对话框 -->
    <el-dialog :visible.sync="logFormDialogVisible" :title="logFormDialogTitle" width="50%" @close="handleCloseLogFormDialog">
      <div>
        <el-form :model="medicationLogForm" label-width="120px" :rules="logFormRules" ref="medicationLogForm">
          <el-form-item label="服药时间" prop="takeTime">
            <el-date-picker
              v-model="medicationLogForm.takeTime"
              type="date"
              placeholder="选择服药时间"
              :disabled="logIsReadOnly"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="服药状态" prop="isTaken">
            <el-select v-model="medicationLogForm.isTaken" :disabled="logIsReadOnly" placeholder="请选择服药状态">
              <el-option label="已服药" :value="1"></el-option>
              <el-option label="未服药" :value="0"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <!-- 对话框按钮 -->
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button @click="handleCloseLogFormDialog">取消</el-button>
        <el-button type="primary" @click="handleLogSubmit" v-if="!logIsReadOnly">{{ logFormDialogButtonText }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAllMedicationRecords,
  addMedicationRecord,
  updateMedicationRecord,
  deleteMedicationRecord,
  getMedicationRecordById
} from '@/api/patient/medicationRecord'
import {
  listAllMedicationLogs,
  getMedicationLogById,
  getMedicationLogsByRecordId,
  addMedicationLog,
  updateMedicationLog,
  deleteMedicationLog
} from '@/api/patient/medicationLog'
import {listAllPatients} from '@/api/patient/patient'
import {listAllDrugs} from '@/api/patient/drug'

export default {
  data() {
    return {
      loading: false,
      medicationRecordList: [],
      patientOptions: [], // 患者下拉选项
      drugOptions: [], // 药品下拉选项
      dialogVisible: false,
      dialogTitle: '',
      dialogButtonText: '',
      totalMedicationRecords: 0,
      isReadOnly: false, // 是否只读模式
      // 服药打卡对话框相关
      logDialogVisible: false,
      logDialogTitle: '',
      medicationLogList: [],
      logLoading: false,
      currentRecordId: null,
      currentRecordInfo: '',
      // 打卡记录表单对话框相关
      logFormDialogVisible: false,
      logFormDialogTitle: '',
      logFormDialogButtonText: '',
      logIsReadOnly: false,
      medicationLogForm: {
        id: null,
        recordId: null,
        takeTime: '',
        isTaken: 1
      },
      medicationRecordForm: {
        id: null,
        patientId: null,
        drugId: null,
        dosage: '',
        frequency: '',
        startDate: '',
        endDate: '',
        status: 1
      },
      // 表单验证规则
      formRules: {
        patientId: [
          { required: true, message: '请选择患者', trigger: 'change' }
        ],
        drugId: [
          { required: true, message: '请选择药品', trigger: 'change' }
        ],
        dosage: [
          { required: true, message: '请输入剂量', trigger: 'blur' },
          { max: 50, message: '剂量不能超过50个字符', trigger: 'blur' }
        ],
        frequency: [
          { required: true, message: '请输入频率', trigger: 'blur' },
          { max: 50, message: '频率不能超过50个字符', trigger: 'blur' }
        ],
        startDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      logFormRules: {
        takeTime: [
          { required: true, message: '请选择服药时间', trigger: 'change' }
        ],
        isTaken: [
          { required: true, message: '请选择服药状态', trigger: 'change' }
        ]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
    }
  },
  created() {
    this.fetchMedicationRecords()
    this.fetchPatientOptions()
    this.fetchDrugOptions()
  },
  methods: {
    fetchMedicationRecords() {
      this.loading = true
      listAllMedicationRecords(this.queryParams).then(response => {
        this.medicationRecordList = response.rows
        this.totalMedicationRecords = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    fetchPatientOptions() {
      listAllPatients({pageNum: 1, pageSize: 1000}).then(response => {
        this.patientOptions = response.rows.map(patient => ({
          id: patient.userId,
          patientName: patient.patientName || `患者${patient.id}`, // 使用真实姓名，如果没有则用默认格式
          medicalRecordNo: patient.medicalRecordNo,
          doctorId: patient.doctorId,
          doctorName: patient.doctorName
        }))
      })
    },
    fetchDrugOptions() {
      listAllDrugs({pageNum: 1, pageSize: 1000}).then(response => {
        this.drugOptions = response.rows.map(drug => ({
          id: drug.id,
          drugName: drug.drugName
        }))
      })
    },
    getPatientName(patientId) {
      if (!patientId) return '-'
      const patient = this.patientOptions.find(p => p.id === patientId)
      return patient ? patient.patientName : `您暂无查看权限`
    },
    getDoctorName(patientId) {
      if (!patientId) return '-'
      const patient = this.patientOptions.find(p => p.id === patientId)
      return patient && patient.doctorName ? patient.doctorName : '您暂无查看权限'
    },
    getDrugName(drugId) {
      if (!drugId) return '-'
      const drug = this.drugOptions.find(d => d.id === drugId)
      return drug ? drug.drugName : `药品ID: ${drugId}`
    },
    handleView(row) {
      this.dialogTitle = '查看用药记录'
      this.dialogButtonText = '关闭'
      this.isReadOnly = true
      getMedicationRecordById(row.id).then(response => {
        this.medicationRecordForm = response.data
        this.dialogVisible = true
      })
    },
    handleAddMedicationRecord() {
      this.isReadOnly = false
      this.dialogTitle = '新增用药记录'
      this.dialogButtonText = '添加'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isReadOnly = false
      this.dialogTitle = '编辑用药记录'
      this.dialogButtonText = '更新'
      this.medicationRecordForm = Object.assign({}, row)
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该用药记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMedicationRecord(row.id).then(() => {
          this.$message.success('删除成功')
          this.fetchMedicationRecords()
        })
      })
    },
    handleCloseDialog() {
      this.dialogVisible = false
      this.clearForm()
      // 清除表单验证
      if (this.$refs.medicationRecordForm) {
        this.$refs.medicationRecordForm.clearValidate()
      }
    },
    clearForm() {
      this.medicationRecordForm = {
        id: null,
        patientId: null,
        drugId: null,
        dosage: '',
        frequency: '',
        startDate: '',
        endDate: '',
        status: 1
      }
    },
    handleSubmit() {
      this.$refs.medicationRecordForm.validate((valid) => {
        if (valid) {
          if (this.dialogButtonText === '添加') {
            addMedicationRecord(this.medicationRecordForm).then(() => {
              this.$message.success('添加成功')
              this.dialogVisible = false
              this.clearForm()
              this.fetchMedicationRecords()
            }).catch(error => {
              console.error('添加失败:', error)
              this.$message.error('添加失败，请检查数据')
            })
          } else if (this.dialogButtonText === '更新') {
            updateMedicationRecord(this.medicationRecordForm).then(() => {
              this.$message.success('更新成功')
              this.dialogVisible = false
              this.clearForm()
              this.fetchMedicationRecords()
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
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },

    // ========== 服药打卡管理相关方法 ==========
    handleViewMedicationLogs(row) {
      this.currentRecordId = row.id
      this.currentRecordInfo = `${this.getPatientName(row.patientId)} - ${this.getDrugName(row.drugId)}`
      this.logDialogTitle = `服药打卡记录 - ${this.currentRecordInfo}`
      this.logDialogVisible = true
      this.fetchMedicationLogs()
    },
    fetchMedicationLogs() {
      this.logLoading = true
      getMedicationLogsByRecordId(this.currentRecordId).then(response => {
        console.log('打卡记录数据响应:', response)
        // 兼容不同的响应格式
        let logData = []
        if (response.data) {
          // 如果data是数组，直接使用；如果是单个对象，转换为数组
          if (Array.isArray(response.data)) {
            logData = response.data
          } else if (response.data && typeof response.data === 'object') {
            logData = [response.data]
          }
        } else if (response.rows) {
          logData = Array.isArray(response.rows) ? response.rows : []
        } else if (Array.isArray(response)) {
          logData = response
        } else if (response && typeof response === 'object') {
          // 如果response本身就是单个对象，转换为数组
          logData = [response]
        }

        this.medicationLogList = logData
        console.log('打卡记录列表数据:', this.medicationLogList)
        this.logLoading = false
      }).catch(error => {
        console.error('获取打卡记录列表失败:', error)
        this.logLoading = false
        this.$message.error('获取打卡记录列表失败')
      })
    },
    handleAddLog() {
      this.logIsReadOnly = false
      this.logFormDialogTitle = `新增打卡记录 - ${this.currentRecordInfo}`
      this.logFormDialogButtonText = '添加'
      this.medicationLogForm.recordId = this.currentRecordId
      this.logFormDialogVisible = true
    },
    handleViewLog(row) {
      this.logIsReadOnly = true
      this.logFormDialogTitle = `查看打卡记录 - ${this.currentRecordInfo}`
      this.logFormDialogButtonText = '关闭'
      this.medicationLogForm = Object.assign({}, row)
      this.logFormDialogVisible = true
    },
    handleEditLog(row) {
      this.logIsReadOnly = false
      this.logFormDialogTitle = `编辑打卡记录 - ${this.currentRecordInfo}`
      this.logFormDialogButtonText = '更新'
      this.medicationLogForm = Object.assign({}, row)
      this.logFormDialogVisible = true
    },
    handleDeleteLog(row) {
      this.$confirm('确认删除该打卡记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMedicationLog(row.id).then(() => {
          this.$message.success('删除成功')
          this.fetchMedicationLogs()
        })
      })
    },
    handleCloseLogDialog() {
      this.logDialogVisible = false
      this.medicationLogList = []
      this.currentRecordId = null
      this.currentRecordInfo = ''
    },
    handleCloseLogFormDialog() {
      this.logFormDialogVisible = false
      // 只有在不是从打卡管理页面进入时才完全清空
      if (!this.logDialogVisible) {
        this.clearLogForm()
      } else {
        // 保留recordId，只清空表单内容
        this.medicationLogForm = {
          id: null,
          recordId: this.currentRecordId,
          takeTime: '',
          isTaken: 1
        }
      }
      // 清除表单验证
      if (this.$refs.medicationLogForm) {
        this.$refs.medicationLogForm.clearValidate()
      }
    },
    clearLogForm() {
      // 保留recordId，因为这是当前操作的用药记录ID
      const currentRecordId = this.medicationLogForm.recordId
      this.medicationLogForm = {
        id: null,
        recordId: currentRecordId,
        takeTime: '',
        isTaken: 1
      }
    },
    handleLogSubmit() {
      this.$refs.medicationLogForm.validate((valid) => {
        if (valid) {
          if (this.logFormDialogButtonText === '添加') {
            addMedicationLog(this.medicationLogForm).then(response => {
              console.log('添加打卡记录成功响应:', response)
              this.$message.success('添加成功')
              this.logFormDialogVisible = false
              this.clearLogForm()
              // 延时刷新确保数据已保存
              setTimeout(() => {
                this.fetchMedicationLogs()
              }, 500)
            }).catch(error => {
              console.error('添加失败:', error)
              this.$message.error('添加失败，请检查数据')
            })
          } else if (this.logFormDialogButtonText === '更新') {
            updateMedicationLog(this.medicationLogForm).then(response => {
              console.log('更新打卡记录成功响应:', response)
              this.$message.success('更新成功')
              this.logFormDialogVisible = false
              this.clearLogForm()
              // 延时刷新确保数据已保存
              setTimeout(() => {
                this.fetchMedicationLogs()
              }, 500)
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
    }
  }
}
</script>

<style scoped>
/* 样式可以根据需求调整 */
</style>
