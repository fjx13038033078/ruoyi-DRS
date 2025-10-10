# 页面认证工具使用指南

## 📋 概述

这是一个用于处理小程序页面登录状态和认证失败的通用工具。当 API 返回 401 状态码时，会自动显示友好的重新登录提示，无需在每个页面重复编写相同的逻辑。

## 🚀 快速开始

### 1. 引入工具

在需要认证处理的页面 JS 文件中引入：

```javascript
const { withAuth } = require('../../utils/page-auth')
```

### 2. 使用 withAuth 包装页面配置

将原来的 `Page({...})` 改为 `Page(withAuth({...}))`：

```javascript
// 修改前
Page({
  data: {
    list: []
  },
  // ...其他配置
})

// 修改后
const { withAuth } = require('../../utils/page-auth')

Page(withAuth({
  data: {
    list: []
  },
  // ...其他配置
}))
```

### 3. 在页面 JSON 中引入 auth-tip 组件

```json
{
  "usingComponents": {
    "auth-tip": "/components/auth-tip/auth-tip"
  }
}
```

### 4. 在页面 WXML 底部添加组件

```xml
<!-- 页面其他内容... -->

<!-- 认证失败提示 -->
<auth-tip show="{{showAuthTip}}" bind:relogin="handleReLogin"></auth-tip>
```

### 5. 在错误处理中调用 handleApiError

```javascript
async loadData() {
  this.setData({ loading: true })
  
  try {
    const res = await someApi.getData()
    // 处理成功响应...
  } catch (error) {
    // 使用统一的错误处理
    this.handleApiError(error)
  } finally {
    this.setData({ loading: false })
  }
}
```

## 📦 可用方法

withAuth 会自动为页面添加以下方法：

### checkLoginStatus()
检查本地登录状态，如果未登录则跳转到登录页。

```javascript
onShow() {
  if (this.checkLoginStatus()) {
    // 已登录，继续执行
    this.loadData()
  }
}
```

### handleApiError(error)
统一处理 API 错误。如果是 401 错误，自动显示认证提示；否则设置 error 信息。

```javascript
catch (error) {
  this.handleApiError(error)
}
```

### handleReLogin()
处理重新登录的逻辑，显示确认对话框后调用全局 logout()。

```javascript
// 这个方法通常由 auth-tip 组件自动调用，无需手动调用
```

### hideAuthTip()
隐藏认证提示（在特殊情况下使用）。

## 💡 完整示例

### my-page.js
```javascript
const app = getApp()
const myApi = require('../../api/myApi')
const { withAuth } = require('../../utils/page-auth')

Page(withAuth({
  data: {
    list: [],
    loading: false,
    error: null
  },

  onLoad() {
    this.checkLoginStatus()
    this.loadData()
  },

  onShow() {
    if (this.checkLoginStatus()) {
      // 如果有错误或显示认证提示，重新加载
      if (this.data.error || this.data.showAuthTip) {
        this.loadData()
      }
    }
  },

  async loadData() {
    if (!this.checkLoginStatus()) {
      return
    }

    this.setData({ loading: true, error: null })

    try {
      const res = await myApi.getList()
      
      if (res.code === 200) {
        this.setData({ list: res.data })
      } else {
        throw new Error(res.msg || '加载失败')
      }
    } catch (error) {
      // 使用统一的错误处理
      this.handleApiError(error)
    } finally {
      this.setData({ loading: false })
    }
  },

  // 重新加载
  retryLoad() {
    this.loadData()
  }
}))
```

### my-page.json
```json
{
  "usingComponents": {
    "navigation-bar": "/components/navigation-bar/navigation-bar",
    "auth-tip": "/components/auth-tip/auth-tip"
  },
  "navigationBarTitleText": "我的页面",
  "enablePullDownRefresh": true
}
```

### my-page.wxml
```xml
<navigation-bar title="我的页面" back="{{true}}"></navigation-bar>

<scroll-view class="scrollarea" scroll-y>
  <view class="container">
    <!-- 加载状态 -->
    <view class="loading-container" wx:if="{{loading}}">
      <text>加载中...</text>
    </view>

    <!-- 错误状态 -->
    <view class="error-container" wx:if="{{!loading && error}}">
      <text class="error-text">{{error}}</text>
      <button bindtap="retryLoad">重新加载</button>
    </view>

    <!-- 主要内容 -->
    <view wx:if="{{!loading && !error}}">
      <view wx:for="{{list}}" wx:key="id">
        {{item.name}}
      </view>
    </view>
  </view>
</scroll-view>

<!-- 认证失败提示 -->
<auth-tip show="{{showAuthTip}}" bind:relogin="handleReLogin"></auth-tip>
```

## 🎨 自定义样式

auth-tip 组件的样式可以在 `/components/auth-tip/auth-tip.wxss` 中修改。

## ⚙️ 工作原理

1. **全局拦截**：`utils/request.js` 拦截所有 401 响应，调用 `app.handleAuthFailure()`
2. **页面级处理**：`handleApiError()` 检测到 401 错误，设置 `showAuthTip = true`
3. **UI 显示**：auth-tip 组件显示友好的重新登录界面
4. **用户操作**：用户点击"立即重新登录"，清除状态并跳转登录页

## 📝 已更新的页面

以下页面已经使用了这个认证处理工具：

- ✅ `/pages/medical-record/medical-record` - 我的病历
- ✅ `/pages/drug-info/drug-info` - 药品信息
- ✅ `/pages/medication-check/medication-check` - 用药打卡
- ✅ `/pages/adverse-reaction/adverse-reaction` - 不良反应

## 🔧 注意事项

1. **不要重复定义方法**：如果页面已有 `checkLoginStatus` 等方法，withAuth 不会覆盖
2. **数据合并**：withAuth 会合并 data 中的 `showAuthTip` 字段
3. **request.js**：确保 `utils/request.js` 正确处理 401 状态码
4. **app.js**：确保 `app.js` 中有 `handleAuthFailure()` 方法

## 🐛 故障排除

### 问题：认证提示不显示

检查：
1. 是否在 JSON 中引入了 auth-tip 组件
2. 是否在 WXML 中添加了 `<auth-tip>` 标签
3. 是否在 catch 中调用了 `handleApiError(error)`

### 问题：点击重新登录没反应

检查：
1. auth-tip 组件的 `bind:relogin` 是否绑定到 `handleReLogin`
2. `app.js` 中的 `logout()` 方法是否正常工作

## 📚 更多资源

- 查看 `/utils/page-auth.js` 了解实现细节
- 查看 `/components/auth-tip/` 了解组件实现
- 查看已更新的页面作为参考示例

