package kindergarten.controller;

import kindergarten.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理班级人数、课程选课、年级分布、出勤率等统计接口
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取班级人数统计（含男女比例）
     *
     * @return 班级统计列表
     */
    @GetMapping("/classes")
    public Map<String, Object> getClassStatistics() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> stats = statisticsService.getClassStatistics();
        result.put("success", true);
        result.put("data", stats);
        return result;
    }

    /**
     * 获取课程选课统计
     *
     * @return 课程统计列表
     */
    @GetMapping("/courses")
    public Map<String, Object> getCourseStatistics() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> stats = statisticsService.getCourseStatistics();
        result.put("success", true);
        result.put("data", stats);
        return result;
    }

    /**
     * 获取年级分布统计
     *
     * @return 年级统计列表
     */
    @GetMapping("/grades")
    public Map<String, Object> getGradeStatistics() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> stats = statisticsService.getGradeStatistics();
        result.put("success", true);
        result.put("data", stats);
        return result;
    }

    /**
     * 获取各班出勤率对比
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 出勤率统计列表
     */
    @GetMapping("/attendance")
    public Map<String, Object> getAttendanceRateReport(
            @RequestParam String start,
            @RequestParam String end) {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            List<Map<String, Object>> stats = statisticsService.getAttendanceRateReport(startDate, endDate);
            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "日期格式错误，请使用yyyy-MM-dd格式");
        }
        return result;
    }
}
