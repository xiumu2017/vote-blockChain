package com.gaoshan.linkvote.vote.controller;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.sys.entity.SysFile;
import com.gaoshan.linkvote.sys.service.SysFileService;
import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.entity.VoteModel;
import com.gaoshan.linkvote.vote.service.VoteService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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

    @Value("${picFile.serverIp}")
    private String serverIp;

    @ApiOperation("图片上传")
    @PostMapping("/uploadImage")
    public R uploadImage(MultipartFile img, HttpServletRequest request) {
        String url = "http://" + serverIp + ":" + request.getServerPort() + request.getContextPath();
        url = url + "/vote/showPic?fileId=";
        if (img != null) {
            SysFile sysFile = new SysFile();
            File dir = new File(filePath + File.separator + formatDate());
            if (!dir.exists() && !dir.mkdir()) {
                return Rx.error("服务器图片路径异常");
            }
            try {
                String fileName = dir.getAbsolutePath() + File.separator + img.getOriginalFilename();
                File file = new File(fileName);
                img.transferTo(file);
                sysFile.setFileName(img.getOriginalFilename());
                sysFile.setFilePath(file.getAbsolutePath());
                sysFile.setFileSize(Math.toIntExact(img.getSize()));
                sysFileService.insert(sysFile);
                // 压缩图片
                String preFix = fileName.substring(0, fileName.lastIndexOf("."));
                String subFix = fileName.substring(fileName.lastIndexOf("."));
                Thumbnails.of(file)
                        .outputQuality(0.5f)
                        .scale(1d)
                        .toFile(new File(preFix + "_thumb" + subFix));
                return Rx.success(url + sysFile.getId());
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
     * @param voteModel  投票实体类
     * @param optionJson 投票选项数据
     */
    @ApiOperation("发起投票")
    @ApiImplicitParams({@ApiImplicitParam(name = "optionJson", value = "选项JSON数据",
            example = "[{index:1,content:'同意'},{index:2,content:'反对'}]", required = true),
            @ApiImplicitParam(name = "address", value = "地址", required = true)})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R addVote(@ApiParam("投票实体") VoteModel voteModel,
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
        return voteService.insert(new Vote(voteModel), optionJson, address);
    }

    @ApiOperation("更新投票的上链Hash")
    @PostMapping("/updateVoteHash")
    public R updateVoteHash(String address, Long voteId, String hash) {
        log.info("更新投票的上链Hash");
        log.info("address: " + address);
        log.info("voteId: " + voteId);
        log.info("hash: " + hash);
        if (StringUtils.isBlank(address)) {
            return Rx.error("地址信息为空");
        }
        if (StringUtils.isBlank(hash)) {
            return Rx.error("Hash不能为空");
        } else if (!hash.startsWith("0x")) {
            return Rx.error("Hash: " + hash + " 格式不正确 ");
        }
        if (voteId == null) {
            return Rx.error("投票ID为空");
        }
        return voteService.updateVoteHash(address, voteId, hash);
    }

    @ApiOperation("删除投票")
    @DeleteMapping("/del")
    public R delVote(Long id, String address) {
        if (id == null) {
            return Rx.error("参数为空异常！");
        }
        return voteService.delete(id, address);
    }

    /**
     * 分页查询
     * 1. 不同用户看到的数据列表不同
     * 2. 黑白名单处理
     *
     * @return 封装数据
     */
    @ApiOperation("分页查询投票列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "地址"),
            @ApiImplicitParam(name = "pageNum", value = "pageNum"),
            @ApiImplicitParam(name = "pageSize", value = "pageSize"),
    })
    @GetMapping("/queryVotePage")
    public R appQueryVotePage(String address, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(address)) {
            return Rx.error("地址信息为空");
        }
        return voteService.appQueryVotePage(address, pageNum, pageSize);
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
    public R getAppVoteDetail(Long voteId, String address) {
        if (voteId == null || StringUtils.isBlank(address)) {
            return Rx.error("参数为空异常！");
        }
        return voteService.getAppVoteDetail(voteId, address);
    }

    @ApiOperation("查看选项")
    @GetMapping("/getVoteOptionDetail")
    public R getVoteOptionDetail(Long optionId) {
        if (optionId == null) {
            return Rx.error("选项id为空");
        }
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
    @RequestMapping(value = "/doVote", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "voteId", value = "投票id"),
            @ApiImplicitParam(name = "options", value = "选项id，多选英文逗号分隔"),
            @ApiImplicitParam(name = "address", value = "地址")})
    public R vote(Long voteId, String options, String address) {
        return voteService.doVoteApp(voteId, options, address);
    }

    @ApiOperation("更新用户投票上链的Hash")
    @ApiImplicitParams({@ApiImplicitParam(name = "address", value = "地址"),
            @ApiImplicitParam(name = "voteId", value = "投票id"),
            @ApiImplicitParam(name = "hash", value = "交易Hash")})
    @PostMapping("/updateAppVoteHash")
    public R updateAppVoteHash(String address, Long voteId, String hash) {
        log.info("更新用户投票上链的Hash");
        log.info("address: " + address);
        log.info("voteId: " + voteId);
        log.info("hash: " + hash);
        if (StringUtils.isBlank(address)) {
            return Rx.error("address不能为空");
        }
        if (voteId == null) {
            return Rx.error("voteId 不能为空");
        }
        if (StringUtils.isBlank(hash)) {
            return Rx.error("Hash 不能为空");
        }
        if (!hash.startsWith("0x")) {
            return Rx.error("Hash:" + hash + " 格式不正确");
        }
        return voteService.updateAppVoteHash(address, voteId, hash);
    }

    private String formatDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
