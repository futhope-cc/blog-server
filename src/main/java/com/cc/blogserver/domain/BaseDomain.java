package com.cc.blogserver.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类，所有 Domain 继承
 * 主键雪花算法(ASSIGN_ID)，自动填充 createTime/updateTime/createBy/updateBy
 * isDelete 逻辑删除标记(0正常 1删除)，手动维护(不走 @TableLogic)，对应数据库列 is_delete
 */
@Data
public abstract class BaseDomain implements Serializable {

    /** 主键ID(数据库自增) */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 创建时间(插入时自动填充) */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间(插入/更新时自动填充) */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建者ID(插入时自动填充，取当前登录用户) */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 修改者ID(插入/更新时自动填充，取当前登录用户) */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 是否删除:0正常 1删除(逻辑删除标记，插入时自动填充为0，删除时手动更新为1) */
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;

    /** 备注 */
    private String remark;
}
