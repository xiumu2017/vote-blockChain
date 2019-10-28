package com.gaoshan.linkvote;

import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.mapper.VoteMapper;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class LinkVoteApplicationTests {

    @Resource
    private VoteMapper voteMapper;

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

}
