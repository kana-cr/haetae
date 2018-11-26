/**
 * betahouse.us
 * CopyRight (c) 2012 - 2018
 */
package us.betahouse.haetae.serviceimpl.activity.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.betahouse.haetae.activity.dal.service.ActivityRepoService;
import us.betahouse.haetae.activity.manager.ActivityManager;
import us.betahouse.haetae.activity.model.ActivityBO;
import us.betahouse.haetae.activity.enums.ActivityStateEnum;
import us.betahouse.haetae.serviceimpl.activity.constant.ActivityExtInfoKey;
import us.betahouse.haetae.serviceimpl.activity.constant.ActivityPermType;
import us.betahouse.haetae.serviceimpl.activity.constant.PermExInfokey;
import us.betahouse.haetae.serviceimpl.activity.manager.ActivityOperateManager;
import us.betahouse.haetae.serviceimpl.activity.request.ActivityManagerRequest;
import us.betahouse.haetae.serviceimpl.activity.service.ActivityService;
import us.betahouse.haetae.serviceimpl.common.OperateContext;
import us.betahouse.haetae.serviceimpl.common.verify.VerifyPerm;
import us.betahouse.haetae.serviceimpl.user.enums.UserRoleCode;
import us.betahouse.haetae.user.dal.service.RoleRepoService;
import us.betahouse.haetae.user.dal.service.UserInfoRepoService;
import us.betahouse.haetae.user.manager.PermManager;
import us.betahouse.haetae.user.manager.UserManager;
import us.betahouse.haetae.user.model.basic.UserInfoBO;
import us.betahouse.haetae.user.model.basic.perm.PermBO;
import us.betahouse.haetae.user.request.PermManageRequest;
import us.betahouse.haetae.user.request.UserManageRequest;
import us.betahouse.haetae.user.user.builder.PermBOBuilder;
import us.betahouse.util.utils.AssertUtil;

import java.util.Collections;
import java.util.List;

/**
 * 活动服务实现
 *
 * @author MessiahJK
 * @version : ActivityServiceImpl.java 2018/11/22 20:56 MessiahJK
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityManager activityManager;

    @Autowired
    private PermManager permManager;

    @Autowired
    private ActivityRepoService activityRepoService;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserInfoRepoService userInfoRepoService;

    @Autowired
    private RoleRepoService roleRepoService;

    @Autowired
    private ActivityOperateManager activityOperateManager;

    @Override
    @VerifyPerm(permType = ActivityPermType.ACTIVITY_CREATE)
    @Transactional
    public ActivityBO create(ActivityManagerRequest request, OperateContext context) {
        // 创建活动
        ActivityBO activityBO = activityManager.create(request);


        // 创建权限
        PermBO permBO = PermBOBuilder.getInstance(ActivityPermType.ACTIVITY_STAMPER, request.getActivityName() + "_盖章权限").build();
        // 将活动id 冗余到权限拓展信息
        permBO.putExtInfo(PermExInfokey.ACTIVITY_ID, activityBO.getActivityId());
        // 构建请求
        PermManageRequest permManageRequest = new PermManageRequest();
        permManageRequest.setPermBO(permBO);
        permManageRequest.setUserId(Collections.singletonList(request.getUserId()));
        permBO = permManager.createPerm(permManageRequest);

        // 更新信息
        request.setActivityId(activityBO.getActivityId());
        request.putExtInfo(ActivityExtInfoKey.ACTIVITY_STAMP_PERM, permBO.getPermId());
        return activityManager.update(request);
    }

    @Override
    public List<ActivityBO> findAll(ActivityManagerRequest request, OperateContext context) {
        if (StringUtils.isBlank(request.getState())) {
            return activityManager.findAll();
        } else {
            ActivityStateEnum state = ActivityStateEnum.getByCode(request.getState());
            AssertUtil.assertNotNull(state, "活动状态不存在");
            return activityManager.findByState(state);
        }
    }

    @Override
    public ActivityBO update(ActivityManagerRequest request, OperateContext operateContext) {
        return activityManager.update(request);
    }

    @Override
    public ActivityBO operate(ActivityManagerRequest request, OperateContext operateContext) {
        return activityOperateManager.operate(request, operateContext);
    }

    @Override
    @VerifyPerm(permType = ActivityPermType.ACTIVITY_CREATE)
    @Transactional
    public void bindStamper(ActivityManagerRequest request, OperateContext context) {
        ActivityBO activity = activityRepoService.queryActivityByActivityId(request.getActivityId());
        AssertUtil.assertNotNull(activity, "活动不存在");
        String stampPermId = activity.fetchExtInfo(ActivityExtInfoKey.ACTIVITY_STAMP_PERM);
        AssertUtil.assertStringNotBlank(stampPermId, "活动没有盖章权限");

        String stamperStuId = request.fetchStamperStuId();

        UserInfoBO userInfo = userInfoRepoService.queryUserInfoByStuId(stamperStuId);
        AssertUtil.assertNotNull(userInfo, "用户不存在或该用户未绑定个人信息");


        UserManageRequest userManageRequest = new UserManageRequest();
        userManageRequest.setUserId(userInfo.getUserId());
        userManageRequest.setPermIds(Collections.singletonList(stampPermId));
        // 绑定 权限
        userManager.batchBindPerm(userManageRequest);

        // 绑定角色
        roleRepoService.userBindRolesByCode(userInfo.getUserId(), UserRoleCode.ACTIVITY_STAMPER);
    }
}
