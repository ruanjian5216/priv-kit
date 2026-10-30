## 全面屏与悬浮栏约束

- Activity 使用 `enableHyperEdgeToEdge()`，根 `HyperThemeConfig.useSystemSafeArea = false`。根画布、滚动视口和 WebView 保持全屏，正文可滚入顶部状态栏与底部 TabBar 后方。
- 禁止用固定高度、`Spacer`、根布局系统栏 padding、滚动容器外的栏高 padding、扣减屏幕高度或裁切，为状态栏、导航栏、悬浮 TabBar 建立永久占位；禁止把悬浮栏放进 `Column` 挤占正文。不得把 `HyperScaffold` 返回的上下净空直接用于滚动视口的 `Modifier.padding`。
- 顶部操作区由 `HyperNavBar` 或局部操作容器根据真实窗口 Insets 保护一次。首屏顶部净空与末尾底部净空只进入 `LazyColumn` / `LazyGrid` 的 `contentPadding` 或 `verticalScroll(...).padding(...)` 内部，随正文滚动；不能在视口外固定留空。
- 悬浮 TabBar 使用覆盖布局，周围透明并透传正文手势。需要末尾避让时使用宿主实测遮挡量，禁止用默认栏高、硬编码 dp 或状态栏高度估算永久占位。Web 宿主传入的安全区与遮挡量同样只用于网页滚动内容及局部操作区，不能缩短网页滚动视口。
- 保留按钮、导航控件、输入法面板、视频画面和编辑器等业务组件自身所需尺寸与 IME 避让；本限制针对系统栏与悬浮栏占位，不要求删除正常控件高度。修改后从布局与滚动链路核对，不能只凭高度值未变或编译成功判定正常。

# Priv Kit repository guidance

## Release procedure

When asked to publish a new version:

1. Modify only `CHANGELOG.md` and the version in `build.gradle.kts`.
2. Commit those two files, create the version tag, then push the commit and tag.
3. End the operation immediately after the pushes complete.

Do not run builds, tests, lint, Gradle tasks, CI monitoring, release polling,
registry verification, code review, issue or pull-request checks, artifact
inspection, or any other release-related operation. Do not modify any other
file during a release.

Read these files before changing product behavior or public APIs:

1. `docs/architecture.md`
2. The README of every affected Gradle module

The `docs` directory contains durable maintainer and AI-readable repository
documentation. It is not published as the project website.

The VitePress project root and published source directory are both `priv-website`.
Every public English Markdown page under `priv-website` must have a path-equivalent
Simplified Chinese page under `priv-website/zh`. Internal or AI-readable Markdown
belongs under `docs`, not `priv-website`. Run `pnpm check` and `pnpm build` from the
`priv-website` directory after changing public documentation.

User-facing copy in reusable product modules, including `:priv-ui`, must not
contain the product name `Priv Kit`. Describe the action or capability without
presenting the library as the host application. System-prompt text, dialogs,
notifications, accessibility text, and clipboard labels all count as
user-facing copy. Logs and diagnostics, documentation, repository metadata,
and the `:priv-sample` app's own branding are exempt. When changing
user-facing copy, sweep every locale and other non-log UI surfaces for
hard-coded `Priv Kit` references.

Declare every hidden Android API only in the `:hidden-api` module. Do not place
framework mirrors or stubs in product modules. Before adding or changing hidden
API declarations, use the project `android-api-diff` skill. The `:hidden-api`
module must not contain Java constant declarations, field initializers, or any
`final` modifier. Convert generated `public static final` fields to uninitialized
`public static` fields, and remove `final` from generated type and member
declarations. Hidden API methods whose return type is `void` must use an empty
method body instead of throwing an exception.

When a hidden API changes across Android maintenance releases that share the
same API level, implement runtime signature detection and dispatch in
`:priv-shared`; do not rely on `SDK_INT` alone. When the hidden interface is
absent on part of the supported API range, expose a narrow compat wrapper that
accepts an `IBinder` and keeps references to that interface out of lower-level
callers. Otherwise, keep compatibility logic on the corresponding hidden type.
Keep hidden API declarations limited to members required by the queried
framework API and compilation. Do not add empty private constructors or other
boilerplate merely to prevent instantiation.

Node.js, TypeScript, and SVG in the repository are limited to documentation and
repository tooling. Executable source files for this tooling must use the `.ts`
extension. The documentation site uses the default VitePress theme and must not
add custom theme overrides. Tailwind CSS is available throughout `priv-website`;
the site must not import Tailwind Preflight. The playground UI lives in Vue
single-file components (`.vue`) under `priv-website/playground`, with page-specific
styles scoped to those components. Thin English and Chinese Markdown pages may
mount it through `ClientOnly` with `layout: false`; VitePress builds all pages.
These technologies must not be added to Gradle product
modules, Android runtime artifacts, or public Android APIs.

Declare all external npm dependencies only in the repository root `package.json`
under `devDependencies`, including dependencies used by website/browser code.
Declare the `@priv-kit/playground` `workspace:*` link in `@priv-kit/website`'s
`dependencies`. Use `catalog:` for all external dependencies, with
their versions declared as caret (`^`) ranges in the default `catalog` in
`pnpm-workspace.yaml`. Keep internal package links as `workspace:*`.
Update the root manifest, catalog, and `pnpm-lock.yaml` together when changing
dependencies.

## 导航与 Hyper UI 接入约束

- UI 修改前先读同级库的[业务使用规则](../hyper-ui/vitepress/docs/mobile-guidelines.md)、[设计基线](../hyper-ui/vitepress/docs/style-reference.md)与相关组件 API，使用当前本地源码接口。
- Navbar 的滚动监听、采样、模糊和渐隐由 UI 库实现。共用页面宿主向 HyperNavBarPage.scrollState 提供主 ScrollState/LazyListState/LazyGridState，Navbar 自动读取库内作用域；不在各页面重复计算导航 scrolledUnder、添加导航专用 derivedStateOf/snapshotFlow、累计手势位移或复制模糊绘制。
- 首屏顶部净空由库测量实际导航区后提供。LazyColumn/LazyGrid 放入 contentPadding；普通 verticalScroll 的 padding 位于滚动容器内。禁止固定顶部占位截断采样、重复状态栏安全区、实色导航底板或业务裁切。
- 固定筛选/搜索区归入共用宿主的导航前景，正文仍可滚过导航后方。系统栏画布使用统一页面背景，卡片使用库内表面与阴影。
- 保留底栏控件自身的尺寸与交互；正文必须全屏覆盖，实测净空只进入滚动内容，禁止保留会截短视口的系统栏或悬浮栏占位。非滚动的播放画面、原生编辑视口及业务自身的位置保存/查询监听不属于导航重复代码，不擅自删除。
- 普通 Scaffold 或固定布局只接入 HyperNavBarScrollScope 并共享同一个主滚动状态，不借监听迁移重排页面；隐藏页或主列表被空状态替代时，用 active 停用该来源。派生导航状态与订阅只在库内维护，不把弹窗、网页或播放器的业务监听当作导航重复代码。
- 用户明确指定只改源码、测试由使用者负责时，不自动执行静态分析、检查脚本、构建、安装、运行或测试；执行结果只在对话中说明，不生成临时测试或辅助脚本。

- Hyper UI 每类组件只使用一套默认规格，禁止新增 small/default/large 尺寸档位或兼容入口。固定分段切换使用 HyperTabs，可左右滑动的分类筛选使用 HyperFilterBar；不恢复整条大高度分类胶囊。
