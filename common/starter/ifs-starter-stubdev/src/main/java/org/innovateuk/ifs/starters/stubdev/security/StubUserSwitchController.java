package org.innovateuk.ifs.starters.stubdev.security;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Convenience controller method used to swap users out at runtime.
 *
 * Also sets up the default user on construction.
 */
@RestController
@Profile(IfsProfileConstants.STUBDEV)
public class StubUserSwitchController {

    public static final String PATH_PREFIX = "/stubUser/";

    @Autowired
    private StubUidSupplier stubUidSupplier;

    /**
     * Sets the active stub user.
     *
     * Technically should be PUT, but its more convenient this way.
     *
     * @param uuid the user uuid that must exist in the database
     * @return a simple text response with the set value
     */
    @GetMapping(value = PATH_PREFIX + "{uuid}", produces = TEXT_PLAIN_VALUE)
    public String setUser(@PathVariable String uuid) {
        stubUidSupplier.setUuid(uuid);
        return "User UUID set to " + uuid + " (this is not validated)";
    }

}
