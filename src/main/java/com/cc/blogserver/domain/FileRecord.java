package com.cc.blogserver.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件实体(本地磁盘存储)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file")
public class FileRecord extends BaseDomain {

    /** 原始文件名 */
    private String name;

    /** 访问URL(相对路径) */
    private String url;

    /** 类型:image=图片 file=附件 */
    private String type;

    /** 文件大小(字节) */
    private Long size;
}
