package com.gaoshan.linkvote.vote.controller;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.sys.entity.SysFile;
import com.gaoshan.linkvote.sys.service.SysFileService;
import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.entity.VoteQuery;
import com.gaoshan.linkvote.vote.service.VoteService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;

/**
 * @author Paradise
 */
@Api(tags = "投票相关接口", hidden = true)
@Slf4j
@RestController
@RequestMapping("/vote")
public class VoteController {

    private final VoteService voteService;
    private final SysFileService fileService;

    @Autowired
    public VoteController(VoteService voteService, SysFileService fileService) {
        this.voteService = voteService;
        this.fileService = fileService;
    }

    @Value("${picFile.path}")
    private String filePath;

    /**
     * 发起投票
     * 1. 参数校验，包括选项的校验（空值，长度，时间）
     * 2. 投票选项解析，校验
     * 3. 图片保存，绝对路径，其它方案
     *
     * @param vote       投票实体类
     * @param optionJson 投票选项数据
     */
    @ApiOperation("发起投票")
    @ApiImplicitParams({@ApiImplicitParam(name = "optionJson", value = "选项JSON数据",
            example = "[{index:1,content:'同意'},{index:2,content:'反对'}]", required = true)})
    @PostMapping("/create")
    public R addVote(@ApiParam("投票实体") Vote vote,
                     String optionJson, String address) {
        try {
            if (StringUtils.isBlank(optionJson)) {
                return Rx.error("投票选项数据为空");
            }
        } catch (Exception e) {
            return Rx.error("9999", "投票选项数据解析错误" + e.getLocalizedMessage());
        }
        return voteService.insert(vote, optionJson, address);
    }

    @ApiOperation("删除投票")
    @DeleteMapping("/del")
    public R delVote(Long id) {
        if (id == null) {
            return Rx.error("参数为空异常！");
        }
        if (voteService.deleteByPrimaryKey(id) == 1) {
            return Rx.success();
        } else {
            return Rx.fail();
        }
    }

    /**
     * 分页查询
     * 1. 不同用户看到的数据列表不同
     * 2. 黑白名单处理
     *
     * @return 封装数据
     */
    @ApiOperation("分页查询投票列表")
    @GetMapping("/queryVotePage")
    public R queryVotePage(@ApiParam("分页查询实体") VoteQuery query) {
        return Rx.success(voteService.queryByPage(query));
    }

    /**
     * 查询投票详情
     *
     * @param voteId 主键
     * @return 投票详情
     * 选项列表；当前用户选择结果；当前投票统计结果；
     */
    @ApiOperation("查询投票详情")
    @GetMapping("/getVoteDetail")
    public R getVoteDetail(Long voteId, Principal principal) {
        if (voteId == null) {
            return Rx.error("参数为空异常！");
        }
        return voteService.getVoteDetail(voteId, principal.getName());
    }

    /**
     * 查询投票选项，包含投票数据
     *
     * @param optionId 选项id
     * @return {@link R}
     */
    @ApiOperation("查看选项")
    @GetMapping("/getVoteOptionDetail")
    public R getVoteOptionDetail(Long optionId) {
        return voteService.getVoteOptionDetail(optionId);
    }

    /**
     * 图片显示接口
     *
     * @param response 响应
     * @param fileId   图片文件id
     */
    @ApiOperation("显示图片")
    @RequestMapping(value = "/showPic", method = RequestMethod.GET)
    public void showPic(HttpServletResponse response, Long fileId) {
        SysFile sysFile = fileService.selectByPrimaryKey(fileId);
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            log.error("文件信息不存在！");
            return;
        }
        response.setContentType("multipart/form-data");
        OutputStream out;
        try {
            FileInputStream ips = new FileInputStream(file);
            out = response.getOutputStream();
            //读取文件流
            int len;
            byte[] buffer = new byte[1024 * 10];
            while ((len = ips.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
