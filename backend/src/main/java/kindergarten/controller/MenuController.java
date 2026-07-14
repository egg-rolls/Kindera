package kindergarten.controller;

import kindergarten.entity.Dish;
import kindergarten.entity.WeeklyMenu;
import kindergarten.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 食谱管理控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理菜品库和每周食谱管理接口
 */
@RestController
@RequestMapping("/api")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取所有菜品
     *
     * @return 菜品列表
     */
    @GetMapping("/dishes")
    public Map<String, Object> getAllDishes() {
        Map<String, Object> result = new HashMap<>();
        List<Dish> dishes = menuService.getAllDishes();
        result.put("success", true);
        result.put("data", dishes);
        return result;
    }

    /**
     * 添加菜品
     *
     * @param dish 菜品信息（JSON请求体）
     * @return 添加结果
     */
    @PostMapping("/dishes")
    public Map<String, Object> addDish(@RequestBody Dish dish) {
        Map<String, Object> result = new HashMap<>();
        String msg = menuService.addDish(dish);
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
     * 修改菜品
     *
     * @param id   菜品ID
     * @param dish 菜品信息（JSON请求体）
     * @return 修改结果
     */
    @PutMapping("/dishes/{id}")
    public Map<String, Object> updateDish(@PathVariable int id, @RequestBody Dish dish) {
        Map<String, Object> result = new HashMap<>();
        dish.setId(id);
        String msg = menuService.updateDish(dish);
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
     * 删除菜品
     *
     * @param id 菜品ID
     * @return 删除结果
     */
    @DeleteMapping("/dishes/{id}")
    public Map<String, Object> deleteDish(@PathVariable int id) {
        Map<String, Object> result = new HashMap<>();
        String msg = menuService.deleteDish(id);
        if (msg.contains("已删除")) {
            result.put("success", true);
            result.put("message", msg);
        } else {
            result.put("success", false);
            result.put("message", msg);
        }
        return result;
    }

    /**
     * 获取当前周食谱
     *
     * @return 本周食谱列表
     */
    @GetMapping("/menus/current")
    public Map<String, Object> getCurrentWeekMenu() {
        Map<String, Object> result = new HashMap<>();
        List<WeeklyMenu> menus = menuService.getCurrentWeekMenu();
        result.put("success", true);
        result.put("data", menus);
        return result;
    }

    /**
     * 获取指定周食谱
     *
     * @param weekStart 周一日期（可选，默认本周）
     * @return 食谱列表
     */
    @GetMapping("/menus")
    public Map<String, Object> getWeekMenu(@RequestParam(required = false) String weekStart) {
        Map<String, Object> result = new HashMap<>();
        List<WeeklyMenu> menus;
        if (weekStart != null && !weekStart.isEmpty()) {
            menus = menuService.getWeekMenu(LocalDate.parse(weekStart));
        } else {
            menus = menuService.getCurrentWeekMenu();
        }
        result.put("success", true);
        result.put("data", menus);
        return result;
    }

    /**
     * 排餐：为指定周某天某餐添加菜品
     *
     * @param params 包含 weekStart, dayOfWeek, mealType, dishId 的请求体
     * @return 排餐结果
     */
    @PostMapping("/menus")
    public Map<String, Object> arrangeMenu(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String weekStartStr = (String) params.get("weekStart");
            LocalDate weekStart = weekStartStr != null ? LocalDate.parse(weekStartStr)
                    : LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            int dayOfWeek = toInt(params.get("dayOfWeek"));
            int mealType = toInt(params.get("mealType"));
            int dishId = toInt(params.get("dishId"));

            String msg = menuService.arrangeMenu(weekStart, dayOfWeek, mealType, dishId);
            if (msg.contains("成功")) {
                result.put("success", true);
                result.put("message", msg);
            } else {
                result.put("success", false);
                result.put("message", msg);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "参数格式错误：" + e.getMessage());
        }
        return result;
    }

    /**
     * 复制上周食谱到本周
     *
     * @return 操作结果
     */
    @PostMapping("/menus/copy")
    public Map<String, Object> copyLastWeekMenu() {
        Map<String, Object> result = new HashMap<>();
        String msg = menuService.copyLastWeekMenu();
        if (msg.contains("已复制")) {
            result.put("success", true);
            result.put("message", msg);
        } else {
            result.put("success", false);
            result.put("message", msg);
        }
        return result;
    }

    /**
     * 删除单条食谱记录
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @DeleteMapping("/menus/{id}")
    public Map<String, Object> deleteMenu(@PathVariable int id) {
        Map<String, Object> result = new HashMap<>();
        String msg = menuService.deleteMenu(id);
        if (msg.contains("已删除")) {
            result.put("success", true);
            result.put("message", msg);
        } else {
            result.put("success", false);
            result.put("message", msg);
        }
        return result;
    }

    /** 安全的类型转换：Object → int */
    private int toInt(Object obj) {
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        throw new IllegalArgumentException("无法转换为整数：" + obj);
    }
}
