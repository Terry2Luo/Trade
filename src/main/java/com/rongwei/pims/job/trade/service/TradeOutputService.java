package com.rongwei.pims.job.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.rongwei.pims.job.trade.domain.TradeOutputDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2021-01-23 21:23:10
 */
public interface TradeOutputService {

	List<TradeOutputDO> list(Map<String, Object> map);
	
	int save(TradeOutputDO tradeOutput);
	
	int deleteByCodes(Map<String, Object> map);
	
	int batchSave(List<TradeOutputDO> list);

	JSONObject listAll();
}
