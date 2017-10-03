package org.innovateuk.ifs.shibboleth.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@ConfigurationProperties(prefix = ApiProperties.PREFIX)
public class ApiProperties {

    public static final String PREFIX = "shibboleth.api";

    @NotNull
    private List<String> keys;


    public List<String> getKeys() {
        return keys;
    }


    public void setKeys(final List<String> keys) {
        this.keys = keys;
    }
}
