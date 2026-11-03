# 素材制作网站实施计划

## 目标
交付一个可由 Docker Compose 启动的 Next.js + Spring Boot + MySQL 素材制作演示系统，覆盖模板、素材上传、异步多步骤任务、历史记录、模拟失败与 README/CI。

## 阶段
- [complete] 阶段 1：仓库调研、边界设计与项目骨架
- [complete] 阶段 2：Java 后端、数据库与异步任务闭环
- [complete] 阶段 3：Next.js 前端交互与状态恢复
- [complete] 阶段 4：Compose、CI、测试与文档
- [complete] 阶段 5：集成验证与验收说明
- [complete] 阶段 6：参考 Magnific 优化素材工作台视觉与交互
- [complete] 阶段 7：提交推送并保持本地演示服务可访问
- [complete] 阶段 8：结果绑定上传素材并完善用户身份隔离
- [complete] 阶段 9：拆分作品页、个人中心侧栏与自动轮播
- [complete] 阶段 10：扩充结果示例模板与生成布局
- [complete] 阶段 11：隔离 Next.js 开发与生产构建缓存
- [complete] 阶段 12：分离模板示例与用户测试素材
- [complete] 阶段 13：替换低质量模板为成熟固定版式

## 当前阶段
全部阶段已完成

## Next Step
提交并推送成熟版式模板替换。

## 决策记录
- 用户身份先采用演示用户 ID（请求头可切换），避免引入不在范围内的账号系统；README 明确演示方式。
- 用户身份升级为浏览器本地持久化的创作者档案选择器；所有任务请求携带 `X-Demo-User`，切换身份时取消旧请求并重新加载个人记录。
- 任务执行采用 Spring `ThreadPoolTaskExecutor` + 数据库状态持久化；重启恢复不承诺并在 README 说明。

## 错误记录
| 错误 | 尝试 | 处理 |
|---|---:|---|
| 本地 Git 未配置 remote/无提交 | 1 | 继续从空仓库初始化实现，并通过浏览器检查用户给定 GitHub |
| git remote-https 不可用 | 1 | 改用浏览器访问 GitHub 页面进行只读调研 |
| Playwright CLI wrapper 在 Windows 无法启动 | 1 | 改用 Codex 内置浏览器自动化完成真实页面验收 |
| Docker Compose 构建拉取基础镜像超时 | 1 | 使用已有 MySQL 镜像并以本机 Java/Node 启动，完成完整业务链路验证 |
| Edge 自动化文件选择器无本地文件访问权限 | 1 | 保留可见上传流程检查，改用同一 Java multipart 接口验证文件校验、任务执行与结果绑定 |
| PowerShell `Invoke-RestMethod -Form` 将图片作为不符合模板的类型提交 | 1 | 改用 `curl -F` 显式声明 `image/jpeg`，任务受理并成功完成 |
| `git push` 连接 GitHub 443 超时 | 1 | 本地 commit 已完成，检查代理与网络后重试推送 |
| Next.js 开发服务缺少 `./138.js` 分块 | 1 | 确认由运行中的 `next dev` 与 `next build` 共用 `.next` 导致，改为开发环境使用 `.next-dev` |
