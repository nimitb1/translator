package com.nb.translator.dto.external;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("translation.api")
public class ApiProperties {

    private String url;

}
