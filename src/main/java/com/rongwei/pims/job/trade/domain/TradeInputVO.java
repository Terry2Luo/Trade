package com.rongwei.pims.job.trade.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value="交易明细",description = "交易明细信息")
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
	@ApiModelProperty(value="交易主键",example="1",required=true)
	private Integer transcationId;
	//交易员ID
	@ApiModelProperty(value="交易员ID",example="1",required=true)
	private Integer tradeId;
	//版本号
	@ApiModelProperty(value="版本号",example="1",required=true)
	private Integer version;
	//证券代码
	@ApiModelProperty(value="证券代码",example="INF、REL、ITC",required=true)
	private String securityCode;
	//数量
	@ApiModelProperty(value="数量",example="50、20",required=true)
	private Integer quantity;
	//操作类型
	@ApiModelProperty(value="操作类型",example="INSERT、UPDATE、CANCEL",required=true)
	private String operationType;
	//买卖类型
	@ApiModelProperty(value="买卖类型",example="Sell、Buy",required=true)
	private String sellType;

}
