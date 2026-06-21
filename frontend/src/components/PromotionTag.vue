<template>
  <div class="promotion-tag" :class="{ 'promotion-tag--active': isActive }">
    <span class="promotion-tag__icon">{{ icon }}</span>
    <span class="promotion-tag__text">{{ text }}</span>
    <span v-if="discount" class="promotion-tag__discount">-{{ discount }}%</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'sale'
  },
  text: {
    type: String,
    default: ''
  },
  discount: {
    type: [Number, String],
    default: null
  },
  endTime: {
    type: [String, Number],
    default: null
  }
})

const icon = computed(() => {
  const icons = {
    sale: '🔥',
    new: '✨',
    hot: '🔥',
    flash: '⚡',
    recommended: '⭐'
  }
  return icons[props.type] || '🏷️'
})

const isActive = computed(() => {
  if (!props.endTime) return true
  const now = Date.now()
  return now < Number(props.endTime)
})
</script>

<style scoped>
.promotion-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  transition: all 0.3s ease;
  
  &--active {
    opacity: 1;
  }
  
  &:not(&--active) {
    opacity: 0.5;
  }
}

.promotion-tag__icon {
  font-size: 14px;
}

.promotion-tag__text {
  color: #fff;
}

.promotion-tag__discount {
  color: #fff;
  font-weight: 700;
}

.promotion-tag {
  &.promotion-tag--sale {
    background: linear-gradient(135deg, #ff4757 0%, #ff6b81 100%);
  }
  
  &.promotion-tag--new {
    background: linear-gradient(135deg, #2ed573 0%, #7bed9f 100%);
  }
  
  &.promotion-tag--hot {
    background: linear-gradient(135deg, #ffa502 0%, #ffcc00 100%);
    color: #333;
    .promotion-tag__text,
    .promotion-tag__discount {
      color: #333;
    }
  }
  
  &.promotion-tag--flash {
    background: linear-gradient(135deg, #a55eea 0%, #df78ef 100%);
    animation: pulse 1.5s infinite;
  }
  
  &.promotion-tag--recommended {
    background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(165, 94, 234, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 0 8px rgba(165, 94, 234, 0);
  }
}
</style>