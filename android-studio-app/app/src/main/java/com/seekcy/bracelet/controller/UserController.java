package com.seekcy.bracelet.controller;

import com.db.database.UserDatabase;
import com.db.database.daoobject.UserInfoDO;
import com.seekcy.bracelet.annotation.AppController;
import com.seekcy.bracelet.annotation.AppRequestMapper;
import com.seekcy.bracelet.annotation.AppRequestMethod;
import com.seekcy.bracelet.data.entity.dto.UserInfoConfigDTO;
import com.seekcy.bracelet.data.entity.vo.response.RespVO;

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
