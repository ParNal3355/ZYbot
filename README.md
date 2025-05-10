# ZYbot - 跨场景QQ机器人

🌟 支持QQ频道/群聊/单聊的全场景机器人框架，基于官方API实现无缝消息互通

## 功能亮点
- 🚀 **全场景支持**：同时兼容QQ频道、QQ群聊、单聊及频道私信
- 📅 **智能作息**：早安晚安提醒（支持睡眠质量分析）
- 🔮 **趣味功能**：今日运势、随机事件、自定义词库互动
- ⚙️ **提示友好**：通过`/帮助`可查看20+内置指令
- 📦 **便携部署**：单JAR包+资源文件即可运行

## 运行要求
- JDK 17+

## 🚀 快速开始
1.克隆该项目
2.根据 [SimpleRobot-Bot配置文件部分](https://simbot.forte.love/component-qq-guild-bot-config.html) 配置机器人信息
3.启动项目

## 📦 打包部署
```bash
mvn clean package

# 放置在运行环境时，生成的target/ZYbot-*.jar文件需与data目录一起移动且同级：
.
├── ZYbot-0.0.1.jar
└── data/
    └──......
```

## 📜 协议声明
核心框架依赖 [simpler-robot](https://github.com/simple-robot/simpler-robot) (LGPL-3.0)
本项目遵循 [LGPL-3.0 协议](LICENSE)，您可以在根目录查看完整协议文本。
📌重要声明：
1. 您有权通过以下方式获取simpler-robot项目源代码：
   ```bash
   git clone https://github.com/simple-robot/simpler-robot.git
   ```