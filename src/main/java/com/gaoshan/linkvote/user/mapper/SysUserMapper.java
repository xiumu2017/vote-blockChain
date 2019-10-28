package com.gaoshan.linkvote.user.mapper;

import com.gaoshan.linkvote.user.bean.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Paradise
 */
@Mapper
public interface SysUserMapper {
    /**
     * delete by primary key
     *
     * @param id primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Long id);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(SysUser record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(SysUser record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    SysUser selectByPrimaryKey(Long id);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(SysUser record);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(SysUser record);

    SysUser selectByName(@Param("name") String name);

    /**
     * select by address
     *
     * @param address 地址
     * @return object by address
     */
    SysUser selectByAddress(@Param("address") String address);

    int changePassword(@Param("id") Long id, @Param("encodePassword") String encodePassword);
}