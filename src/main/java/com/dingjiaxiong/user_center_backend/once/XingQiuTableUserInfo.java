package com.dingjiaxiong.user_center_backend.once;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

//导入Excel数据
@Data
public class XingQiuTableUserInfo {
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;

}
