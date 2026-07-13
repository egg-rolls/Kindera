package kindergarten;

import kindergarten.util.InitDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 幼儿园管理系统 Web 应用入口
 *
 * @author 开发团队
 * @date 2026-07-08
 * @version 2.0
 * @description Spring Boot 启动类，启动时自动初始化数据库
 */
@SpringBootApplication
public class KindergartenApplication {

    public static void main(String[] args) {
        // 启动前初始化数据库
        new InitDatabase().init();
        SpringApplication.run(KindergartenApplication.class, args);
    }
}
