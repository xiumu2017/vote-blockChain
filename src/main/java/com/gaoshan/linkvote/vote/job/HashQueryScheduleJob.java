package com.gaoshan.linkvote.vote.job;

import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.entity.VoteUser;
import com.gaoshan.linkvote.vote.entity.Vote_Status;
import com.gaoshan.linkvote.vote.entity.Vote_User_Status;
import com.gaoshan.linkvote.vote.mapper.VoteMapper;
import com.gaoshan.linkvote.vote.mapper.VoteUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.chain3j.protocol.Chain3j;
import org.chain3j.protocol.core.methods.response.Transaction;
import org.chain3j.protocol.core.methods.response.TransactionReceipt;
import org.chain3j.protocol.http.HttpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Component
@Slf4j
public class HashQueryScheduleJob {

    @Resource
    private VoteMapper voteMapper;
    @Resource
    private VoteUserMapper voteUserMapper;

    @Value("${vnodeAddress}")
    private String vnodeAddress;
    @Value("${expireMinute}")
    private Long expireMinute;

    /**
     * 定时任务 上链成功的投票设置为进行中
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void updateVoteStatus() {
        int y = voteMapper.updateBlockSuccessToIng();
        log.info("--->>> 定时任务 上链成功的投票设置为进行中：" + y);
        // 超过截止时间的设置为已截止
        int x = voteMapper.setEndedStatus();
        log.info("--->>> 超过截止时间的设置为已截止 :" + x);
    }

    /**
     * 定时任务，多线程扫描未确认的Hash
     * 判断扫描时长或次数，超时判定失败
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void hashQuery() {
        log.info("定时任务，多线程扫描未确认的Hash >>> ");
        List<Vote> voteList = voteMapper.selectUnConfirmedHash();
        List<VoteUser> voteUserList = voteUserMapper.selectUnConfirmedHash();
        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), new ThreadPoolExecutor.AbortPolicy());

        CompletionService<Integer> completionService = new ExecutorCompletionService<>(pool);

        for (Vote vote : voteList) {
            completionService.submit(() -> {
                log.info(">>> 提交投票Hash任务：Topic: " + vote.getTopic());
                blockChainQueryVote(vote);
                return null;
            });
        }
        for (VoteUser voteUser : voteUserList) {
            completionService.submit(() -> {
                blockChainQuery(voteUser);
                return null;
            });
        }
    }

    /**
     * 查询 用户投票上链结果
     *
     * @param voteUser 用户投票信息
     */
    private void blockChainQuery(VoteUser voteUser) {
        // 判断创建时间，是否超时
        if (checkExpired(voteUser.getVoteTime(), expireMinute)) {
            voteUserMapper.updateStatus(voteUser.getVoteId(), voteUser.getUserId(), Vote_User_Status.TX_FAIL.getCode());
        }
        if (chain3jHashQuery(voteUser.getHash())) {
            // 更新投票选择状态
            voteUserMapper.updateStatus(voteUser.getVoteId(), voteUser.getUserId(), Vote_User_Status.TX_SUCCESS.getCode());
        }
    }

    /**
     * 查询投票信息上链结果
     *
     * @param vote 投票信息
     */
    private void blockChainQueryVote(Vote vote) {
        if (checkExpired(vote.getCreateTime(), expireMinute)) {
            voteMapper.updateStatus(vote.getId(), Vote_Status.TX_FAIL.getCode());
        }
        if (chain3jHashQuery(vote.getHash())) {
            // 更新投票状态
            log.info("// 更新投票状态");
            voteMapper.updateStatus(vote.getId(), Vote_Status.TX_SUCCESS.getCode());
        }
    }

    /**
     * 根据hash 查询交易信息
     *
     * @param hash 交易hash
     * @return true 交易上链成功 false 失败
     */
    private boolean chain3jHashQuery(String hash) {
        // 查询 moac 交易状态
        log.info(">>> start block query, Hash: " + hash);
        log.info(">>> start block query, vnodeAddress: " + vnodeAddress);
        Chain3j chain3j = Chain3j.build(new HttpService(vnodeAddress));
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = chain3j.mcGetTransactionReceipt(hash).send().getResult();
            log.info("transactionReceipt-Status:" + transactionReceipt.getStatus());
            boolean txStatus = transactionReceipt.isStatusOK();
            if (txStatus) {
                Transaction transaction;
                transaction = chain3j.mcGetTransactionByHash(hash).send().getResult();
                // 校验 input data
                log.info(transaction.getInput());
                return true;
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return false;
    }

    /**
     * 判断是否过期
     *
     * @param createTime    开始时间
     * @param expireMinutes 过期判定市场 单位：分钟
     * @return true 已过期 false 未过期
     */
    private boolean checkExpired(Date createTime, Long expireMinutes) {
        Calendar now = Calendar.getInstance();
        Calendar createCalendar = Calendar.getInstance();
        createCalendar.setTime(createTime);
        long min = (now.getTimeInMillis() - createCalendar.getTimeInMillis()) / (1000 * 60);
        return min > expireMinutes;
    }

    public static void main(String[] args) throws IOException {
        // 查询 moac 交易状态
        Chain3j chain3j = Chain3j.build(new HttpService("https://chain3.mytokenpocket.vip"));
        String hash = "0x8165af34c1395f7c89f9528575dc288a3e558ce9b4f87a06b76539c206d1f0c5";
        TransactionReceipt transactionReceipt = chain3j.mcGetTransactionReceipt(hash).send().getResult();
        boolean txStatus = transactionReceipt.isStatusOK();
        if (txStatus) {
            Transaction transaction = chain3j.mcGetTransactionByHash(hash).send().getResult();
            // 校验 input data
            log.info(transaction.getInput());
        }
    }
}
