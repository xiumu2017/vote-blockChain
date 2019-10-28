package com.gaoshan.linkvote.user.mapper;

import org.apache.ibatis.annotations.Param;

import com.gaoshan.linkvote.user.entity.WhiteList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WhiteListMapper {
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
    int insert(WhiteList record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(WhiteList record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    WhiteList selectByPrimaryKey(Long id);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(WhiteList record);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(WhiteList record);

    List<WhiteList> selectByAll(WhiteList whiteList);

}