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
     * 排餐：为指定周某天某餐添加菜品
     *
     * @param params 包含 weekStart, dayOfWeek, mealType, dishId 的请求体
     * @return 排餐结果
     */
    @PostMapping("/menus")
    public Map<String, Object> arrangeMenu(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDate weekStart = LocalDate.parse((String) params.get("weekStart"));
            int dayOfWeek = (int) params.get("dayOfWeek");
            int mealType = (int) params.get("mealType");
            int dishId = (int) params.get("dishId");

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
}
