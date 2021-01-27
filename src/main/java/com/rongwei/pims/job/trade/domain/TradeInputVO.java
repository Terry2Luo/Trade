package com.rongwei.pims.job.trade.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2021-01-23 21:23:02
 */
@Data
public class TradeInputVO extends TradeInputDO implements Serializable {
	private static final long serialVersionUID = 1L;

	public TradeInputVO() {
	}

	public TradeInputVO(Integer transcationId, Integer tradeId, Integer version, String securityCode, Integer quantity, String operationType, String sellType) {
		this.tradeId = tradeId;
		this.transcationId = transcationId;
		this.version = version;
		this.securityCode = securityCode;
		this.quantity = quantity;
		this.operationType = operationType;
		this.sellType = sellType;
	}
	//交易主键
	private Integer transcationId;
	//交易员ID
	private Integer tradeId;
	//版本号
	private Integer version;
	//证券代码
	private String securityCode;
	//数量
	private Integer quantity;
	//操作类型
	private String operationType;
	//买卖类型
	private String sellType;

}
