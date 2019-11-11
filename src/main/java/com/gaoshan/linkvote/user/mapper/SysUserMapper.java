package com.gaoshan.linkvote.user.mapper;

import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.bean.UserHash;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    SysUser selectByName(@Param("name") String name);

    /**
     * select by address
     *
     * @param address 地址
     * @return object by address
     */
    SysUser selectByAddress(@Param("address") String address);

    /**
     * 修改管理员密码
     *
     * @param id             管理员id
     * @param encodePassword 加密后的密码
     * @return update result
     */
    int changePassword(@Param("id") Long id, @Param("encodePassword") String encodePassword);

    /**
     * 根据投票id 查询用户列表
     *
     * @param voteId 投票id
     * @return 用户列表信息
     */
    List<SysUser> selectUsersByVoteId(@Param("voteId") Long voteId);

    List<UserHash> selectVoteUserHashByVoteId(@Param("voteId") Long voteId);

    List<SysUser> selectByAll(SysUser sysUser);

    int delAdminUser(Long id, Long userId);
}