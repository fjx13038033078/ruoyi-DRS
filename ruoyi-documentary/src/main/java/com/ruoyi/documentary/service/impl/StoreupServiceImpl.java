package com.ruoyi.documentary.service.impl;

import com.ruoyi.documentary.domain.Storeup;
import com.ruoyi.documentary.mapper.StoreupMapper;
import com.ruoyi.documentary.service.StoreupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * StoreupService 实现类
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreupServiceImpl implements StoreupService {
    private final StoreupMapper storeupMapper;

    @Override
    public List<Storeup> getAllStoreupsWithDocumentaryName() {
        return storeupMapper.getAllStoreupsWithDocumentaryName();
    }

    @Override
    public Storeup getStoreupById(Long storeupId) {
        return storeupMapper.getStoreupById(storeupId);
    }

    @Override
    public List<Storeup> getStoreupsByUserIdAndActionType(Long userId, Integer actionType) {
        return storeupMapper.getStoreupsByUserIdAndActionType(userId, actionType);
    }

    @Override
    public List<Storeup> getCollectionsByUserId(Long userId) {
        return storeupMapper.getCollectionsByUserId(userId);
    }

    @Override
    public boolean addStoreup(Storeup storeup) {
        // 只对收藏操作（actionType=3）进行重复检查
        // 查看（actionType=1）和访问（actionType=2）可以重复记录
        if (storeup.getActionType() != null && storeup.getActionType() == 3) {
            Long existingId = storeupMapper.checkStoreupExists(
                storeup.getUserId(), 
                storeup.getDocumentaryId(), 
                storeup.getActionType()
            );
            
            if (existingId != null) {
                log.warn("用户{}已经收藏过纪录片{}，不重复添加", 
                    storeup.getUserId(), storeup.getDocumentaryId());
                return false;
            }
        }
        
        // 直接插入记录（查看和访问可以重复）
        int rows = storeupMapper.addStoreup(storeup);
        return rows > 0;
    }

    @Override
    public boolean updateStoreup(Storeup storeup) {
        int rows = storeupMapper.updateStoreup(storeup);
        return rows > 0;
    }

    @Override
    public boolean deleteStoreup(Long storeupId) {
        int rows = storeupMapper.deleteStoreup(storeupId);
        return rows > 0;
    }

    @Override
    public boolean checkStoreupExists(Long userId, Long documentaryId, Integer actionType) {
        Long existingId = storeupMapper.checkStoreupExists(userId, documentaryId, actionType);
        return existingId != null;
    }

    @Override
    public boolean cancelCollection(Long userId, Long documentaryId) {
        int rows = storeupMapper.deleteStoreupByUserAndDocumentary(userId, documentaryId);
        return rows > 0;
    }
}

