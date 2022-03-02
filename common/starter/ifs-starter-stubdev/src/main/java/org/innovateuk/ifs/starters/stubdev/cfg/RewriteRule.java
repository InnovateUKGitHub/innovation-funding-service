package org.innovateuk.ifs.starters.stubdev.cfg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Convenience pair implementation for use in StubDevConfigurationProperties
 */
@Getter
@Setter
@NoArgsConstructor
public class RewriteRule {

    private String existing;
    private String rewrite;

}
