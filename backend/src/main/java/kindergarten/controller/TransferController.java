package kindergarten.controller;

import kindergarten.entity.TransferLog;
import kindergarten.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调班管理控制器
 *
 * @author 开发团队
 * @date 2026-07-13
 * @version 1.0
 * @description 处理幼儿调班操作和调班记录查询接口
 */
@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    /**
     * 获取所有调班记录
     *
     * @return 调班记录列表
     */
    @GetMapping
    public Map<String, Object> getAllTransferLogs() {
        Map<String, Object> result = new HashMap<>();
        List<TransferLog> logs = transferService.getAllTransferLogs();
        result.put("success", true);
        result.put("data", logs);
        return result;
    }

    /**
     * 执行调班操作
     *
     * @param params 包含 childId, newClassId, operatorId, remark 的请求体
     * @return 调班结果
     */
    @PostMapping
    public Map<String, Object> transfer(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            int childId = (int) params.get("childId");
            int newClassId = (int) params.get("newClassId");
            int operatorId = (int) params.get("operatorId");
            String remark = (String) params.get("remark");

            String msg = transferService.transfer(childId, newClassId, operatorId, remark);
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
