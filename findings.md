# 调研记录

## 仓库现状
- 工作目录只有 `.git`，没有提交、远端或源码。

## 相似项目
- 待通过 GitHub 页面和公开搜索确认；仅复用通用架构思路，不复制不明来源代码或素材。

## 已确认
- 用户给定 GitHub 仓库为空仓库。
- 搜索 `spring boot async job nextjs` 仅发现通用 SmartCart 样例，无同题素材制作业务可直接复用。
- `@Async` 必须放在独立 Bean；已拆分 `TaskRunner`，避免同类自调用失效。
