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
- [in_progress] 阶段 7：提交推送并保持本地演示服务可访问

## 当前阶段
阶段 7

## Next Step
提交并推送验证通过的改版，随后恢复本地演示服务。

## 决策记录
- 用户身份先采用演示用户 ID（请求头可切换），避免引入不在范围内的账号系统；README 明确演示方式。
- 任务执行采用 Spring `ThreadPoolTaskExecutor` + 数据库状态持久化；重启恢复不承诺并在 README 说明。

## 错误记录
| 错误 | 尝试 | 处理 |
|---|---:|---|
| 本地 Git 未配置 remote/无提交 | 1 | 继续从空仓库初始化实现，并通过浏览器检查用户给定 GitHub |
| git remote-https 不可用 | 1 | 改用浏览器访问 GitHub 页面进行只读调研 |
| Playwright CLI wrapper 在 Windows 无法启动 | 1 | 改用 Codex 内置浏览器自动化完成真实页面验收 |
| Docker Compose 构建拉取基础镜像超时 | 1 | 使用已有 MySQL 镜像并以本机 Java/Node 启动，完成完整业务链路验证 |
