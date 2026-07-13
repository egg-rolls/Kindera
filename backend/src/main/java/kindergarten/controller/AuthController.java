package kindergarten.controller;

import kindergarten.entity.User;
import kindergarten.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理用户登录认证相关接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param params 包含 username 和 password 的请求体
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String username = params.get("username");
        String password = params.get("password");

        User user = userService.login(username, password);
        if (user != null) {
            result.put("success", true);
            // 不返回密码
            user.setPassword(null);
            result.put("data", user);
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
}
