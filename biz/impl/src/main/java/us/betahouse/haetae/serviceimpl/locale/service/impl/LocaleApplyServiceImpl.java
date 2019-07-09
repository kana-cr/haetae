/*
 * betahouse.us
 * Copyright (c) 2012 - 2019
 */

package us.betahouse.haetae.serviceimpl.locale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.betahouse.haetae.locale.manager.LocaleApplyManager;
import us.betahouse.haetae.locale.model.basic.LocaleApplyBO;
import us.betahouse.haetae.locale.model.common.PageList;
import us.betahouse.haetae.locale.request.LocaleApplyRequest;
import us.betahouse.haetae.serviceimpl.common.OperateContext;
import us.betahouse.haetae.serviceimpl.locale.request.LocaleApplyManagerRequest;
import us.betahouse.haetae.serviceimpl.locale.service.LocaleApplyService;
import us.betahouse.util.utils.NumberUtils;

/**
 * @author NathanDai
 * @version :  2019-07-05 05:33 NathanDai
 */

@Service
public class LocaleApplyServiceImpl implements LocaleApplyService {

    @Autowired
    LocaleApplyManager localeApplyManager;

    /**
     * 创建场地申请
     *
     * @param request
     * @param context
     * @return
     */
    @Override
    public LocaleApplyBO create(LocaleApplyManagerRequest request, OperateContext context) {
        LocaleApplyBO localeApplyBO = localeApplyManager.create(request);
        return localeApplyBO;
    }

    /**
     * 更新场地申请状态
     *
     * @param request
     * @param context
     * @return
     */
    @Override
    public LocaleApplyBO update(LocaleApplyManagerRequest request, OperateContext context) {
        LocaleApplyBO localeApplyBO = localeApplyManager.update(request);
        return localeApplyBO;
    }

    /**
     * 查询场地申请
     *
     * @param request
     * @param context
     * @return
     */
    @Override
    public PageList<LocaleApplyBO> findAllByStatus(LocaleApplyManagerRequest request, OperateContext context) {
        //默认值 学期不限 状态不限 类型不限 第0页 每页十条 逆序
        String status = "";
        Integer page = 0;
        Integer limit = 10;
        String orderRule = "DESC";

        if (StringUtils.isNotBlank(request.getStatus())) {
            status = request.getStatus();
        }
        if (NumberUtils.isNotBlank(request.getPage())) {
            page = request.getPage();
        }
        if (NumberUtils.isNotBlank(request.getLimit())) {
            limit = request.getLimit();
        }
        if (StringUtils.isNotBlank(request.getOrderRule())) {
            //顺序
            String asc = "ASC";
            if (asc.equals(request.getOrderRule())) {
                orderRule = asc;
            }
        }
        LocaleApplyRequest re = new LocaleApplyRequest();
        re.setStatus(status);
        re.setPage(page);
        re.setLimit(limit);
        re.setOrderRule(orderRule);
        return localeApplyManager.findByStatus(re);
    }

    @Override
    public PageList<LocaleApplyBO> findAllByUserId(LocaleApplyManagerRequest request, OperateContext context) {
        //默认值 学期不限 状态不限 类型不限 第0页 每页十条 逆序
        String userId = "";
        Integer page = 0;
        Integer limit = 10;
        String orderRule = "DESC";

        if (StringUtils.isNotBlank(request.getUserId())) {
            userId = request.getUserId();
        }
        if (NumberUtils.isNotBlank(request.getPage())) {
            page = request.getPage();
        }
        if (NumberUtils.isNotBlank(request.getLimit())) {
            limit = request.getLimit();
        }
        if (StringUtils.isNotBlank(request.getOrderRule())) {
            //顺序
            String asc = "ASC";
            if (asc.equals(request.getOrderRule())) {
                orderRule = asc;
            }
        }
        LocaleApplyRequest re = new LocaleApplyRequest();
        re.setUserId(userId);
        re.setPage(page);
        re.setLimit(limit);
        re.setOrderRule(orderRule);
        return localeApplyManager.findByUserId(re);
    }

}
