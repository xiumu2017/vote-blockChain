package com.gaoshan.linkvote.vote.controller;

import com.gaoshan.linkvote.base.utils.ExcelExportUtils;
import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.entity.VoteUser;
import com.gaoshan.linkvote.vote.service.VoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Api(value = "投票结果导出", tags = "投票相关接口")
@Controller
@Slf4j
@RequestMapping("/vote")
public class VoteExportController {

    private final VoteService voteService;

    @Autowired
    public VoteExportController(VoteService voteService) {
        this.voteService = voteService;
    }

    @ApiOperation("投票结果导出Excel")
    @ApiImplicitParams(@ApiImplicitParam(name = "voteId", value = "投票id"))
    @RequestMapping(value = "export", method = RequestMethod.GET)
    public void export(Long voteId, HttpServletRequest request, HttpServletResponse response) {
        Vote vote = voteService.selectByPrimaryKey(voteId);
        if (vote == null) {
            return;
        }
        List<VoteUser> list = voteService.selectExcel(voteId);
        List<Object[]> dataList = getDataList(list);
        String[] rowNames = {"序号", "address", "已选择", "状态", "链上地址", "投票时间"};
        ExcelExportUtils.ExcelExportCfg cfg = ExcelExportUtils.ExcelExportCfg.builder()
                .request(request).response(response).dataList(dataList).rowName(rowNames)
                .sheetName(vote.getTopic() + "投票结果统计表")
                .title(vote.getTopic() + "投票结果统计表")
                .build();
        try {
            ExcelExportUtils.exportData(cfg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private List<Object[]> getDataList(List<VoteUser> list) {
        String pre = "http://explorer.moac.io/tx/";
        List<Object[]> resultList = new ArrayList<>();
        Object[] arr;
        int index = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (VoteUser voteUser : list) {
            arr = new Object[6];
            arr[0] = index++;
            arr[1] = StringUtils.isEmpty(voteUser.getAddress()) ? "" : voteUser.getAddress();
            arr[2] = StringUtils.isEmpty(voteUser.getIndexes()) ? "" : voteUser.getIndexes();
            arr[3] = dealStatus(voteUser.getStatus());
            if (StringUtils.isBlank(voteUser.getHash())) {
                arr[4] = "";
            } else {
                arr[4] = pre + voteUser.getHash();
            }
            arr[5] = sdf.format(voteUser.getVoteTime());
            resultList.add(arr);
        }
        return resultList;
    }

    private String dealStatus(String status) {
        String[] arr = new String[]{"未上链", "等待交易确认", "交易成功", "交易失败"};
        String res = arr[Integer.parseInt(status)];
        return StringUtils.isEmpty(res) ? "" : res;
    }
}
