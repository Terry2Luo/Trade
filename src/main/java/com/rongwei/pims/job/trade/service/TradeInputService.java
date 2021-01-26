package com.rongwei.pims.job.trade.service;



import com.alibaba.fastjson.JSONObject;
import com.rongwei.pims.job.trade.domain.TradeInputDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2021-01-23 21:23:02
 */
public interface TradeInputService {
	List<TradeInputDO> list(Map<String, Object> map);
	
	int save(TradeInputDO tradeInput);
	
	int update(TradeInputDO tradeInput);

	JSONObject saveData(TradeInputDO tradeInput);

	List<TradeInputDO> listTotal(Map<String, Object> map);

    int deleteAll();
}
