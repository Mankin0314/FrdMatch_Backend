package com.dingjiaxiong.user_center_backend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -8454862198819098569L;
    private String name;

    private Long id;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
