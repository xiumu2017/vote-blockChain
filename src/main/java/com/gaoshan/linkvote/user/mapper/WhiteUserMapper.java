package com.gaoshan.linkvote.user.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.gaoshan.linkvote.user.entity.WhiteUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WhiteUserMapper {
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
    int insert(WhiteUser record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(WhiteUser record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    WhiteUser selectByPrimaryKey(Long id);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(WhiteUser record);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(WhiteUser record);

    List<WhiteUser> selectAllByWhiteId(@Param("whiteId") Long whiteId);

    List<String> selectAddressByWhiteId(@Param("whiteId") Long whiteId);

    int batchInsert(@Param("id") Long id, @Param("addressList") List<String> addressList);

    Long countByWhiteIdAndAddress(@Param("whiteId") Long whiteId,
                                  @Param("address") String address);

}