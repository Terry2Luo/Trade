package com.rongwei.pims.job.trade.controller;


import java.util.List;
import java.util.Map;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.rongwei.pims.job.common.R;
import com.rongwei.pims.job.trade.domain.TradeInputDO;
import com.rongwei.pims.job.trade.domain.TradeInputVO;
import com.rongwei.pims.job.trade.service.TradeInputService;
import com.rongwei.pims.job.trade.service.TradeOutputService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2021-01-23 21:23:02
 */

@RestController
@RequestMapping("/tradeInput")
@Api(value = "/tradeInput", description = "证券交易操作信息")
public class TradeInputController {
	@Autowired
	private TradeInputService tradeInputService;

	@Autowired
	private TradeOutputService tradeOutputService;


	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/saveTradeInput")
	@ApiOperation(value = "保存交易记录", notes = "保存交易记录，并自动计算证券头寸")
	public JSONObject saveTradeInput(TradeInputVO tradeInput){
		JSONObject json = tradeInputService.saveData(tradeInput);
		return json;
	}


    /**
     * 保存
     */
    @ResponseBody
    @PostMapping("/listTradeOutput")
    @ApiOperation(value = "查询所有的证券头寸信息", notes = "查询所有的证券头寸信息")
    public JSONObject listTradeOutput(){
        JSONObject json = tradeOutputService.listAll();
        return json;
    }

	

	
}
