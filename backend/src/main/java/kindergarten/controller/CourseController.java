package kindergarten.controller;

import kindergarten.entity.ChildCourse;
import kindergarten.entity.Course;
import kindergarten.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理课程的增删改查、选课、退课接口
 */
@RestController
@RequestMapping("/api")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 获取所有课程
     *
     * @return 课程列表
     */
    @GetMapping("/courses")
    public Map<String, Object> getAllCourses() {
        Map<String, Object> result = new HashMap<>();
        List<Course> courses = courseService.getAllCourses();
        result.put("success", true);
        result.put("data", courses);
        return result;
    }

    /**
     * 添加课程
     *
     * @param course 课程信息（JSON请求体）
     * @return 添加结果
     */
    @PostMapping("/courses")
    public Map<String, Object> addCourse(@RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        String msg = courseService.addCourse(course);
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
     * 修改课程
     *
     * @param id     课程ID
     * @param course 课程信息（JSON请求体）
     * @return 修改结果
     */
    @PutMapping("/courses/{id}")
    public Map<String, Object> updateCourse(@PathVariable int id, @RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        course.setId(id);
        String msg = courseService.updateCourse(course);
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
     * 删除课程
     *
     * @param id 课程ID
     * @return 删除结果
     */
    @DeleteMapping("/courses/{id}")
    public Map<String, Object> deleteCourse(@PathVariable int id) {
        Map<String, Object> result = new HashMap<>();
        String msg = courseService.deleteCourse(id);
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
     * 获取幼儿的选课列表
     *
     * @param childId 幼儿ID
     * @return 选课列表
     */
    @GetMapping("/children/{childId}/courses")
    public Map<String, Object> getChildCourses(@PathVariable int childId) {
        Map<String, Object> result = new HashMap<>();
        List<ChildCourse> courses = courseService.getChildCourses(childId);
        result.put("success", true);
        result.put("data", courses);
        return result;
    }

    /**
     * 为幼儿选课
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 选课结果
     */
    @PostMapping("/children/{childId}/courses/{courseId}")
    public Map<String, Object> selectCourse(@PathVariable int childId, @PathVariable int courseId) {
        Map<String, Object> result = new HashMap<>();
        String msg = courseService.selectCourse(childId, courseId);
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
     * 获取课程的学员列表
     *
     * @param courseId 课程ID
     * @return 学员列表
     */
    @GetMapping("/courses/{courseId}/students")
    public Map<String, Object> getCourseStudents(@PathVariable int courseId) {
        Map<String, Object> result = new HashMap<>();
        List<ChildCourse> students = courseService.getCourseStudents(courseId);
        result.put("success", true);
        result.put("data", students);
        return result;
    }

    /**
     * 为幼儿退课
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 退课结果
     */
    @DeleteMapping("/children/{childId}/courses/{courseId}")
    public Map<String, Object> dropCourse(@PathVariable int childId, @PathVariable int courseId) {
        Map<String, Object> result = new HashMap<>();
        String msg = courseService.dropCourse(childId, courseId);
        if (msg.contains("成功")) {
            result.put("success", true);
            result.put("message", msg);
        } else {
            result.put("success", false);
            result.put("message", msg);
        }
        return result;
    }
}
