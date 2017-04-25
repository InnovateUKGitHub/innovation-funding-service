package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.junit.Assert.assertTrue;

public class ApplicationFundingDecisionValidatorTest {
    private ApplicationFundingDecisionValidator validator;

    @Before
    public void setUp() {
        validator = new ApplicationFundingDecisionValidator();
    }

    @Test
    public void testIsValid_applicationInSubmittedStateShouldBeValid() throws Exception {
        Application application = newApplication()
                .withFundingDecision(FundingDecisionStatus.FUNDED)
                .withApplicationState(ApplicationState.SUBMITTED).build();

        boolean result = validator.isValid(application);

        assertTrue(result);
    }

    @Test
    public void testIsValid_applicationIsNotSubmittedShouldBeInvalid() throws Exception {
        Application application = newApplication()
                .withFundingDecision(FundingDecisionStatus.FUNDED)
                .withApplicationState(ApplicationState.CREATED).build();

        boolean result = validator.isValid(application);

        assertFalse(result);
    }

    @Test
    public void testIsValid_applicationIsSuccessfulWithNoNotificationSentShouldBeValid() throws Exception {
        Application application = newApplication()
                .withFundingDecision(FundingDecisionStatus.FUNDED)
                .withApplicationState(ApplicationState.APPROVED).build();

        boolean result = validator.isValid(application);

        assertTrue(result);
    }

    @Test
    public void testIsValid_applicationIsSuccessfulWithNotificationSentShouldBeInvalid() throws Exception {
        Application application = newApplication()
                .withFundingDecision(FundingDecisionStatus.FUNDED)
                .withApplicationState(ApplicationState.APPROVED)
                .withManageFundingEmailDate(ZonedDateTime.now()).build();

        boolean result = validator.isValid(application);

        assertFalse(result);
    }

    @Test
    public void testIsValid_applicationIsUnsuccessfulWithNotificationSentShouldBeValid() throws Exception {
        Application application = newApplication()
                .withFundingDecision(FundingDecisionStatus.UNFUNDED)
                .withApplicationState(ApplicationState.REJECTED)
                .withManageFundingEmailDate(ZonedDateTime.now()).build();

        boolean result = validator.isValid(application);

        assertTrue(result);
    }

    @Test
    public void testIsValid_applicationIsUnsuccessfulWithNoNotificationsSentShouldBeValid() throws Exception {
        Application application = newApplication()
                .withFundingDecision(FundingDecisionStatus.UNFUNDED)
                .withApplicationState(ApplicationState.REJECTED)
                .build();

        boolean result = validator.isValid(application);

        assertTrue(result);
    }
}