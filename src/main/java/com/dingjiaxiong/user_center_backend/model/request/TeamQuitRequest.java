package com.dingjiaxiong.user_center_backend.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitRequest implements Serializable {


    private static final long serialVersionUID = 7351775252768614178L;
    private Long teamId;


}
