# 工作进度

## 2026-11-02
- 初始化项目计划文件。
- 确认本地仓库为空，准备从零搭建。
- 完成 Spring Boot/MyBatis/MySQL 模型、模板种子、素材校验、异步三阶段任务及失败模拟。
- 完成 Next.js 模板列表、筛选、上传预览、任务轮询、历史恢复和响应式样式。
- 完成 Docker Compose、Dockerfile、CI、README 与自制 SVG 素材。
- 验证：`mvn test package -q` 通过；`npm run typecheck` 与 `npm run build` 通过；`docker compose -p material-studio config` 通过。
- 当前机器 Docker Desktop 引擎未运行，因此未执行完整容器启动；Compose 配置已通过解析验证。

## 2026-11-03
- 开始参考 Magnific 优化现有素材制作页；其官网被安全过滤器拦截，Playwright CLI 启动失败，视觉策略限定为通用图像优先工作台并以本地已授权素材实现。
- 完成暗色图像优先首页、模板分类、详情上传工作台、任务状态列表和结果大图预览改版。
- 使用用户提供的本地图片实际验证两张素材上传；提交按钮按素材数量启用，任务从等待处理推进至成功，结果可预览。
- 新增 Vitest 状态逻辑测试，覆盖多任务按 ID 隔离、查询异常保留原状态、失败任务终态识别。
- 验证通过：前端 lint、typecheck、3 个 Vitest 用例和 production build；后端 `mvn test package`。
- 改版已提交并推送到 GitHub `main`，验收 commit 为 `94f97c2a`。
- 修正固定结果缺陷：Java 在结果整理阶段读取当前任务素材，生成内嵌上传图片的任务专属 SVG；新增测试验证两个上传文件均进入结果。
- 新增本地创作者档案选择器，身份写入 localStorage，所有任务接口携带 X-Demo-User，切换身份会取消旧请求并恢复该用户历史。
- 浏览器实测：creator-lin 上传两张不同图片后结果同时包含两图；刷新后身份和历史恢复；creator-mia 历史为空且无法查询 creator-lin 的任务（HTTP 404）。
- 将制作记录从首页拆分到 `/creations` 独立作品页；新增右侧个人中心侧栏、身份切换、任务统计和最近历史入口。
- 首页精选模板轮播每 5 秒自动切换，并提供前后切换和分页点；鼠标悬停或键盘聚焦时暂停。
- 清理本机 MySQL 中 9 条验收测试任务，确认三个演示身份当前历史均为 0，数据库种子不包含默认任务。
- 新增梦境海报、雨夜电影感、冰川幻想 3 个模板效果示例与对应 SVG 生成布局，模板总数扩充到 6 个。
- 前后端全量检查通过：Maven 测试/打包、前端 typecheck、lint、3 个 Vitest 用例和 production build；页面刷新后确认 Java 接口与首页均展示 6 个模板。
- 实测首页自动轮播会在约 5 秒后切换；通过 Java multipart 接口提交梦境海报任务，作品页恢复成功记录并正常打开竖版结果预览。
- 验证生成结果嵌入的图片字节哈希与本次上传源文件一致，证明新增模板没有返回固定示例图。
- 将原有头像、四格拼贴和明信片封面也升级为离线合成的 JPG 成品效果图，6 个模板现在全部展示处理后预览；三个演示身份的验收任务均已清理为 0。
- 最终验证通过：后端 `mvn clean test package`，前端 typecheck、lint、Vitest 和 production build；本地 3000/8080/3307 服务保持可用。
- 修复开发服务器偶发缺少 Next.js 分块的问题：开发缓存改用 `.next-dev`，生产与 Docker 构建继续使用 `.next`。
- 在 `next dev` 持续运行时完成 typecheck、Vitest 与 `next build`，`.next-dev` 和 `.next` 分别生成；随后刷新首页确认 6 个模板正常、无控制台错误。
