package com.gaoshan.linkvote.vote.controller;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.sys.entity.SysFile;
import com.gaoshan.linkvote.sys.service.SysFileService;
import com.gaoshan.linkvote.vote.entity.VoteQuery;
import com.gaoshan.linkvote.vote.service.VoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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

    /**
     * 分页查询
     * 1. 不同用户看到的数据列表不同
     * 2. 黑白名单处理
     *
     * @return 封装数据
     */
    @ApiOperation("分页查询投票列表")
    @GetMapping("/queryVotePage")
    public R queryVotePage(@ApiParam("分页查询实体") VoteQuery query, Principal principal) {
        log.info(principal.getName());
        return Rx.success(voteService.queryByPage(query));
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
     * 查询投票详情
     *
     * @param voteId 主键
     * @return 投票详情
     * 选项列表；当前用户选择结果；当前投票统计结果；
     */
    @ApiIgnore
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
    @ApiIgnore
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
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
