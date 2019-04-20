/*
 * betahouse.us
 * CopyRight (c) 2012 - 2019
 */
package us.betahouse.haetae.controller.certificate;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.betahouse.haetae.certificate.builder.CertificateBOBuilder;
import us.betahouse.haetae.certificate.manager.CertificateManager;
import us.betahouse.haetae.certificate.model.basic.CertificateBO;
import us.betahouse.haetae.common.log.LoggerName;
import us.betahouse.haetae.common.session.CheckLogin;
import us.betahouse.haetae.common.template.RestOperateCallBack;
import us.betahouse.haetae.common.template.RestOperateTemplate;
import us.betahouse.haetae.model.certificate.request.CertificateRestRequest;
import us.betahouse.haetae.serviceimpl.certificate.builder.CertificateRequestBuilder;
import us.betahouse.haetae.serviceimpl.certificate.service.CertificateManagerService;
import us.betahouse.haetae.serviceimpl.certificate.service.CertificateService;
import us.betahouse.haetae.serviceimpl.common.OperateContext;
import us.betahouse.haetae.utils.IPUtil;
import us.betahouse.haetae.utils.RestResultUtil;
import us.betahouse.util.common.Result;
import us.betahouse.util.enums.RestResultCode;
import us.betahouse.util.log.Log;
import us.betahouse.util.utils.AssertUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 证书接口
 *
 * @author guofan.cp
 * @version : CertificateController.java 2019/04/04 20:30 guofan.cp
 */
@RestController
@RequestMapping(value = "/certificate")
public class CertificateController {

    /**
     * 日志实体
     */
    private final Logger LOGGER = LoggerFactory.getLogger(CertificateController.class);

    /**
     * 证书详细信息
     */
    private final String DESCRIPTION = "description";

    @Autowired
    private CertificateManagerService certificateManagerService;
    @Autowired
    private CertificateService certificateService;

    /**
     * 创建资格证书
     *
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping
    @Log(loggerName = LoggerName.WEB_DIGEST)
    public Result<CertificateBO> createCertificate(@RequestBody CertificateRestRequest request, HttpServletRequest httpServletRequest) {
        //post提交json数据
        return RestOperateTemplate.operate(LOGGER, "新增证书记录", request, new RestOperateCallBack<CertificateBO>() {

            @Override
            public void before() {
                AssertUtil.assertNotNull(request, RestResultCode.ILLEGAL_PARAMETERS.getCode(), "请求体不能为空");
                AssertUtil.assertStringNotBlank(request.getCertificateType(), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "证书类型不能为空");
                AssertUtil.assertNotNull(request.getCertificatePublishTime(), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "证书发布时间不能为空");
                //获取 Extinfo 中 description信息
                AssertUtil.assertNotNull(request.getExtInfo().get(DESCRIPTION), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "证书详细信息不能为空");
                boolean validateTime = new Date(request.getCertificatePublishTime()).before(new Date());
                AssertUtil.assertTrue(validateTime, "证书发布时间必须早于当前时间");
            }

            @Override
            public Result<CertificateBO> execute() {
                OperateContext context = new OperateContext();
                context.setOperateIP(IPUtil.getIpAddr(httpServletRequest));
                CertificateRequestBuilder builder = CertificateRequestBuilder.getInstance()
                        .withCertificateName(request.getCertificateName())
                        .withCompetitionName(request.getCompetitionName())
                        .withUserID(request.getUserId())
                        .withCertificateOrganization(request.getCertificateOrganization())
                        .withCertificatePublishTime(request.getCertificatePublishTime())
                        .withCertificateType(request.getCertificateType())
                        .withType(request.getType())
                        .withRank(request.getRank())
                        .withWorkUserId(request.getWorkUserId())
                        .withExpirationTime(request.getExpirationTime())
                        .withTeamName(request.getTeamName())
                        .withTeacher(request.getTeacher())
                        .withCertificateNumber(request.getCertificateNumber())
                        .withExtInfo(request.getExtInfo());
                return RestResultUtil.buildSuccessResult(certificateManagerService.create(builder.build(), context), "创建证书记录成功");
            }
        });
    }

    /**
     * 修改证书信息  更新的话还要所有数据验证一遍 然后队友改了要批量操作 其他的话也没什么了 这边基本是啥都需要修改
     */
    @PutMapping
    @Log(loggerName = LoggerName.WEB_DIGEST)
    @CheckLogin
    public Result<CertificateBO> modifiyCertificate(@RequestBody CertificateRestRequest request, HttpServletRequest httpServletRequest) {
        return RestOperateTemplate.operate(LOGGER, "更改证书记录", request, new RestOperateCallBack<CertificateBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, RestResultCode.ILLEGAL_PARAMETERS.getCode(), "请求体不能为空");
                AssertUtil.assertNotNull(request.getCertificateId(), "证书id不能为空");
                AssertUtil.assertStringNotBlank(request.getCertificateType(), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "证书类型不能为空");
            }

            @Override
            public Result<CertificateBO> execute() {
                OperateContext context = new OperateContext();
                context.setOperateIP(IPUtil.getIpAddr(httpServletRequest));
                CertificateRequestBuilder builder = CertificateRequestBuilder.getInstance()
                        .withCertificateId(request.getCertificateId())
                        .withCertificateName(request.getCertificateName())
                        .withCompetitionName(request.getCompetitionName())
                        .withUserID(request.getUserId())
                        .withCertificateOrganization(request.getCertificateOrganization())
                        .withCertificatePublishTime(request.getCertificatePublishTime())
                        .withCertificateType(request.getCertificateType())
                        .withType(request.getType())
                        .withRank(request.getRank())
                        .withWorkUserId(request.getWorkUserId())
                        .withExpirationTime(request.getExpirationTime())
                        .withTeamName(request.getTeamName())
                        .withTeacher(request.getTeacher())
                        .withCertificateNumber(request.getCertificateNumber())
                        .withExtInfo(request.getExtInfo());
                return RestResultUtil.buildSuccessResult(certificateManagerService.update(builder.build(), context), "修改记录成功");
            }
        });
    }

    /**
     * 删除证书
     */
    @DeleteMapping
    @Log(loggerName = LoggerName.WEB_DIGEST)
    public Result deleteCertificate(@RequestBody CertificateRestRequest request, HttpServletRequest httpServletRequest) {
        return RestOperateTemplate.operate(LOGGER, "删除证书记录", request, new RestOperateCallBack<CertificateBO>() {

            @Override
            public void before() {
                AssertUtil.assertNotNull(request.getCertificateId(), "证书id不能为空");
                AssertUtil.assertNotNull(request.getCertificateType(), "证书类型不能为空");
                AssertUtil.assertNotNull(request.getUserId(), "学生id不能为空");
            }

            @Override
            public Result<CertificateBO> execute() {
                OperateContext context = new OperateContext();
                context.setOperateIP(IPUtil.getIpAddr(httpServletRequest));
                CertificateRequestBuilder builder = CertificateRequestBuilder.getInstance()
                        .withUserID(request.getUserId())
                        .withCertificateId(request.getCertificateId())
                        .withTeamId(request.getTeamId())
                        .withCertificateName(request.getCertificateName());
                certificateService.delete(builder.build(), context);
                return RestResultUtil.buildSuccessResult("删除记录成功");
            }
        });
    }

    /**
     * 根据证书id获取证书详细信息
     */
    @GetMapping(value = "/byCertificateId")
    @Log(loggerName = LoggerName.WEB_DIGEST)
    public Result byCertificateId(@RequestBody CertificateRestRequest request, HttpServletRequest httpServletRequest) {
        return RestOperateTemplate.operate(LOGGER, "根据证书id获取证书记录", request, new RestOperateCallBack<CertificateBO>() {

            @Override
            public void before() {
                AssertUtil.assertNotNull(request.getCertificateId(), "证书id不能为空");
            }

            @Override
            public Result<CertificateBO> execute() {
                return RestResultUtil.buildSuccessResult(certificateService.findByCertificateId(request.getCertificateId()), "获取记录成功");
            }
        });
    }

    /**
     * 根据用户id获取多条证书记录
     */
    @GetMapping(value = "/byUserId")
    @Log(loggerName = LoggerName.WEB_DIGEST)
    public Result byUserId(@RequestBody CertificateRestRequest request, HttpServletRequest httpServletRequest) {
        return RestOperateTemplate.operate(LOGGER, "根据用户id获取证书记录", request, new RestOperateCallBack<List<CertificateBO>>() {

            @Override
            public void before() {
                AssertUtil.assertNotNull(request.getUserId(), "用户id不能为空");
            }

            @Override
            public Result<List<CertificateBO>> execute() {
                return RestResultUtil.buildSuccessResult(certificateService.findByUserId(request.getUserId()), "获取记录成功");
            }
        });
    }
    /**
     * 盖章
     */
}
