package com.ruoyi.web.controller.documentary;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.documentary.domain.Storeup;
import com.ruoyi.documentary.service.StoreupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户行为记录管理 Controller
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */

@RestController
@RequestMapping("/documentary/storeup")
@RequiredArgsConstructor
public class StoreupController extends BaseController {
    private final StoreupService storeupService;

    /**
     * 获取所有行为记录列表
     *
     * @return 行为记录列表（分页）
     */
    @GetMapping("/listAll")
    public TableDataInfo listAllStoreups() {
        startPage();
        List<Storeup> allStoreups = storeupService.getAllStoreupsWithDocumentaryName();
        return getDataTable(allStoreups);
    }

    /**
     * 根据行为记录ID获取行为记录详情
     *
     * @param storeupId 行为记录ID
     * @return 行为记录详情
     */
    @GetMapping("/detail")
    public AjaxResult getStoreupById(@RequestParam Long storeupId) {
        return AjaxResult.success(storeupService.getStoreupById(storeupId));
    }

    /**
     * 根据用户ID和操作类型获取行为记录列表
     *
     * @param userId 用户ID
     * @param actionType 操作类型（1-查看，2-访问，3-收藏）
     * @return 行为记录列表（分页）
     */
    @GetMapping("/listByUserAndType")
    public TableDataInfo getStoreupsByUserIdAndActionType(@RequestParam Long userId, 
                                                           @RequestParam(required = false) Integer actionType) {
        startPage();
        List<Storeup> storeups = storeupService.getStoreupsByUserIdAndActionType(userId, actionType);
        return getDataTable(storeups);
    }

    /**
     * 根据用户ID获取收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表（分页）
     */
    @GetMapping("/collections")
    public TableDataInfo getCollectionsByUserId(@RequestParam Long userId) {
        startPage();
        List<Storeup> collections = storeupService.getCollectionsByUserId(userId);
        return getDataTable(collections);
    }

    /**
     * 添加行为记录
     *
     * @param storeup 待添加的行为记录信息
     * @return 操作结果
     */
    @Log(title = "用户行为记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addStoreup(@RequestBody Storeup storeup) {
        return toAjax(storeupService.addStoreup(storeup));
    }

    /**
     * 更新行为记录信息
     *
     * @param storeup 待更新的行为记录信息
     * @return 操作结果
     */
    @Log(title = "用户行为记录", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult updateStoreup(@RequestBody Storeup storeup) {
        return toAjax(storeupService.updateStoreup(storeup));
    }

    /**
     * 删除行为记录
     *
     * @param storeupId 待删除的行为记录ID
     * @return 操作结果
     */
    @Log(title = "用户行为记录", businessType = BusinessType.DELETE)
    @GetMapping("/delete")
    public AjaxResult deleteStoreup(@RequestParam Long storeupId) {
        return toAjax(storeupService.deleteStoreup(storeupId));
    }

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param documentaryId 纪录片ID
     * @return 操作结果
     */
    @Log(title = "用户收藏", businessType = BusinessType.DELETE)
    @GetMapping("/cancelCollection")
    public AjaxResult cancelCollection(@RequestParam Long userId, @RequestParam Long documentaryId) {
        return toAjax(storeupService.cancelCollection(userId, documentaryId));
    }
}

