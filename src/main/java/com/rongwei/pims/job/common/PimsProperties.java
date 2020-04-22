package com.rongwei.pims.job.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties("job.pims") // 或(prefix = "custom")
public class PimsProperties {
    private String serverIp;
    private String serverPort;
    private String userID;
    private String passWord;
}
