package com.rongwei.pims.job.trade.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rongwei.pims.job.trade.domain.TradeInputDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2021-01-23 21:23:02
 */
@Mapper
public interface TradeInputDao {
	
	List<TradeInputDO> list(Map<String, Object> map);
	
	int save(TradeInputDO tradeInput);
	
	int update(TradeInputDO tradeInput);

	List<TradeInputDO> listTotal(Map<String, Object> map);

    int deleteAll();


}
