package com.dingjiaxiong.user_center_backend.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamDeleteRequest implements Serializable {



    private static final long serialVersionUID = 1969981102705557300L;
    private Long teamId;


}
