package com.huang.service;

import com.huang.bo.RegisterInfoBO;
import com.huang.common.lang.Result;
import com.huang.entity.MUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
public interface MUserService extends IService<MUser> {

    Result registerUser(RegisterInfoBO registerInfoBO);

//    AccountResult loginUser(String username, String password);
}
