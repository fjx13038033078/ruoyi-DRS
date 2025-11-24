package com.ruoyi.web.controller.documentary;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.documentary.domain.Documentary;
import com.ruoyi.documentary.service.DocumentaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 纪录片管理 Controller
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */

@RestController
@RequestMapping("/documentary/documentary")
@RequiredArgsConstructor
public class DocumentaryController extends BaseController {
    private final DocumentaryService documentaryService;

    /**
     * 获取所有纪录片列表
     *
     * @return 纪录片列表（分页）
     */
    @GetMapping("/listAll")
    public TableDataInfo listAllDocumentaries() {
        startPage();
        List<Documentary> allDocumentaries = documentaryService.getAllDocumentaries();
        return getDataTable(allDocumentaries);
    }

    /**
     * 根据纪录片ID获取纪录片详情
     *
     * @param documentaryId 纪录片ID
     * @return 纪录片详情
     */
    @GetMapping("/detail")
    public AjaxResult getDocumentaryById(@RequestParam Long documentaryId) {
        return AjaxResult.success(documentaryService.getDocumentaryById(documentaryId));
    }

    /**
     * 添加纪录片
     *
     * @param documentary 待添加的纪录片信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public AjaxResult addDocumentary(@RequestBody Documentary documentary) {
        return toAjax(documentaryService.addDocumentary(documentary));
    }

    /**
     * 更新纪录片信息
     *
     * @param documentary 待更新的纪录片信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public AjaxResult updateDocumentary(@RequestBody Documentary documentary) {
        return toAjax(documentaryService.updateDocumentary(documentary));
    }

    /**
     * 删除纪录片
     *
     * @param documentaryId 待删除的纪录片ID
     * @return 操作结果
     */
    @GetMapping("/delete")
    public AjaxResult deleteDocumentary(@RequestParam Long documentaryId) {
        return toAjax(documentaryService.deleteDocumentary(documentaryId));
    }
}

