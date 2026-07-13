package kindergarten.controller;

import kindergarten.entity.Attendance;
import kindergarten.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤管理控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理考勤记录查询和录入接口
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 获取考勤记录
     * 支持按班级+日期查询，或按幼儿+日期范围查询
     *
     * @param classId  班级ID（可选）
     * @param date     日期（可选，配合classId使用）
     * @param childId  幼儿ID（可选）
     * @param start    开始日期（可选，配合childId使用）
     * @param end      结束日期（可选，配合childId使用）
     * @return 考勤记录列表
     */
    @GetMapping
    public Map<String, Object> getAttendance(
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer childId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        Map<String, Object> result = new HashMap<>();
        List<Attendance> attendances;

        if (childId != null && start != null && end != null) {
            // 按幼儿+日期范围查询
            attendances = attendanceService.getChildAttendance(
                    childId, LocalDate.parse(start), LocalDate.parse(end));
        } else if (classId != null && date != null) {
            // 按班级+日期查询
            attendances = attendanceService.getClassAttendance(classId, LocalDate.parse(date));
        } else {
            result.put("success", false);
            result.put("message", "请提供查询参数：classId+date 或 childId+start+end");
            return result;
        }

        result.put("success", true);
        result.put("data", attendances);
        return result;
    }

    /**
     * 记录单个幼儿考勤
     *
     * @param params 包含 childId, date, status, remark 的请求体
     * @return 记录结果
     */
    @PostMapping
    public Map<String, Object> recordAttendance(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            int childId = (int) params.get("childId");
            LocalDate date = LocalDate.parse((String) params.get("date"));
            int status = (int) params.get("status");
            String remark = (String) params.get("remark");

            String msg = attendanceService.recordAttendance(childId, date, status, remark);
            if (msg.contains("已记录")) {
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
     * 批量记录班级考勤
     *
     * @param params 包含 classId, date, status 的请求体
     * @return 记录结果
     */
    @PostMapping("/batch")
    public Map<String, Object> batchRecord(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            int classId = (int) params.get("classId");
            LocalDate date = LocalDate.parse((String) params.get("date"));
            int status = (int) params.get("status");

            int count = attendanceService.batchRecordByClass(classId, date, status);
            result.put("success", true);
            result.put("message", "批量记录成功，共" + count + "人");
            result.put("data", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "参数格式错误：" + e.getMessage());
        }
        return result;
    }
}
