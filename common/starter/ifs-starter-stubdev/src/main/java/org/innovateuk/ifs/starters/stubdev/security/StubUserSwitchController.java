package org.innovateuk.ifs.starters.stubdev.security;

import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
public class StubUserSwitchController {

    public static final String PATH_PREFIX = "/stubUser/";

    @Autowired
    private StubUidSupplier stubUidSupplier;

    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    @PostConstruct
    public void init() {
        stubUidSupplier.setUuid(stubDevConfigurationProperties.getDefaultUuid());
    }

    @GetMapping(value = PATH_PREFIX + "{uuid}", produces = TEXT_PLAIN_VALUE)
    public String setUser(@PathVariable String uuid) {
        stubUidSupplier.setUuid(uuid);
        return "User UUID set to " + uuid + " (this is not validated)";
    }

}
