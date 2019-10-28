package com.gaoshan.linkvote.user.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.gaoshan.linkvote.user.entity.BlackUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlackUserMapper {
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
    int insert(BlackUser record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(BlackUser record);

    /**
     * select by primary key
     * @param id primary key
     * @return object by primary key
     */
    BlackUser selectByPrimaryKey(Long id);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(BlackUser record);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(BlackUser record);

    List<BlackUser> selectAllByBlackId(@Param("blackId")Long blackId);


}