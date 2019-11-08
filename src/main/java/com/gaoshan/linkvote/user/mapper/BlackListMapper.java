package com.gaoshan.linkvote.user.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.gaoshan.linkvote.user.entity.BlackList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlackListMapper {
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
    int insert(BlackList record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(BlackList record);

    /**
     * select by primary key
     * @param id primary key
     * @return object by primary key
     */
    BlackList selectByPrimaryKey(Long id);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(BlackList record);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(BlackList record);

    List<BlackList> selectByAll(BlackList blackList);

    List<BlackList> selectByAddress(String address);
}