package com.gaoshan.linkvote;

import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.entity.VoteUser;
import com.gaoshan.linkvote.vote.mapper.VoteMapper;
import com.gaoshan.linkvote.vote.mapper.VoteUserMapper;
import org.chain3j.protocol.Chain3j;
import org.chain3j.protocol.core.methods.response.Transaction;
import org.chain3j.protocol.core.methods.response.TransactionReceipt;
import org.chain3j.protocol.http.HttpService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LinkVoteApplicationTests {

    @Resource
    private VoteMapper voteMapper;
    @Resource
    private VoteUserMapper voteUserMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    @Ignore
    public void timeTest() {
        Vote vote = new Vote();
        vote.setTopic("Hello");
        voteMapper.insert(vote);

        if (vote.getId() != null) {
            Vote v = voteMapper.selectByPrimaryKey(vote.getId());
            System.out.println(v.getCreateTime().getTime());
        }
        System.out.println(voteMapper.selectNow().getTime());
    }

    @Test
    @Ignore
    public void testVote() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/vote/create")
                .header("authorization", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getFlashMap();


    }

    @Test
    @Ignore
    public void testHash() {
        // 查询 moac 交易状态
        Chain3j chain3j = Chain3j.build(new HttpService("https://chain3.mytokenpocket.vip"));
        TransactionReceipt transactionReceipt;
        String hash = "0xd0a133f2fd88c48ac68f62cde64a29a2c3b1d3816773a6ce926606a1da0dee91";
        try {
            transactionReceipt = chain3j.mcGetTransactionReceipt(hash).send().getResult();
            boolean txStatus = transactionReceipt.isStatusOK();
            if (txStatus) {
                Transaction transaction;
                transaction = chain3j.mcGetTransactionByHash(hash).send().getResult();
                // 数据校验
                if (dataValidate(hash, hexStringToString(transaction.getInput()), transaction.getFrom())) {
                    System.out.println(true + ">>>>>>>>>>>>>>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean dataValidate(String hash, String data, String from) {
        List<VoteUser> voteUserList = voteUserMapper.selectAllByHash(hash);
        if (voteUserList.isEmpty()) {
            return false;
        } else {
            String address = voteUserList.get(0).getAddress();
            Long voteId = voteUserList.get(0).getVoteId();
            String voteHash = voteMapper.selectByPrimaryKey(voteId).getHash();
            if (!data.contains(voteHash)) return false;
            return address.equals(from);
        }
    }


    private static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        s = s.replace("0x", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
        }
        return new String(baKeyword, StandardCharsets.UTF_8);
    }

}
