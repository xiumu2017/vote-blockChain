package com.gaoshan.linkvote.sys.service;

import com.gaoshan.linkvote.sys.entity.SysFile;

public interface SysFileService {


    int deleteByPrimaryKey(Long id);

    int insert(SysFile record);

    int insertSelective(SysFile record);

    SysFile selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysFile record);

    int updateByPrimaryKey(SysFile record);

}
