package org.innovateuk.ifs.shibboleth.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@ConfigurationProperties(prefix = PasswordPolicyProperties.PREFIX)
public class PasswordPolicyProperties {

    public static final String PREFIX = "shibboleth.password.policy";

    @NotNull
    private List<String> blacklist;


    public List<String> getBlacklist() {
        return blacklist;
    }


    public void setBlacklist(final List<String> blacklist) {
        this.blacklist = blacklist;
    }
}
