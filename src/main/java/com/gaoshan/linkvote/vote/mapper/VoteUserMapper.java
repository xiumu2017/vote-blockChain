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
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    VoteUser selectByPrimaryKey(Long id);

    List<VoteUser> selectByVoteIdAndUserId(@Param("voteId") Long voteId, @Param("userId") Long userId);

    Long countByVoteIdAndOptId(@Param("voteId") Long voteId, @Param("optId") Long optId);


    int insertBatch(@Param("userId") Long userId,
                    @Param("address") String address,
                    @Param("voteId") Long voteId,
                    @Param("optionIdList") List<Long> optionIdList);

    List<VoteUser> selectAllByOptId(@Param("optId") Long optId);


    int updateVoteHash(@Param("userId") Long userId,
                       @Param("voteId") Long voteId,
                       @Param("hash") String hash);

    int updateStatus(@Param("voteId") Long voteId,
                     @Param("userId") Long userId,
                     @Param("status") String status);

    List<VoteUser> selectUnConfirmedHash();

    int deleteByVoteIdAndUserId(@Param("voteId") Long voteId,
                                @Param("userId") Long userId);

}