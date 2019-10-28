package com.gaoshan.linkvote.sys.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.gaoshan.linkvote.sys.mapper.SysFileMapper;
import com.gaoshan.linkvote.sys.entity.SysFile;
import com.gaoshan.linkvote.sys.service.SysFileService;
@Service
public class SysFileServiceImpl implements SysFileService{

    @Resource
    private SysFileMapper sysFileMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return sysFileMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(SysFile record) {
        return sysFileMapper.insert(record);
    }

    @Override
    public int insertSelective(SysFile record) {
        return sysFileMapper.insertSelective(record);
    }

    @Override
    public SysFile selectByPrimaryKey(Long id) {
        return sysFileMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SysFile record) {
        return sysFileMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(SysFile record) {
        return sysFileMapper.updateByPrimaryKey(record);
    }

}
