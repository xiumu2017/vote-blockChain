package com.gaoshan.linkvote.vote.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.gaoshan.linkvote.vote.entity.VoteOption;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VoteOptionMapper {
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
    int insert(VoteOption record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(VoteOption record);

    /**
     * select by primary key
     * @param id primary key
     * @return object by primary key
     */
    VoteOption selectByPrimaryKey(Long id);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(VoteOption record);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(VoteOption record);

    List<VoteOption> selectByVoteId(@Param("voteId")Long voteId);


}