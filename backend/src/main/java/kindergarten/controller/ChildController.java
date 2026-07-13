package kindergarten.controller;

import kindergarten.entity.Child;
import kindergarten.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 幼儿管理控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理幼儿学籍的增删改查接口
 */
@RestController
@RequestMapping("/api/children")
public class ChildController {

    @Autowired
    private ChildService childService;

    /**
     * 获取所有幼儿（支持按班级ID和姓名筛选）
     *
     * @param classId 班级ID（可选）
     * @param name    姓名关键字（可选）
     * @return 幼儿列表
     */
    @GetMapping
    public Map<String, Object> getAllChildren(
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) String name) {
        Map<String, Object> result = new HashMap<>();
        List<Child> children;
        if (name != null && !name.trim().isEmpty()) {
            children = childService.searchByName(name.trim());
        } else if (classId != null) {
            children = childService.getChildrenByClass(classId);
        } else {
            children = childService.getAllChildren();
        }
        result.put("success", true);
        result.put("data", children);
        return result;
    }

    /**
     * 根据ID获取幼儿详情
     *
     * @param id 幼儿ID
     * @return 幼儿信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getChildById(@PathVariable int id) {
        Map<String, Object> result = new HashMap<>();
        Child child = childService.getChildById(id);
        if (child != null) {
            result.put("success", true);
            result.put("data", child);
        } else {
            result.put("success", false);
            result.put("message", "幼儿不存在");
        }
        return result;
    }

    /**
     * 添加幼儿
     *
     * @param child 幼儿信息（JSON请求体）
     * @return 添加结果
     */
    @PostMapping
    public Map<String, Object> addChild(@RequestBody Child child) {
        Map<String, Object> result = new HashMap<>();
        String msg = childService.addChild(child);
        if (msg.contains("成功")) {
            result.put("success", true);
            result.put("message", msg);
        } else {
            result.put("success", false);
            result.put("message", msg);
        }
        return result;
    }

    /**
     * 修改幼儿信息
     *
     * @param id    幼儿ID
     * @param child 幼儿信息（JSON请求体）
     * @return 修改结果
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateChild(@PathVariable int id, @RequestBody Child child) {
        Map<String, Object> result = new HashMap<>();
        child.setId(id);
        String msg = childService.updateChild(child);
        if (msg.contains("成功")) {
            result.put("success", true);
            result.put("message", msg);
        } else {
            result.put("success", false);
            result.put("message", msg);
        }
        return result;
    }

    /**
     * 删除幼儿（软删除）
     *
     * @param id 幼儿ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteChild(@PathVariable int id) {
        Map<String, Object> result = new HashMap<>();
        String msg = childService.deleteChild(id);
        if (msg.contains("成功") || msg.contains("已标记")) {
            result.put("success", true);
            result.put("message", msg);
        } else {
            result.put("success", false);
            result.put("message", msg);
        }
        return result;
    }
}
