package com.dingjiaxiong.user_center_backend.common;

import lombok.Data;

import java.io.Serializable;
//通用的删除请求参数
@Data
public class DeleteRequest implements Serializable {



    private static final long serialVersionUID = 1969981102705557300L;
    private Long teamId;


}
