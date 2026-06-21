<template>
  <div class="product-card" @click="$emit('click', product)">
    <div class="product-card__image-wrapper">
      <img 
        :src="product.image || 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=product%20photo%20white%20background&image_size=square'" 
        :alt="product.name"
        class="product-card__image"
      />
      <div class="product-card__promotions">
        <PromotionTag 
          v-for="promo in product.promotions" 
          :key="promo.type"
          :type="promo.type"
          :text="promo.text"
          :discount="promo.discount"
        />
      </div>
      <CountdownTimer 
        v-if="product.flashSaleEndTime"
        :end-time="product.flashSaleEndTime"
        label="限时抢"
        urgent-threshold="3600000"
        class="product-card__countdown"
      />
    </div>
    
    <div class="product-card__content">
      <h3 class="product-card__name">{{ product.name }}</h3>
      
      <div class="product-card__price-row">
        <div class="product-card__price">
          <span class="product-card__currency">¥</span>
          <span class="product-card__amount">{{ product.price }}</span>
        </div>
        <span v-if="product.originalPrice" class="product-card__original-price">
          ¥{{ product.originalPrice }}
        </span>
      </div>
      
      <div class="product-card__info">
        <span class="product-card__sales">已售 {{ product.sales }}件</span>
        <span class="product-card__rating">⭐ {{ product.rating }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import PromotionTag from './PromotionTag.vue'
import CountdownTimer from './CountdownTimer.vue'

defineProps({
  product: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])
</script>

<style scoped>
.product-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}

.product-card__image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%;
  background: #f8f9fa;
}

.product-card__image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-card__promotions {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-card__countdown {
  position: absolute;
  bottom: 8px;
  left: 8px;
  right: 8px;
}

.product-card__content {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-card__name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin: 0;
}

.product-card__price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.product-card__price {
  display: flex;
  align-items: baseline;
  color: #ff4757;
}

.product-card__currency {
  font-size: 14px;
  font-weight: 600;
}

.product-card__amount {
  font-size: 24px;
  font-weight: 700;
}

.product-card__original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.product-card__info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
</style>