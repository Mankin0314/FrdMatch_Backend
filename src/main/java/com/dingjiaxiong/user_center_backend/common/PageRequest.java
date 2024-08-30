package com.dingjiaxiong.user_center_backend.common;

import lombok.Data;


import java.io.Serializable;

@Data
public class PageRequest implements Serializable{



    private static final long serialVersionUID = 698934617317697429L;
    protected int pageSize= 10;

    protected int pageNum=1;
}
