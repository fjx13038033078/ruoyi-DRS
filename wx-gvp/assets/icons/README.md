# TabBar 图标说明

## 当前状态
已移除图标配置，使用纯文字显示tabBar。

## 如果您想要添加图标，有以下选择：

### 方案1：使用Emoji图标
在app.json的tabBar配置中添加：
```json
{
  "pagePath": "pages/medical-record/medical-record",
  "text": "我的病历",
  "iconPath": "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiByeD0iNCIgZmlsbD0iIzY2N2VlYSIvPgo8dGV4dCB4PSIyMCIgeT0iMjYiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyMCIgZmlsbD0id2hpdGUiIHRleHQtYW5jaG9yPSJtaWRkbGUiPuWbvueJhzwvdGV4dD4KPC9zdmc+",
  "selectedIconPath": "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiByeD0iNCIgZmlsbD0iIzUyYzQxYSIvPgo8dGV4dCB4PSIyMCIgeT0iMjYiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyMCIgZmlsbD0id2hpdGUiIHRleHQtYW5jaG9yPSJtaWRkbGUiPuWbvueJhzwvdGV4dD4KPC9zdmc+"
}
```

### 方案2：使用真实PNG图标
1. 准备40x40像素的PNG图标文件
2. 将图标文件放在 `assets/icons/` 目录下
3. 在app.json中配置图标路径

### 方案3：使用在线图标库
推荐使用：
- [Iconfont](https://www.iconfont.cn/)
- [Feather Icons](https://feathericons.com/)
- [Heroicons](https://heroicons.com/)

## 图标规格要求
- 尺寸：40x40像素
- 格式：PNG
- 背景：透明
- 颜色：单色（微信会自动处理选中状态的颜色变化）
