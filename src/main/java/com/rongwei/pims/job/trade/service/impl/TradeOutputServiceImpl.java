package com.rongwei.pims.job.trade.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rongwei.pims.job.trade.dao.TradeOutputDao;
import com.rongwei.pims.job.trade.domain.TradeOutputDO;
import com.rongwei.pims.job.trade.service.TradeOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TradeOutputServiceImpl implements TradeOutputService {
	@Autowired
	private TradeOutputDao tradeOutputDao;

	@Override
	public List<TradeOutputDO> list(Map<String, Object> map){
		return tradeOutputDao.list(map);
	}
	
	@Override
	public int save(TradeOutputDO tradeOutput){
		return tradeOutputDao.save(tradeOutput);
	}
	
	@Override
	public int deleteByCodes(Map<String, Object> map){
		return tradeOutputDao.deleteByCodes(map);
	}
	
	@Override
	public int batchSave(List<TradeOutputDO> list){
		return tradeOutputDao.batchSave(list);
	}

	@Override
	public JSONObject listAll(){
		JSONObject json = new JSONObject();
		Map<String, Object> map = new HashMap<>();
		List<TradeOutputDO> list =	 tradeOutputDao.list(map);
		if(list != null && list.size() > 0){
			for(TradeOutputDO tradeOutput:list){
				json.put(tradeOutput.getSecurityCode(),tradeOutput.getQuantity());
			}
		}
		return json;
	}

}
