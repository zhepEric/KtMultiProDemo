# AGENTS.md — KtMultiProDemo

Kotlin Multiplatform（KMP）+ Compose Multiplatform 学习/演示项目，一套 Compose UI 代码同时运行在 Android、iOS、Desktop（JVM）、Web（wasmJs + JS）五个目标。

## 项目结构

```
KtMultiProDemo/
├── shared/          核心模块：commonMain 放共享业务逻辑 + Compose UI，各 *Main 目录放平台 actual 实现
│   └── src/{commonMain,commonTest,androidMain,androidHostTest,iosMain,iosTest,jvmMain,jvmTest,jsMain,wasmJsMain,webTest}
├── androidApp/      Android 入口（仅 MainActivity，引用 shared 的 App()）
├── desktopApp/      Desktop 入口（仅 main.kt）
├── webApp/          Web 入口（webMain/main.kt + index.html）
├── iosApp/          Xcode 工程（SwiftUI 壳，通过 Shared.framework 调用 Kotlin）
└── gradle/libs.versions.toml   版本目录：所有依赖版本统一在这里管理
```

- 各应用模块只做入口，业务代码一律放 `shared`
- `kotlin-js-store/wasm/yarn.lock` 必须提交，保证 Web 构建可复现

## 构建与运行

```bash
./gradlew :androidApp:assembleDebug                              # Android APK
./gradlew :desktopApp:hotRun --auto                              # Desktop（热重载，调 UI 首选）
./gradlew :desktopApp:run                                        # Desktop（标准）
./gradlew :webApp:wasmJsBrowserDevelopmentRun                    # Web 开发服务器 → http://localhost:8080（wasmJs，推荐）
./gradlew :webApp:jsBrowserDevelopmentRun                        # Web 开发服务器（JS 备用，端口同为 8080，先停掉上一个）
./gradlew :webApp:wasmJsBrowserDistribution                      # Web 生产包（webApp/build/dist/）
```

- iOS：在 Android Studio 先跑一次 `./gradlew :shared:assembleSharedDebugXCFramework`，再用 Xcode 打开 `iosApp/` 运行
- Web 目标首次构建若报 `build/wasm/yarn.lock` 缺失，先执行 `./gradlew :kotlinWasmNpmInstall`

## 测试

```bash
./gradlew :shared:jvmTest                      # Desktop
./gradlew :shared:testAndroidHostTest          # Android 宿主测试
./gradlew :shared:iosSimulatorArm64Test        # iOS 模拟器
./gradlew :shared:wasmJsTest                   # Web wasmJs
./gradlew :shared:jsTest                       # Web JS
```

共用逻辑测试写 `commonTest`（一套代码全平台跑）；平台相关逻辑写对应平台的 `*Test` 源码集。

## 约定

### 平台差异：expect / actual

- 公共声明放 `commonMain`（如 `expect fun getPlatform(): Platform`），各平台在 `*Main` 目录提供 `actual` 实现
- 禁止在 commonMain 用 `if (platform == ...)` 分支判断平台；新增平台能力一律走 expect/actual 抽象
- wasmJs 平台的 JS 互操作用 `@JsFun` + `external fun`（注解在 `kotlin` 包，函数须声明为 `external` 且无函数体），不要依赖 `kotlinx.browser`——wasmJsMain 未引入该依赖

### 依赖管理

- 所有版本和库坐标只进 `gradle/libs.versions.toml`，build 脚本里用 `libs.xxx` 引用，不在 build.gradle.kts 里写死版本号
- commonMain 只引多平台库（Ktor、kotlinx.serialization、SQLDelight/Room KMP 等）；JVM-only 库只能进 `jvmMain`/`androidMain`

### 提交信息（沿用团队规范）

```
[feature] 简短描述新功能
Fix: 简短描述修复
```

- 分支：`main`，直接推送
- 语言：中文描述

## 边界（不要做的事）

- 不要提交 `local.properties`、`build/`、`.idea/`（已被 .gitignore 覆盖，不要强行 `git add -f`）
- 不要在 iosApp 的 Xcode 工程里手动改 framework 引用路径，它由 Gradle 任务生成
- 不要升级 `libs.versions.toml` 里的 Kotlin / AGP / Compose Multiplatform 大版本，除非明确要跟进升级并全平台回归
- Web 开发服务器（8080 端口）验证完必须停止，不要留在后台运行
