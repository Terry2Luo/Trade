package com.rongwei.pims.job.trade.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rongwei.pims.job.trade.domain.TradeOutputDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2021-01-23 21:23:10
 */
@Mapper
public interface TradeOutputDao {
	
	List<TradeOutputDO> list(Map<String, Object> map);
	
	int save(TradeOutputDO tradeOutput);
	
	int deleteByCodes(Map<String, Object> map);

	int batchSave(List<TradeOutputDO> list);


}
