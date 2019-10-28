package com.gaoshan.linkvote.vote.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.gaoshan.linkvote.vote.entity.VoteUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VoteUserMapper {
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
    int insert(VoteUser record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(VoteUser record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    VoteUser selectByPrimaryKey(Long id);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(VoteUser record);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(VoteUser record);

    List<VoteUser> selectByVoteIdAndUserId(@Param("voteId") Long voteId, @Param("userId") Long userId);

    Long countByVoteIdAndOptId(@Param("voteId") Long voteId, @Param("optId") Long optId);


    int insertBatch(@Param("userId") Long userId, @Param("voteId") Long voteId,
                    @Param("optionIdList") List<Long> optionIdList);

    List<VoteUser> selectAllByOptId(@Param("optId")Long optId);


}