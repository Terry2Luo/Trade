package com.rongwei.pims.job.domain;

import lombok.Data;
import java.io.Serializable;

@Data
public class AcOperatorVO extends AcOperator implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 部门id
         * */
        private Long orgId;
}
