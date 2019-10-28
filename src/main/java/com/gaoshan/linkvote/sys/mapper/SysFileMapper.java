package com.gaoshan.linkvote.sys.mapper;

import com.gaoshan.linkvote.sys.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysFileMapper {
    /**
     * delete by primary key
     * @param id primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Long id);

    /**
     * insert record to table
     * @param record the record
     * @return insert count
     */
    int insert(SysFile record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(SysFile record);

    /**
     * select by primary key
     * @param id primary key
     * @return object by primary key
     */
    SysFile selectByPrimaryKey(Long id);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(SysFile record);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(SysFile record);
}