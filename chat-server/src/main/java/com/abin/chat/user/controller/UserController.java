package com.abin.chat.user.controller;


import com.abin.chat.common.constant.MQConstant;
import com.abin.chat.common.constant.RedisKey;
import com.abin.chat.common.domain.dto.LoginMessageDTO;
import com.abin.chat.common.domain.vo.response.ApiResult;
import com.abin.chat.common.utils.AssertUtil;
import com.abin.chat.common.utils.RedisUtils;
import com.abin.chat.common.utils.RequestHolder;
import com.abin.chat.user.dao.UserDao;
import com.abin.chat.user.domain.dto.ItemInfoDTO;
import com.abin.chat.user.domain.dto.SummeryInfoDTO;
import com.abin.chat.user.domain.entity.User;
import com.abin.chat.user.domain.enums.RoleEnum;
import com.abin.chat.user.domain.vo.request.user.*;
import com.abin.chat.user.domain.vo.response.user.BadgeResp;
import com.abin.chat.user.domain.vo.response.user.UserInfoResp;
import com.abin.chat.user.service.IRoleService;
import com.abin.chat.user.service.UserService;
import com.abin.mallchat.transaction.service.MQProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户管理相关接口")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private UserDao userDao;
    @Autowired
    private MQProducer mqProducer;

    @PostMapping("/public/login")
    @Transactional
    public ApiResult<UserInfoResp> login(@RequestParam(defaultValue = "1000") String openid, @RequestParam(defaultValue = "1000") Integer loginCode){
        User user = userDao.getByOpenId(openid);
        //如果已经注册,直接登录成功
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getAvatar())) {
            mqProducer.sendMsg(MQConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), loginCode));
            return null;
        }

        //user为空先注册,手动生成,以保存uid
        if (Objects.isNull(user)) {
            user = User.builder().openId(openid).build();
            userService.register(user);
        }
        //在redis中保存openid和场景code的关系，后续才能通知到前端,旧版数据没有清除,这里设置了过期时间
        RedisUtils.set(RedisKey.getKey(RedisKey.OPEN_ID_STRING, openid), loginCode, 60, TimeUnit.MINUTES);
        //授权流程,给用户发送授权消息，并且异步通知前端扫码成功,等待授权

        user = userDao.getByOpenId(openid);
        //更新用户信息
        if (StringUtils.isEmpty(user.getName())) {
            user.setName(new Date().getTime()+"");
            user.setAvatar("http://mms1.baidu.com/it/u=1979830414,2984779047&fm=253&app=138&f=JPEG&fmt=auto&q=75?");
            user.setSex(0);
            userDao.updateById(user);
        }
        //找到对应的code
        Integer code = RedisUtils.get(RedisKey.getKey(RedisKey.OPEN_ID_STRING, openid), Integer.class);
        //发送登录成功事件
        mqProducer.sendMsg(MQConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), code));
        return  null;
    }

    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PostMapping("/public/summary/userInfo/batch")
    @ApiOperation("用户聚合信息-返回的代表需要刷新的")
    public ApiResult<List<SummeryInfoDTO>> getSummeryUserInfo(@Valid @RequestBody SummeryInfoReq req) {
        return ApiResult.success(userService.getSummeryUserInfo(req));
    }

    @PostMapping("/public/badges/batch")
    @ApiOperation("徽章聚合信息-返回的代表需要刷新的")
    public ApiResult<List<ItemInfoDTO>> getItemInfo(@Valid @RequestBody ItemInfoReq req) {
        return ApiResult.success(userService.getItemInfo(req));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "没有权限");
        userService.black(req);
        return ApiResult.success();
    }


}

