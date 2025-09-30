package com.ruoyi.patient.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.patient.domain.AdverseReaction;
import com.ruoyi.patient.domain.Drug;
import com.ruoyi.patient.domain.Patient;
import com.ruoyi.patient.mapper.AdverseReactionMapper;
import com.ruoyi.patient.service.AdverseReactionService;
import com.ruoyi.patient.service.DrugService;
import com.ruoyi.patient.service.PatientService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 范佳兴
 * @date 2025/9/24 17:39
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdverseReactionServiceImpl  implements AdverseReactionService {
    private final AdverseReactionMapper adverseReactionMapper;

    private final DrugService drugService;

    private final ISysUserService sysUserService;

    private final PatientService patientService;

    private final ISysRoleService iSysRoleService;

    @Override
    public List<AdverseReaction> getAllAdverseReactions() {
        Long userId = SecurityUtils.getUserId();
        String role = iSysRoleService.selectStringRoleByUserId(userId);
        if (role.equalsIgnoreCase("admin") ){
            List<AdverseReaction> reactions = adverseReactionMapper.getAllAdverseReactions();
            fillExtraInfo(reactions);
            return reactions;
        } else if (role.equalsIgnoreCase("doctor")) {
            List<AdverseReaction> adverseReactionsByDoctorId = adverseReactionMapper.getAdverseReactionsByDoctorId(userId);
            fillExtraInfo(adverseReactionsByDoctorId);
            return adverseReactionsByDoctorId;
        } else {
            List<AdverseReaction> adverseReactionsByPatientId = adverseReactionMapper.getAdverseReactionsByPatientId(userId);
            fillExtraInfo(adverseReactionsByPatientId);
            return adverseReactionsByPatientId;
        }
    }

    @Override
    public AdverseReaction getAdverseReactionById(Long id) {
        AdverseReaction reaction = adverseReactionMapper.getAdverseReactionById(id);
        fillExtraInfo(reaction);
        return reaction;
    }

    @Override
    public List<AdverseReaction> getAdverseReactionsByPatientId(Long patientId) {
        List<AdverseReaction> reactions = adverseReactionMapper.getAdverseReactionsByPatientId(patientId);
        fillExtraInfo(reactions);
        return reactions;
    }

    @Override
    public List<AdverseReaction> getAdverseReactionsByDrugId(Long drugId) {
        List<AdverseReaction> reactions = adverseReactionMapper.getAdverseReactionsByDrugId(drugId);
        fillExtraInfo(reactions);
        return reactions;
    }

    @Override
    public boolean addAdverseReaction(AdverseReaction adverseReaction) {
        Long userId = SecurityUtils.getUserId();
        String role = iSysRoleService.selectStringRoleByUserId(userId);
        if (role.equalsIgnoreCase("doctor") || role.equalsIgnoreCase("admin")) {
            Long patientId = adverseReaction.getPatientId();
            Patient patientByUserId = patientService.getPatientByUserId(patientId);
            adverseReaction.setDoctorId(patientByUserId.getDoctorId());
        } else {
            adverseReaction.setPatientId(userId);
            Patient patientByUserId = patientService.getPatientByUserId(userId);
            adverseReaction.setDoctorId(patientByUserId.getDoctorId());
        }
        adverseReaction.setDoctorConfirmed(0);
        int rows = adverseReactionMapper.addAdverseReaction(adverseReaction);
        return rows > 0;
    }

    @Override
    public boolean updateAdverseReaction(AdverseReaction adverseReaction) {
        int rows = adverseReactionMapper.updateAdverseReaction(adverseReaction);
        return rows > 0;
    }

    @Override
    public boolean updateDoctorConfirmed(AdverseReaction adverseReaction) {
        int rows = adverseReactionMapper.updateAdverseReactionConfirm(adverseReaction);
        return rows > 0;
    }

    @Override
    public boolean deleteAdverseReaction(Long id) {
        int rows = adverseReactionMapper.deleteAdverseReaction(id);
        return rows > 0;
    }

    /**
     * 批量填充额外信息（例如药品名称、患者姓名等）
     *
     * @param reactions 不良反应列表
     */
    private void fillExtraInfo(List<AdverseReaction> reactions) {
        for (AdverseReaction reaction : reactions) {
            fillExtraInfo(reaction);
        }
    }

    /**
     * 填充单条不良反应记录的额外信息
     *
     * @param reaction 不良反应对象
     */
    private void fillExtraInfo(AdverseReaction reaction) {
        if (reaction == null) {
            return;
        }
        // 填充药品名称
        Drug drugById = drugService.getDrugById(reaction.getDrugId());
        reaction.setDrugName(drugById.getDrugName());
        // 填充患者姓名
        reaction.setPatientName(sysUserService.selectUserById(reaction.getPatientId()).getNickName());
        reaction.setDoctorName(sysUserService.selectUserById(reaction.getDoctorId()).getNickName());
    }
}
