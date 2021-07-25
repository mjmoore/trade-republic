package io.mjmoore.traderepublic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "partner")
public class PartnerConfig {
    private String url;
    private String instrumentsUrl;
    private String quotesUrl;
}
