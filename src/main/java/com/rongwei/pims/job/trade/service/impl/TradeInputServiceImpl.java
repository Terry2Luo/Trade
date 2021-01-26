package com.rongwei.pims.job.trade.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rongwei.pims.job.trade.dao.TradeInputDao;
import com.rongwei.pims.job.trade.domain.TradeInputDO;
import com.rongwei.pims.job.trade.domain.TradeOutputDO;
import com.rongwei.pims.job.trade.service.TradeInputService;
import com.rongwei.pims.job.trade.service.TradeOutputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class TradeInputServiceImpl implements TradeInputService {
    private static String OPERATION_TYPE_INSERT = "INSERT";
    private static String OPERATION_TYPE_UPDATE = "UPDATE";
    private static String OPERATION_TYPE_CANCEL = "CANCEL";

    private static String SELL_TYPE_BUY = "Buy";
    private static String SELL_TYPE_SELL = "Sell";

	private static String IS_VALID_Y = "Y";
	private static String IS_VALID_N = "N";

	private static String VALID_MSG_VERSION_EXIST = "版本号重复";

	private static String VALID_MSG_VERSION_NO_EXIST = "版本不存在,当前版本应该为1";

	private static String RETURN_MSG = "操作成功！";

	@Autowired
	private TradeInputDao tradeInputDao;

	@Autowired
	private TradeOutputService tradeOutputService;


	
	@Override
	public List<TradeInputDO> list(Map<String, Object> map){
		return tradeInputDao.list(map);
	}

	
	@Override
	public int save(TradeInputDO tradeInput){
		return tradeInputDao.save(tradeInput);
	}
	
	@Override
	public int update(TradeInputDO tradeInput){
		return tradeInputDao.update(tradeInput);
	}

	@Override
	public List<TradeInputDO> listTotal(Map<String, Object> map){
		return tradeInputDao.listTotal(map);
	}

    @Override
    public int deleteAll(){
        return tradeInputDao.deleteAll();
    }

	/**
	 * 保存证券交易信息，并计算头寸信息
	 * @param tradeInput
	 * @return JSON数据格式：status:1——操作成功；0——操作失败； msg：操作返回信息
	 */
	@Override
	@Transactional
	public JSONObject saveData(TradeInputDO tradeInput){
		JSONObject json = new JSONObject();

		Map<String, Object> map = new HashMap<>();
		map.put("tradeId", tradeInput.getTradeId());
		map.put("isValid", IS_VALID_Y);//只更新有效是数据
		List<TradeInputDO> tradeInputs = this.list(map);
		log.info("tradeInputs",tradeInputs.size());

		//数据校验
		String msg = validTradeInputInfo(tradeInput, tradeInputs);
		if(msg != null && !msg.equals("")){
			json.put("status", "0");
			json.put("msg",msg);
			return json;
		}

		try{
			//本次计算影响的证券代码
			Set<String> calSecurityCode = new HashSet<String>();
			calSecurityCode.add(tradeInput.getSecurityCode());

			if(tradeInput.getOperationType().equals(OPERATION_TYPE_UPDATE)
					|| tradeInput.getOperationType().equals(OPERATION_TYPE_CANCEL) ){
				//设置当前的交易员的历史有效数据变成无效数据，便于计算股票头寸
				Set<String> securityCodeOld = setHistoryVersionStatus(tradeInput, tradeInputs);

				calSecurityCode.addAll(securityCodeOld);
			}
			log.info("securityCodeOld",calSecurityCode.toArray());

			//有效状态
			tradeInput.setIsValid(IS_VALID_Y);
			//实际数量
			if(tradeInput.getSellType().equals(SELL_TYPE_SELL) ){
				//设置当前的交易员的历史有效数据变成无效数据，便于计算股票头寸
				tradeInput.setRealQuantity(-tradeInput.getQuantity());
			}else if(tradeInput.getOperationType().equals(OPERATION_TYPE_CANCEL) ){
				//设置当前的交易员的历史有效数据变成无效数据，便于计算股票头寸
				tradeInput.setRealQuantity(0);
			}else{
				tradeInput.setRealQuantity(tradeInput.getQuantity());
			}
			tradeInput.setOperationDate(new Date());
			tradeInputDao.save(tradeInput);
			//更新股票头寸
			calcuTradeOutput(calSecurityCode);
		}catch(Exception e){
			json.put("status", "0");
			json.put("msg",e.getMessage());
			return json;
		}

		json.put("status", "1");
		json.put("msg",RETURN_MSG);

		return json;
	}

	/**
	 * 设置当前的交易员的历史有效数据变成无效数据，便于计算股票头寸
	 * @param tradeInput
	 */
	private Set<String> setHistoryVersionStatus(TradeInputDO tradeInput, List<TradeInputDO> tradeInputs){
		Set<String> rtn = new HashSet<String>();
		if(tradeInputs != null && tradeInputs.size() >0){
			for(TradeInputDO tradeInputHistory:tradeInputs){
				rtn.add(tradeInputHistory.getSecurityCode()) ;
				tradeInputHistory.setIsValid(IS_VALID_N);//设置为无效状态
				this.update(tradeInputHistory);
			}
		}

		return rtn;
	}


	/**
	 * 更新股票头寸
	 * * @param calSecurityCode
	 */
	private void calcuTradeOutput(Set<String> calSecurityCode){
		Map<String, Object> map = new HashMap<>();
		map.put("calSecurityCode", calSecurityCode);
		//删除关联的股票头寸信息
		tradeOutputService.deleteByCodes(map);
		//查询关联股票的统计信息
		List<TradeInputDO> tradeInputs = this.listTotal(map);
		List<TradeOutputDO> tradeOutputs = new ArrayList<TradeOutputDO>();

		if(tradeInputs != null && tradeInputs.size() >0){
			for(TradeInputDO tradeInput:tradeInputs){
				TradeOutputDO tradeOutput = new TradeOutputDO();
				tradeOutput.setSecurityCode(tradeInput.getSecurityCode());
				tradeOutput.setQuantity(tradeInput.getQuantity());
				tradeOutputs.add(tradeOutput);
			}
			//保存关联股票的头寸信息
			tradeOutputService.batchSave(tradeOutputs);
		}
	}


	/**
	 * 数据校验
	 * @param tradeInput
	 * @param tradeInputs
	 * @return
	 */
	private String validTradeInputInfo(TradeInputDO tradeInput, List<TradeInputDO> tradeInputs){
		String rtn = null;
		if(tradeInput.getVersion()>1 && (tradeInputs == null || tradeInputs.size() == 0)){
			return VALID_MSG_VERSION_NO_EXIST;
		}
		if(tradeInput.getVersion() == 1 && (tradeInputs != null && tradeInputs.size() > 0)){
			return VALID_MSG_VERSION_EXIST;
		}
		return rtn;
	}


	
}
