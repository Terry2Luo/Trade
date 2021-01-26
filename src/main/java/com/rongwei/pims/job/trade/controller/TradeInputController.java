package com.rongwei.pims.job.trade.controller;


import java.util.List;
import java.util.Map;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.rongwei.pims.job.common.R;
import com.rongwei.pims.job.trade.domain.TradeInputDO;
import com.rongwei.pims.job.trade.service.TradeInputService;
import com.rongwei.pims.job.trade.service.TradeOutputService;
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
public class TradeInputController {
	@Autowired
	private TradeInputService tradeInputService;

	@Autowired
	private TradeOutputService tradeOutputService;
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@ApiOperation(value = "证券头寸交易记录", notes = "证券头寸交易记录")
	public JSONObject save(TradeInputDO tradeInput){
		JSONObject json = tradeInputService.saveData(tradeInput);
		return json;
	}

	

	
}
