package org.innovateuk.ifs.finance.pact;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactSource;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@RunWith(SpringRestPactRunner.class)
@Provider("CostTotalProvider")
@PactSource(SpringPactFolderLoader.class)
@VerificationReports({"console", "markdown", "json"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
public class CostTotalControllerPactVerificationTest {

    @TestTarget
    public final Target target = new SpringBootHttpTarget();

    @State("SendCostTotalsState")
    public void setState() {
        // Any preparation that needs to take place for the "SendCostTotalsState" state
    }
}