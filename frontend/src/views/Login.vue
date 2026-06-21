<template>
  <div class="login">
    <div class="login__container">
      <div class="login__logo">🛒</div>
      <h1 class="login__title">电商购物平台</h1>
      <p class="login__subtitle">欢迎回来，请登录您的账户</p>
      
      <el-form 
        ref="formRef" 
        :model="form" 
        class="login__form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username" label="用户名">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
            prefix-icon="user"
            :disabled="loading"
          />
        </el-form-item>
        
        <el-form-item prop="password" label="密码">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            prefix-icon="lock"
            :disabled="loading"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item class="login__remember">
          <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
          <a href="#" class="login__forgot">忘记密码?</a>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            class="login__btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login__footer">
        <span>还没有账户?</span>
        <a href="#" class="login__register">立即注册</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store'
import { setToken, setRefreshToken } from '../utils/auth'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  
  loading.value = true
  
  try {
    const response = await userStore.login(form.username, form.password)
    
    if (response.accessToken) {
      setToken(response.accessToken)
      setRefreshToken(response.refreshToken)
      
      ElMessage.success('登录成功')
      
      setTimeout(() => {
        router.push('/')
      }, 1000)
    }
  } catch (error) {
    console.error('Login error:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login__container {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.login__logo {
  font-size: 64px;
  text-align: center;
  margin-bottom: 16px;
}

.login__title {
  font-size: 28px;
  font-weight: 700;
  text-align: center;
  color: #333;
  margin-bottom: 8px;
}

.login__subtitle {
  font-size: 14px;
  text-align: center;
  color: #999;
  margin-bottom: 32px;
}

.login__form {
  margin-bottom: 24px;
}

.login__remember {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login__forgot {
  color: #409eff;
  font-size: 14px;
  
  &:hover {
    text-decoration: underline;
  }
}

.login__btn {
  width: 100%;
}

.login__footer {
  text-align: center;
  color: #999;
  font-size: 14px;
}

.login__register {
  color: #409eff;
  margin-left: 4px;
  
  &:hover {
    text-decoration: underline;
  }
}
</style>