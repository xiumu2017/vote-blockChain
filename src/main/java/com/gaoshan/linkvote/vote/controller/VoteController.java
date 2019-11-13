package com.gaoshan.linkvote.vote.controller;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.sys.entity.SysFile;
import com.gaoshan.linkvote.sys.service.SysFileService;
import com.gaoshan.linkvote.vote.entity.VoteQuery;
import com.gaoshan.linkvote.vote.service.VoteService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public R queryVotePage(@ApiParam("分页查询实体") VoteQuery query, @ApiIgnore Principal principal) {
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
    @ApiOperation("查询投票详情")
    @GetMapping("/getVoteDetail")
    public R getVoteDetail(Long voteId, @ApiIgnore Principal principal) {
        if (voteId == null) {
            return Rx.error("参数为空异常！");
        }
        return voteService.getVoteDetail(voteId, principal.getName());
    }

    @ApiOperation("增加黑/白名单数据")
    @PostMapping("/addVoteBWList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型：black-黑名单；white-白名单", example = "black"),
            @ApiImplicitParam(name = "voteId", value = "投票id", example = "30"),
    })
    public R addVoteBWList(MultipartFile file, Long voteId, @ApiIgnore Principal principal, String type) {
        log.info(principal.getName());
        if (voteId == null) {
            return Rx.error("voteId 不能为空");
        }
        if (file == null) {
            return Rx.error("文件为空");
        }
        if (StringUtils.isBlank(type)) {
            return Rx.error("type 为空");
        }
        return voteService.addVoteBWList(file, voteId, type);
    }

    @ApiOperation("分页展示投票黑名单列表")
    @GetMapping("/getBlackPage")
    public R getBlackPage(Long voteId, Integer pageNum, Integer pageSize) {
        if (voteId == null) {
            return Rx.error("参数为空");
        }
        return voteService.getBlackPage(voteId, pageNum, pageSize);
    }

    @ApiOperation("分页展示投票白名单列表")
    @GetMapping("/getWhitePage")
    public R getWhitePage(Long voteId, Integer pageNum, Integer pageSize) {
        if (voteId == null) {
            return Rx.error("参数为空");
        }
        return voteService.getWhitePage(voteId, pageNum, pageSize);
    }

    @ApiOperation("删除黑名单地址")
    @GetMapping("/delBlackUser")
    public R delBlackUser(Long id) {
        if (id == null) return Rx.error("参数为空");
        return voteService.delBlackUser(id);
    }

    @ApiOperation("删除白名单地址")
    @GetMapping("/delWhiteUser")
    public R delWhiteUser(Long id) {
        if (id == null) return Rx.error("参数为空");
        return voteService.delWhiteUser(id);
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
    public void showPic(HttpServletResponse response, Long fileId, String origin) {
        SysFile sysFile = fileService.selectByPrimaryKey(fileId);
        File file;
        // 默认缩略图
        if (StringUtils.isNotBlank(origin)) {
            file = new File(sysFile.getFilePath());
        } else {
            String fileName = sysFile.getFilePath();
            String preFix = fileName.substring(0, fileName.lastIndexOf("."));
            String subFix = fileName.substring(fileName.lastIndexOf("."));
            file = new File(preFix + "_thumb" + subFix);
            if (!file.exists()) {
                file = new File(sysFile.getFilePath());
            }
        }
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
