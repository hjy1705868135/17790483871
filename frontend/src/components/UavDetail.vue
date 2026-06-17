<template>
  <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">无人机详情</h5>
          <button type="button" class="close" @click="$emit('close')">
            <span>&times;</span>
          </button>
        </div>
        <div class="modal-body" v-if="uav">
          <div class="row">
            <div class="col-md-6">
              <div class="form-group">
                <label>编号</label>
                <p class="form-control-plaintext">{{ uav.code }}</p>
              </div>
              <div class="form-group">
                <label>型号</label>
                <p class="form-control-plaintext">{{ uav.model }}</p>
              </div>
              <div class="form-group">
                <label>制造商</label>
                <p class="form-control-plaintext">{{ uav.manufacturer }}</p>
              </div>
              <div class="form-group">
                <label>最大飞行时间</label>
                <p class="form-control-plaintext">{{ uav.maxFlightTimeMinutes }} 分钟</p>
              </div>
              <div class="form-group">
                <label>最大航程</label>
                <p class="form-control-plaintext">{{ uav.maxRangeKm }} 公里</p>
              </div>
              <div class="form-group">
                <label>载重</label>
                <p class="form-control-plaintext">{{ uav.payloadKg }} 公斤</p>
              </div>
              <div class="form-group">
                <label>最大高度</label>
                <p class="form-control-plaintext">{{ uav.maxAltitudeMeters }} 米</p>
              </div>
              <div class="form-group">
                <label>最大速度</label>
                <p class="form-control-plaintext">{{ uav.maxSpeedKmh }} 公里/小时</p>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-group">
                <label>电池容量</label>
                <p class="form-control-plaintext">{{ uav.batteryCapacityMah }} mAh</p>
              </div>
              <div class="form-group">
                <label>重量</label>
                <p class="form-control-plaintext">{{ uav.weightKg }} 公斤</p>
              </div>
              <div class="form-group">
                <label>摄像头分辨率</label>
                <p class="form-control-plaintext">{{ uav.cameraResolution }}</p>
              </div>
              <div class="form-group">
                <label>GPS功能</label>
                <p class="form-control-plaintext">{{ uav.hasGps ? '是' : '否' }}</p>
              </div>
              <div class="form-group">
                <label>避障功能</label>
                <p class="form-control-plaintext">{{ uav.hasObstacleAvoidance ? '是' : '否' }}</p>
              </div>
              <div class="form-group">
                <label>序列号</label>
                <p class="form-control-plaintext">{{ uav.serialNumber }}</p>
              </div>
              <div class="form-group">
                <label>所属部门</label>
                <p class="form-control-plaintext">{{ uav.department }}</p>
              </div>
              <div class="form-group">
                <label>应用领域</label>
                <p class="form-control-plaintext">{{ uav.applicationField }}</p>
              </div>
              <div class="form-group">
                <label>状态</label>
                <p class="form-control-plaintext">{{ getStatusText(uav.status) }}</p>
              </div>
              <div class="form-group">
                <label>备注</label>
                <p class="form-control-plaintext">{{ uav.remark || '-' }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" @click="$emit('edit')">编辑</button>
          <button type="button" class="btn btn-secondary" @click="$emit('close')">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  uav: Object
})

defineEmits(['close', 'edit'])

const getStatusText = (status) => {
  const texts = {
    'ACTIVE': '运行中',
    'INACTIVE': '停用',
    'MAINTENANCE': '维护中'
  }
  return texts[status] || status
}
</script>
