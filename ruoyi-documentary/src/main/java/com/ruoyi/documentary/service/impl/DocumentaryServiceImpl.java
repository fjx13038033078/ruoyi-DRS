package com.ruoyi.documentary.service.impl;

import com.ruoyi.documentary.domain.Documentary;
import com.ruoyi.documentary.mapper.DocumentaryMapper;
import com.ruoyi.documentary.service.DocumentaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * DocumentaryService 实现类
 *
 * @Author 范佳兴
 * @date 2024/11/24
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentaryServiceImpl implements DocumentaryService {
    private final DocumentaryMapper documentaryMapper;

    @Override
    public List<Documentary> getAllDocumentaries(Documentary documentary) {
        return documentaryMapper.getAllDocumentaries(documentary);
    }

    @Override
    public Documentary getDocumentaryById(Long documentaryId) {
        return documentaryMapper.getDocumentaryById(documentaryId);
    }

    @Override
    public boolean addDocumentary(Documentary documentary) {
        int rows = documentaryMapper.addDocumentary(documentary);
        return rows > 0;
    }

    @Override
    public boolean updateDocumentary(Documentary documentary) {
        int rows = documentaryMapper.updateDocumentary(documentary);
        return rows > 0;
    }

    @Override
    public boolean deleteDocumentary(Long documentaryId) {
        int rows = documentaryMapper.deleteDocumentary(documentaryId);
        return rows > 0;
    }
}

