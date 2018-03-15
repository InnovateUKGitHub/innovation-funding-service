package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.finance.totals.service.ApplicationFinanceTotalsSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendFinanceTotalsActionTest {

    @Mock
    private ApplicationFinanceTotalsSender financeTotalsSender;

    @InjectMocks
    private SendFinanceTotalsAction sendFinanceTotalsAction;

    @Before
    public void init() {
        sendFinanceTotalsAction = new SendFinanceTotalsAction();
        initMocks(this);
    }

    @Test
    public void doExecute_toggleOn() {
        setFinanceTotalsToggle(true);

        Application application = newApplication().build();

        sendFinanceTotalsAction.doExecute(application, null);

        verify(financeTotalsSender).sendFinanceTotalsForApplication(application.getId());
    }

    @Test
    public void doExecute_toggleOff() {
        setFinanceTotalsToggle(false);

        Application application = newApplication().build();

        sendFinanceTotalsAction.doExecute(application, null);

        verifyNoMoreInteractions(financeTotalsSender);
    }

    private void setFinanceTotalsToggle(boolean toggleValue) {
        Field financeTotalsEnabledToggle = ReflectionUtils.findField(SendFinanceTotalsAction.class, "financeTotalsEnabled");
        ReflectionUtils.makeAccessible(financeTotalsEnabledToggle);
        ReflectionUtils.setField(financeTotalsEnabledToggle, sendFinanceTotalsAction, toggleValue);
    }
}