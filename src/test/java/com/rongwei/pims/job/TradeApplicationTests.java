package com.rongwei.pims.job;

import com.alibaba.fastjson.JSONObject;
import com.rongwei.pims.job.trade.domain.TradeInputDO;
import com.rongwei.pims.job.trade.service.TradeInputService;
import com.rongwei.pims.job.trade.service.TradeOutputService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TradeApplicationTests {

    @Autowired
    private TradeInputService tradeInputService;

    @Autowired
    private TradeOutputService tradeOutputService;

//    @Before
    @Test
    @Order(1)
    public void initData() throws Exception {
        tradeInputService.deleteAll();
    }



    /**
     * 测试insertAndBuy的数据
     * @throws Exception
     */
    @Test
    @Order(2)
    public void testInsertAndBuy() throws Exception {
        log.info("A");
        TradeInputDO tradeInputREL = new TradeInputDO(1, 1, 1, "REL", 50, "INSERT", "Buy");
        tradeInputService.saveData(tradeInputREL);
        TradeInputDO tradeInputITC = new TradeInputDO(2, 2, 1, "ITC", 40, "INSERT", "Sell");
        tradeInputService.saveData(tradeInputITC);
        TradeInputDO tradeInputINF = new TradeInputDO(3, 3, 1, "INF", 70, "INSERT", "Buy");
        tradeInputService.saveData(tradeInputINF);
        JSONObject json = tradeOutputService.listAll();

        assertEquals(json.getString("REL"), "50");
        assertEquals(json.getString("ITC"), "-40");
        assertEquals(json.getString("INF"), "70");
    }


    /**
     * 测试insertAndBuy的数据
     * @throws Exception
     */
    @Test
    @Order(3)
    public void testUpdateAndBuy() throws Exception {
        log.info("B");
        TradeInputDO tradeInputREL = new TradeInputDO(4, 1, 2, "REL", 60, "UPDATE", "Buy");
        tradeInputService.saveData(tradeInputREL);
        JSONObject json = tradeOutputService.listAll();
        assertEquals(json.getString("REL"), "60");
    }
    /**
     * 测试insertAndBuy的数据
     * @throws Exception
     */
    @Test
    @Order(4)
    public void testCancelAndBuy() throws Exception {
        log.info("C");
        TradeInputDO tradeInputREL = new TradeInputDO(5, 2, 2, "ITC", 30, "CANCEL", "Buy");
        tradeInputService.saveData(tradeInputREL);
        JSONObject json = tradeOutputService.listAll();
        assertEquals(json.getString("ITC"), "0");
    }

    /**
     * 测试insertAndBuy的数据
     * @throws Exception
     */
    @Test
    @Order(5)
    public void testInsertAndSellByOthers() throws Exception {
        log.info("D");
        TradeInputDO tradeInputREL = new TradeInputDO(6, 4, 1, "INF", 20, "INSERT", "Sell");
        tradeInputService.saveData(tradeInputREL);
        JSONObject json = tradeOutputService.listAll();
        assertEquals(json.getString("INF"), "50");
    }




}
