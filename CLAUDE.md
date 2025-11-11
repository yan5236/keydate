# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个名为 "Key date" 的 Android 应用，使用 Kotlin 和 Jetpack Compose 构建。应用包名为 `com.slcatwujian.keydate`。

## 技术栈

- **语言**: Kotlin 2.0.21
- **构建工具**: Gradle (Kotlin DSL)
- **UI 框架**: Jetpack Compose (Material 3)
- **最小 SDK**: 26 (Android 8.0)
- **目标 SDK**: 36
- **JVM 目标**: Java 11

## 常用命令

### 构建与运行
```bash
# 清理项目
./gradlew clean

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease

# 安装并运行 Debug 版本到设备
./gradlew installDebug

# 构建并运行所有测试
./gradlew build
```

### 测试
```bash
# 运行所有单元测试
./gradlew test

# 运行所有 instrumented 测试（需要连接设备或模拟器）
./gradlew connectedAndroidTest

# 运行特定的单元测试
./gradlew test --tests com.slcatwujian.keydate.ExampleUnitTest

# 运行特定的 instrumented 测试
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.slcatwujian.keydate.ExampleInstrumentedTest
```

### 代码检查
```bash
# 检查 Kotlin 代码风格
./gradlew lintDebug

# 生成 lint 报告
./gradlew lint
```

## 项目结构

### 核心架构
- **app/src/main/java/com/slcatwujian/keydate/**
  - `MainActivity.kt`: 应用主入口，使用 Compose 设置内容
  - `ui/theme/`: Material 3 主题配置
    - `Color.kt`: 颜色定义
    - `Theme.kt`: 主题配置
    - `Type.kt`: 字体排版配置

### 依赖管理
项目使用 Gradle Version Catalog (`gradle/libs.versions.toml`) 管理所有依赖版本，确保版本一致性。

### UI 开发
- 应用使用 Jetpack Compose 构建 UI
- MainActivity 启用了 Edge-to-Edge 显示
- 所有 Composable 函数应添加 `@Preview` 注解以便于预览
- 使用 Material 3 设计系统

## 开发注意事项

### 模块化原则
- 新功能应考虑模块化设计，便于维护和测试
- UI 组件应拆分为独立的 Composable 函数

### 代码风格
- 所有 Kotlin 文件应包含适当的注释（除非代码逻辑非常简单明了）
- 遵循 Kotlin 编码规范
- 使用有意义的变量和函数命名

### 测试
- `app/src/test/`: 单元测试（JUnit）
- `app/src/androidTest/`: Instrumented 测试（需要 Android 环境）
