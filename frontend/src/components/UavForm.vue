<template>
  <div class="card">
    <div class="card-header">
      <h3 class="card-title">{{ uav ? '编辑无人机' : '新增无人机' }}</h3>
    </div>
    <div class="card-body">
      <form @submit.prevent="handleSubmit">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">编号 *</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="form.code" 
              required
              placeholder="请输入无人机编号"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">型号 *</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="form.model" 
              required
              placeholder="请输入无人机型号"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">制造商 *</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="form.manufacturer" 
              required
              placeholder="请输入制造商"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">最大飞行时间(分钟)</label>
            <input 
              type="number" 
              class="form-control" 
              v-model.number="form.maxFlightTimeMinutes"
              placeholder="请输入最大飞行时间"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">最大航程(公里)</label>
            <input 
              type="number" 
              class="form-control" 
              v-model.number="form.maxRangeKm"
              placeholder="请输入最大航程"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">载重(公斤)</label>
            <input 
              type="number" 
              class="form-control" 
              v-model.number="form.payloadKg"
              placeholder="请输入载重"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">最大高度(米)</label>
            <input 
              type="number" 
              class="form-control" 
              v-model.number="form.maxAltitudeMeters"
              placeholder="请输入最大高度"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">最大速度(公里/小时)</label>
            <input 
              type="number" 
              class="form-control" 
              v-model.number="form.maxSpeedKmh"
              placeholder="请输入最大速度"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">电池容量(mAh)</label>
            <input 
              type="number" 
              class="form-control" 
              v-model.number="form.batteryCapacityMah"
              placeholder="请输入电池容量"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">重量(公斤)</label>
            <input 
              type="number" 
              class="form-control" 
              v-model.number="form.weightKg"
              placeholder="请输入重量"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">摄像头分辨率</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="form.cameraResolution"
              placeholder="请输入摄像头分辨率"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">序列号</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="form.serialNumber"
              placeholder="请输入序列号"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">所属部门</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="form.department"
              placeholder="请输入所属部门"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">应用领域</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="form.applicationField"
              placeholder="请输入应用领域"
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">状态</label>
            <select class="form-control" v-model="form.status">
              <option value="ACTIVE">运行中</option>
              <option value="INACTIVE">停用</option>
              <option value="MAINTENANCE">维护中</option>
            </select>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">备注</label>
            <textarea 
              class="form-control" 
              v-model="form.remark" 
              rows="3"
              placeholder="请输入备注信息"
            ></textarea>
          </div>
        </div>
        <div class="mb-3">
          <button type="submit" class="btn btn-primary">保存</button>
          <button type="button" class="btn btn-secondary ml-2" @click="$emit('cancel')">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  uav: Object
})

const emit = defineEmits(['submit', 'cancel'])

const form = ref({
  code: '',
  model: '',
  manufacturer: '',
  maxFlightTimeMinutes: null,
  maxRangeKm: null,
  payloadKg: null,
  maxAltitudeMeters: null,
  maxSpeedKmh: null,
  batteryCapacityMah: null,
  weightKg: null,
  cameraResolution: '',
  hasGps: true,
  hasObstacleAvoidance: false,
  purchaseDate: '',
  warrantyExpireDate: '',
  serialNumber: '',
  department: '',
  applicationField: '',
  status: 'ACTIVE',
  remark: ''
})

watch(() => props.uav, (newVal) => {
  if (newVal) {
    form.value = {
      code: newVal.code || '',
      model: newVal.model || '',
      manufacturer: newVal.manufacturer || '',
      maxFlightTimeMinutes: newVal.maxFlightTimeMinutes || null,
      maxRangeKm: newVal.maxRangeKm || null,
      payloadKg: newVal.payloadKg || null,
      maxAltitudeMeters: newVal.maxAltitudeMeters || null,
      maxSpeedKmh: newVal.maxSpeedKmh || null,
      batteryCapacityMah: newVal.batteryCapacityMah || null,
      weightKg: newVal.weightKg || null,
      cameraResolution: newVal.cameraResolution || '',
      hasGps: newVal.hasGps || true,
      hasObstacleAvoidance: newVal.hasObstacleAvoidance || false,
      purchaseDate: newVal.purchaseDate || '',
      warrantyExpireDate: newVal.warrantyExpireDate || '',
      serialNumber: newVal.serialNumber || '',
      department: newVal.department || '',
      applicationField: newVal.applicationField || '',
      status: newVal.status || 'ACTIVE',
      remark: newVal.remark || ''
    }
  }
}, { immediate: true })

const handleSubmit = () => {
  emit('submit', { ...form.value })
}
</script>
