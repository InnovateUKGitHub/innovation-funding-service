package org.innovateuk.ifs;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "ifs.loan.partb.enabled=true" })
public class EndpointDocumentation extends EndpointDocumentationTest {
    // runs documentEndPoints() within the current web application context
}
