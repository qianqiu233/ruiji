package com.qianqiu.ruiji_take_out.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证

    private Integer status;
    @TableField(value = "create_Time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_Time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "create_User",fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(value = "update_User",fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
