package org.example.zybot;

import love.forte.simbot.component.qguild.message.ImageParser;
import love.forte.simbot.spring.EnableSimbot;
import org.example.zybot.begin.other.Assistant.DirectoryChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Scanner;


@EnableSimbot
@EnableScheduling
@SpringBootApplication
public class ZYbotApplication {

    public static void main(String[] args) {

        //指定jvm虚拟机使用UTF-8编码，防止中文乱码
        System.setProperty("file.encoding", "UTF-8");

        // ，禁用使用Base64上传图片时的日志警告
        ImageParser.disableBase64UploadWarn();

        DirectoryChecker.ensureDirectoryCreated("./data");

        SpringApplication.run(ZYbotApplication.class, args);

        //当输入stop时终止程序
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if ("stop".equalsIgnoreCase(command)) {
                System.exit(0);
            }
        }
    }

}
