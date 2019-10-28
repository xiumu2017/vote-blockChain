package com.gaoshan.linkvote.sys.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SysFile {
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节b）
     */
    private Integer fileSize;

    /**
     * 图片URL
     */
    private String fileUrl;

    private Date createTime;
}