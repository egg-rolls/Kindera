package kindergarten.controller;

import kindergarten.entity.ClassInfo;
import kindergarten.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级管理控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理班级查询相关接口
 */
@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 获取所有班级
     *
     * @return 班级列表
     */
    @GetMapping
    public Map<String, Object> getAllClasses() {
        Map<String, Object> result = new HashMap<>();
        List<ClassInfo> classes = classService.getAllClasses();
        result.put("success", true);
        result.put("data", classes);
        return result;
    }
}
