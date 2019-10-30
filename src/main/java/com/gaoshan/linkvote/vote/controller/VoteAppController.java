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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Paradise
 */
@Api(tags = "投票相关APP接口")
@Slf4j
@RestController
@RequestMapping("/vote/app")
public class VoteAppController {

    private final VoteService voteService;
    private final SysFileService sysFileService;

    @Autowired
    public VoteAppController(VoteService voteService, SysFileService sysFileService) {
        this.voteService = voteService;
        this.sysFileService = sysFileService;
    }

    @Value("${picFile.path}")
    private String filePath;

    @ApiOperation("图片上传")
    @PostMapping("/uploadImage")
    public R uploadImage(MultipartFile img) {
        if (img != null) {
            SysFile sysFile = new SysFile();
            File dir = new File(filePath + File.separator + formatDate());
            if (!dir.exists() && !dir.mkdir()) {
                return Rx.error("服务器图片路径异常");
            }
            try {
                File file = new File(dir.getAbsolutePath() + File.separator + img.getOriginalFilename());
                img.transferTo(file);
                sysFile.setFileName(img.getOriginalFilename());
                sysFile.setFilePath(file.getAbsolutePath());
                sysFile.setFileSize(Math.toIntExact(img.getSize()));
                sysFileService.insert(sysFile);
                return Rx.success(sysFile);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
                return Rx.error(e.getLocalizedMessage());
            }
        } else {
            return Rx.error("图片信息不存在");
        }
    }

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
            example = "[{index:1,content:'同意'},{index:2,content:'反对'}]", required = true),
            @ApiImplicitParam(name = "address", value = "地址", required = true)})
    @PostMapping("/create")
    public R addVote(@ApiParam("投票实体") Vote vote,
                     String optionJson,
                     String address) {
        try {
            if (StringUtils.isBlank(address)) {
                return Rx.error("地址信息为空");
            }
            if (StringUtils.isBlank(optionJson)) {
                return Rx.error("投票选项数据为空");
            }
        } catch (Exception e) {
            return Rx.error("500", "投票选项数据解析错误" + e.getLocalizedMessage());
        }
        return voteService.insert(vote, optionJson, address);
    }

    @ApiOperation("更新投票的上链Hash")
    @PostMapping("/updateVoteHash")
    public R updateVoteHash(String address, Long voteId, String hash) {
        if (StringUtils.isBlank(address)) {
            return Rx.error("地址信息为空");
        }
        if (StringUtils.isBlank(hash)) {
            return Rx.error("Hash不能为空");
        }
        if (voteId == null) {
            return Rx.error("投票ID为空");
        }
        return voteService.updateVoteHash(address, voteId, hash);
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

    @ApiOperation("查看选项")
    @GetMapping("/getVoteOptionDetail")
    public R getVoteOptionDetail(Long optionId) {
        return voteService.getVoteOptionDetail(optionId);
    }

    /**
     * app 用户投票
     *
     * @param voteId  投票id
     * @param options 选项列表
     * @param address 用户地址
     * @return {@link R}
     */
    @ApiOperation("用户投票")
    @PostMapping("/doVote")
    @ApiImplicitParams({@ApiImplicitParam("投票id"),
            @ApiImplicitParam("选项id，多选英文逗号分隔"),
            @ApiImplicitParam(name = "address", value = "地址")})
    public R vote(Long voteId, String options, String address) {
        return voteService.doVoteApp(voteId, options, address);
    }

    @ApiOperation("更新用户投票上链的Hash")
    @ApiImplicitParams({@ApiImplicitParam(name = "address", value = "地址"),
            @ApiImplicitParam("投票id"),
            @ApiImplicitParam("交易Hash")})
    @PostMapping("/updateAppVoteHash")
    public R updateAppVoteHash(String address, Long voteId, String hash) {
        return voteService.updateAppVoteHash(address, voteId, hash);
    }

    private String formatDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
