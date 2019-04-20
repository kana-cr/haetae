/*
 * betahouse.us
 * CopyRight (c) 2012 - 2019
 */
package us.betahouse.haetae.certificate.dal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.betahouse.haetae.certificate.builder.CertificateBOBuilder;
import us.betahouse.haetae.certificate.dal.model.SkillDO;
import us.betahouse.haetae.certificate.dal.repo.SkillDORepo;
import us.betahouse.haetae.certificate.dal.service.SkillRepoService;
import us.betahouse.haetae.certificate.enums.CertificateTypeEnum;
import us.betahouse.haetae.certificate.idfactory.BizIdFactory;
import us.betahouse.haetae.certificate.model.basic.CertificateBO;
import us.betahouse.util.enums.CommonResultCode;
import us.betahouse.util.exceptions.BetahouseException;
import us.betahouse.util.utils.CollectionUtils;
import us.betahouse.util.utils.LoggerUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author guofan.cp
 * @version : SkillRepoServiceImpl.java 2019/04/05 12:43 guofan.cp
 */
@Service
public class SkillRepoServiceImpl implements SkillRepoService {

    private final Logger LOGGER = LoggerFactory.getLogger(SkillRepoServiceImpl.class);

    @Autowired
    private SkillDORepo skillDORepo;
    @Autowired
    private BizIdFactory bizIdFactory;

    @Override
    public CertificateBO create(CertificateBO certificateBO) {
        if (StringUtils.isBlank(certificateBO.getCertificateId())) {
            certificateBO.setCertificateId(bizIdFactory.getSkillId());
        }
        return convert(skillDORepo.save(convert(certificateBO)));
    }

    @Override
    public void delete(String certificateId) {
        skillDORepo.deleteByCertificateId(certificateId);
    }

    @Override
    public void deleteByCertificateIdAndUserId(String certificateId, String userId) {
        skillDORepo.deleteByCertificateIdAndUserId(certificateId, userId);
    }

    @Override
    public CertificateBO modify(CertificateBO certificateBO) {
        SkillDO skillDO = skillDORepo.findByCertificateId(certificateBO.getCertificateId());
        if (skillDO == null) {
            LoggerUtil.error(LOGGER, "更新技能证书不存在 certificateId={0}", certificateBO.getCertificateId());
            throw new BetahouseException(CommonResultCode.ILLEGAL_PARAMETERS.getCode(), "更新证书不存在");
        }
        SkillDO skillDO1 = convert(certificateBO);
        if (skillDO1.getCertificateName() != null) {
            skillDO.setCertificateName(skillDO1.getCertificateName());
        }
        if (skillDO1.getCertificateNumber() != null) {
            skillDO.setCertificateNumber(skillDO1.getCertificateNumber());
        }
        if (skillDO1.getRank() != null) {
            skillDO.setRank(skillDO1.getRank());
        }
        if (skillDO1.getCertificatePublishTime() != null) {
            skillDO.setCertificatePublishTime(skillDO1.getCertificatePublishTime());
        }
        //更新额外信息
        if (skillDO1.getExtInfo() != null) {
            skillDO.setExtInfo(skillDO1.getExtInfo());
        }
        //更新证书状态
        if (skillDO1.getStatus() != null) {
            skillDO.setStatus(skillDO1.getStatus());
        }
        //更新修改时间
        if (skillDO1.getGmtModified() != null) {
            skillDO.setGmtModified(skillDO1.getGmtModified());
        }
        return convert(skillDORepo.save(skillDO));
    }

    @Override
    public CertificateBO queryByCertificateId(String certificateId) {
        return convert(skillDORepo.findByCertificateId(certificateId));
    }

    @Override
    public List<CertificateBO> queryByUserId(String userId) {
        return CollectionUtils.toStream(skillDORepo.findByUserId(userId))
                .filter(Objects::isNull)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateBO> queryByCertificateName(String certificateName) {
        return CollectionUtils.toStream(skillDORepo.findByCertificateName(certificateName))
                .filter(Objects::isNull)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public CertificateBO queryByCertificateIdAndUserId(String certificateId, String userId) {
        return convert(skillDORepo.findByCertificateIdAndUserId(certificateId, userId));
    }

    @Override
    public List<CertificateBO> queryByCertificateNameAndUserId(String certificateName, String userId) {
        return CollectionUtils.toStream(skillDORepo.findByCertificateNameAndUserId(certificateName, userId))
                .filter(Objects::isNull)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    /**
     * 资格证书DO2BO
     *
     * @param skillDO
     * @return
     */
    @SuppressWarnings("unchecked")
    public CertificateBO convert(SkillDO skillDO) {
        if (skillDO == null) {
            return null;
        }
        CertificateBOBuilder builder = CertificateBOBuilder.getInstance();
        builder.withCertificateId(skillDO.getCertificateId())
                .withCertificateName(skillDO.getCertificateName())
                .withCertificatePublishTime(skillDO.getCertificatePublishTime())
                .withCertificateType(CertificateTypeEnum.SKILL.getCode())
                .withCertificateNumber(skillDO.getCertificateNumber())
                .withStatus(skillDO.getStatus())
                .withRank(skillDO.getRank())
                .withExpirationTime(skillDO.getExpirationTime())
                .withExtInfo(JSONObject.parseObject(skillDO.getExtInfo(), Map.class));
        return builder.build();
    }

    /**
     * 资格证书BO2DO
     *
     * @param certificateBO
     * @return
     */
    public SkillDO convert(CertificateBO certificateBO) {
        if (certificateBO == null) {
            return null;
        }
        SkillDO skillDO = new SkillDO();
        skillDO.setCertificateId(certificateBO.getCertificateId());
        skillDO.setUserId(certificateBO.getUserId());
        skillDO.setCertificateName(certificateBO.getCertificateName());
        skillDO.setCertificateNumber(certificateBO.getCertificateNumber());
        skillDO.setCertificatePublishTime(certificateBO.getCertificatePublishTime());
        skillDO.setExpirationTime(certificateBO.getExpirationTime());
        skillDO.setExtInfo(JSON.toJSONString(certificateBO.getExtInfo()));
        skillDO.setStatus(certificateBO.getStatus());
        skillDO.setRank(certificateBO.getRank());
        return skillDO;
    }
}
