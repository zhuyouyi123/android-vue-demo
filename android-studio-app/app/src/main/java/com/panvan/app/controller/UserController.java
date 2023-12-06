package com.panvan.app.controller;

import com.db.database.UserDatabase;
import com.db.database.daoobject.UserInfoDO;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.data.entity.dto.UserInfoConfigDTO;
import com.panvan.app.response.RespVO;

import java.util.Objects;

@AppController(path = "user")
public class UserController {
    @AppRequestMapper(path = "/user-info")
    public RespVO<UserInfoDO> userConfig() {
        return RespVO.success(UserDatabase.getInstance().getUserInfoDAO().query());
    }

    @AppRequestMapper(path = "/user-info", method = AppRequestMethod.POST)
    public RespVO<Void> userConfig(UserInfoConfigDTO dto) {
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUsername(dto.getUsername());
        userInfoDO.setGender(dto.getGender());

        if (Objects.isNull(dto.getId())){
            UserDatabase.getInstance().getUserInfoDAO().insert(userInfoDO);
        }else{
            userInfoDO.setId(dto.getId());
            UserDatabase.getInstance().getUserInfoDAO().update(userInfoDO);
        }

        return RespVO.success();
    }
}
