<template>
  <div class="countdown-timer" :class="{ 'countdown-timer--urgent': isUrgent }">
    <div class="countdown-timer__label">{{ label }}</div>
    <div class="countdown-timer__time">
      <span class="countdown-timer__block">
        <span class="countdown-timer__digit">{{ hours }}</span>
        <span class="countdown-timer__unit">时</span>
      </span>
      <span class="countdown-timer__separator">:</span>
      <span class="countdown-timer__block">
        <span class="countdown-timer__digit">{{ minutes }}</span>
        <span class="countdown-timer__unit">分</span>
      </span>
      <span class="countdown-timer__separator">:</span>
      <span class="countdown-timer__block">
        <span class="countdown-timer__digit">{{ seconds }}</span>
        <span class="countdown-timer__unit">秒</span>
      </span>
    </div>
    <div v-if="isUrgent" class="countdown-timer__warning">即将结束!</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  endTime: {
    type: [String, Number],
    required: true
  },
  label: {
    type: String,
    default: '限时特惠'
  },
  urgentThreshold: {
    type: Number,
    default: 3600000
  }
})

const emit = defineEmits(['end'])

const now = ref(Date.now())
let timer = null

const remainingTime = computed(() => {
  const end = Number(props.endTime)
  const diff = end - now.value
  return Math.max(0, diff)
})

const hours = computed(() => {
  return String(Math.floor(remainingTime.value / (1000 * 60 * 60))).padStart(2, '0')
})

const minutes = computed(() => {
  return String(Math.floor((remainingTime.value % (1000 * 60 * 60)) / (1000 * 60))).padStart(2, '0')
})

const seconds = computed(() => {
  return String(Math.floor((remainingTime.value % (1000 * 60)) / 1000)).padStart(2, '0')
})

const isUrgent = computed(() => {
  return remainingTime.value > 0 && remainingTime.value <= props.urgentThreshold
})

const isEnded = computed(() => {
  return remainingTime.value === 0
})

onMounted(() => {
  if (!isEnded.value) {
    timer = setInterval(() => {
      now.value = Date.now()
      
      if (isEnded.value) {
        clearInterval(timer)
        emit('end')
      }
    }, 1000)
  }
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.countdown-timer {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  
  &--urgent {
    background: linear-gradient(135deg, #ff4757 0%, #ff6b81 100%);
    animation: shake 0.5s infinite;
  }
}

.countdown-timer__label {
  font-size: 12px;
  font-weight: 500;
  opacity: 0.9;
}

.countdown-timer__time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.countdown-timer__block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 48px;
}

.countdown-timer__digit {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  font-size: 24px;
  font-weight: 700;
  font-family: 'Courier New', monospace;
}

.countdown-timer__unit {
  font-size: 10px;
  opacity: 0.8;
}

.countdown-timer__separator {
  font-size: 24px;
  font-weight: 700;
  opacity: 0.5;
  margin: 0 2px;
}

.countdown-timer__warning {
  font-size: 11px;
  font-weight: 600;
  animation: blink 0.8s infinite;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-2px);
  }
  75% {
    transform: translateX(2px);
  }
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>