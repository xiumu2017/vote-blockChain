package com.gaoshan.linkvote.vote.mapper;

import com.gaoshan.linkvote.vote.entity.VoteQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Date;

import com.gaoshan.linkvote.vote.entity.Vote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VoteMapper {
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
    int insert(Vote record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(Vote record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    Vote selectByPrimaryKey(Long id);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(Vote record);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(Vote record);

    List<Vote> selectByAll(VoteQuery vote);

    List<Vote> selectByApp(@Param("voteIdList") List<String> voteIdListBlack,
                           @Param("userId") Long userId);

    Date selectNow();

    List<Vote> selectUnConfirmedHash();

    int updateStatus(@Param("id") Long id, @Param("code") String code);

    int updateBlockSuccessToIng();

    int setEndedStatus();

    List<String> selectByBlackAddress(@Param("address") String address);

    List<String> selectByWhiteAddress(@Param("address") String address);
}