# 灵感工坊（素材制作异步任务演示）

这是一个独立运行的素材制作网站示例，前端使用 Next.js App Router + React + TypeScript，后端使用 Java 21、Spring Boot 3.3.5、MyBatis 3.0.3，数据存储使用 MySQL 8.4。仓库原始状态为空；公开检索没有找到可直接复用的同题项目，因此业务代码和演示素材均在本仓库实现。

## 启动

需要 Docker Desktop。执行：

```bash
docker compose up --build
```

浏览器打开 <http://localhost:3000>，后端 API 在 <http://localhost:8080>。首次启动由 Spring SQL 初始化表和种子模板。上传文件和 MySQL 数据分别存放在 Compose named volume 中。

页面划分为两个业务入口：首页 `/` 用于自动轮播 6 个精选模板、分类筛选和提交制作；个人作品页 `/creations` 专门展示当前身份的任务状态、失败原因和成功结果。模板卡片和详情顶部展示的是“处理后效果示例”，帮助用户先判断版式效果；用户提交后仍会使用本次上传素材生成独立结果，不会直接返回示例图。右上角身份按钮打开个人中心侧栏，可切换身份、查看任务统计和最近制作，并进入完整作品历史。数据库初始化不会创建任何默认任务记录。

模板预览只用于展示固定处理规则，不使用用户上传的测试素材。6 个预览均由 `scripts/generate_preview_assets.py` 离线合成为 JPG；其中编辑杂志封面、复古剪贴簿和电影胶片故事板使用脚本程序化绘制的通用场景，不依赖外部图片。运行网站不依赖该脚本、外部图片服务或付费素材源，生成后的静态文件已提交到仓库。用户提供的图片仅用于本地上传验收，不提交进模板素材目录。

新增模板的方向参考了 [Adobe Express 杂志封面模板](https://www.adobe.com/express/templates/cover/magazine)、[Canva 剪贴簿模板](https://www.canva.com/scrapbooks/templates/) 和 [Canva Film Strip 模板](https://www.canva.com/templates/s/film-strip/) 的公开分类与常见版式思路，只借鉴“编辑刊头与标题层级”“纸张/拍立得/胶带”“连续胶片分镜”等通用设计语言；没有复制第三方模板文件、代码、图片或品牌素材。

本地开发也可以分别启动：先用 `docker compose up -d db` 启动映射到本机 3307 端口的数据库；后端进入 `backend`，设置 `DB_URL=jdbc:mysql://localhost:3307/material_app?...` 后运行 `mvn spring-boot:run`（或构建后 `java -jar target/material-backend-1.0.0.jar`）；前端进入 `frontend` 后运行 `npm install && npm run dev`。前端通过 `NEXT_PUBLIC_API_URL` 指向 Java API；后端通过 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 配置连接。

前端开发服务使用 `.next-dev`，生产构建使用 `.next`，因此本地验收时执行 `npm run build` 不会覆盖正在运行的开发服务器缓存。Dockerfile 仍从标准 `.next` 目录复制 standalone 产物。

## 业务边界与接口

- `GET /api/templates`、`GET /api/templates/{id}`：模板列表/详情。
- `POST /api/tasks`：multipart 参数 `templateId` 和重复的 `files`；服务端重新校验数量、MIME 类型和大小，写入本地存储后创建任务，返回 HTTP 202 和任务记录。
- `GET /api/tasks`、`GET /api/tasks/{id}`：按 `X-Demo-User` 请求头查询个人历史或单个任务；未传时使用 `demo-user`。

任务状态为 `QUEUED`、`PROCESSING`、`SUCCEEDED`、`FAILED`，阶段为 `ASSET_CHECK`、`GENERATING`、`PACKAGING`、`COMPLETED`。提交接口不等待生成完成。Java `@Async` 线程池顺序执行素材检查、模拟生成、结果整理，每一步先写入数据库再延迟；失败后立即终止并保存阶段和原因。前端仅轮询查询，不驱动状态推进；刷新页面会先加载历史，再继续跟踪未结束任务。

## 演示控制

Compose 默认 `TASK_DELAY_MS=1200`，可调大以观察处理中和多任务：

```bash
TASK_DELAY_MS=5000 docker compose up --build
```

指定阶段失败：`FAIL_STAGE=ASSET_CHECK`、`GENERATING` 或 `PACKAGING`（也接受连字符写法），例如 `FAIL_STAGE=GENERATING docker compose up --build`。失败只影响新创建的任务。可连续打开多个模板并提交，任务卡片相互独立。刷新浏览器即可演示服务端历史恢复。

身份采用轻量的“本地创作者档案”策略：点击页面右上角身份按钮会打开个人中心侧栏，可以在 `demo-user`、`creator-lin` 和 `creator-mia` 间切换，并查看该身份的任务统计、最近制作及完整历史入口。选择保存在浏览器 `localStorage`。所有任务创建、历史和单任务查询都携带 `X-Demo-User`；后端查询同时使用任务 ID 与用户 ID，因而不同档案的记录互相不可见。切换身份会取消旧身份的加载/轮询请求、清空当前列表，再从 Java 后端恢复新身份的历史。演示方法：使用“创作者 林”创建任务，切到“Mia”应看到独立空记录；刷新后仍保持当前档案。没有实现账号、密码、邮箱、付费或第三方登录，这些均不属于题目范围。

## 测试与交付

CI 位于 `.github/workflows/ci.yml`，push/PR 会执行 Java `mvn test package`，以及前端 lint、TypeScript 检查、Vitest 状态测试和生产构建。后端测试覆盖阶段顺序和失败终态；前端测试覆盖按任务 ID 合并、多任务互不串扰，以及查询异常保留原任务状态而不误判为执行失败。当前仓库未配置浏览器 E2E。

已知限制：服务重启不会自动恢复执行中线程，题目明确不要求；任务受理幂等、断点续跑和自动重试也未实现。生成不调用真实 AI，但不是固定结果：`结果整理` 阶段会读取当前任务保存的上传素材，按头像、明信片、四格拼贴、编辑杂志封面、复古剪贴簿或电影胶片故事板布局生成独立 SVG，结果文件持久化在上传数据卷的 `results` 目录。模板封面是效果预览素材，只用于帮助用户选择模板，不会作为用户任务结果。部署回滚可通过切换 Git commit 后重新执行 `docker compose up --build`，数据卷需按需保留或迁移。

## AI 工作流说明

本项目使用 AI 编程工具完成：先读取题目与仓库状态，检索公开仓库确认没有直接可复用实现，再按“数据模型/后端异步边界/前端状态/容器与 CI/文档”拆分实现；每阶段通过源码检查、Maven/npm 构建和 Compose 集成验证修正问题。例如在模板扩充阶段，先增加数据库种子和 Java 结果布局，再用真实上传文件核对结果内嵌字节哈希，最后通过浏览器检查轮播、详情和作品预览。没有复制第三方业务代码或受限素材。
