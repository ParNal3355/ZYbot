package org.example.zybot.begin.other.Assistant;

import java.io.File;

//检测是否存在./data文件夹，以防止运行时出现数据库创建错误
public class DirectoryChecker {

    public static void ensureDirectoryCreated(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new RuntimeException("无法创建目录: " + path);
            }
        }
    }
}