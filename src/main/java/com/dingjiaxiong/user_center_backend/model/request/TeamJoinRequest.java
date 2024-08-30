package com.dingjiaxiong.user_center_backend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class TeamJoinRequest implements Serializable {


    private Long teamId;


    /**
     * 密码
     */
    private String password;

}
