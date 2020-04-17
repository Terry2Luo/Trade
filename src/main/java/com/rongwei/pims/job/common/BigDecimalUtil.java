package com.rongwei.pims.job.common;

import java.math.BigDecimal;

public class BigDecimalUtil {

   public static BigDecimal  doAdd( BigDecimal a , BigDecimal b  ){
       BigDecimal rtn = new BigDecimal(0);
       if(a != null){
           rtn = rtn.add(a);
       }
       if(b != null){
           rtn = rtn.add(b);
       }
       return rtn;

   }

    public static BigDecimal  doSub( BigDecimal a , BigDecimal b  ){
        BigDecimal rtn = new BigDecimal(0);
        if(a != null){
            rtn = rtn.add(a);
        }
        if(b != null){
            rtn = rtn.subtract(b);
        }
        return rtn;

    }

}
