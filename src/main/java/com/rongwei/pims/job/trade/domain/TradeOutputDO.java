package com.rongwei.pims.job.trade.domain;


import java.io.Serializable;
import lombok.Data;


/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2021-01-23 21:23:10
 */
@Data
public class TradeOutputDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private Integer transactionOutputId;
	//证券代码
	private String securityCode;
	//股票头寸数量
	private Integer quantity;

}
