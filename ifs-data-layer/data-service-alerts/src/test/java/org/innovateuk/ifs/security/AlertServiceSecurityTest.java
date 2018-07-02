package org.innovateuk.ifs.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.alert.builder.AlertResourceBuilder;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.transactional.AlertService;
import org.innovateuk.ifs.transactional.AlertServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlertServiceSecurityTest extends BaseServiceSecurityTest<AlertService> {

    private AlertPermissionRules alertPermissionRules;
    private AlertLookupStrategy alertLookupStrategy;

    @Override
    protected Class<? extends AlertService> getClassUnderTest() {
        return AlertServiceImpl.class;
    }

    @Before
    public void setUp() throws Exception {
        alertPermissionRules = getMockPermissionRulesBean(AlertPermissionRules.class);
        alertLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AlertLookupStrategy.class);
    }

    @Test
    public void create() throws Exception {
        final AlertResource alertResource = AlertResourceBuilder.newAlertResource()
                .build();
        assertAccessDenied(
                () -> classUnderTest.create(alertResource),
                () -> {
                    verify(alertPermissionRules).systemMaintenanceUserCanCreateAlerts(isA(AlertResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void delete() throws Exception {
        when(alertLookupStrategy.getAlertResource(9999L)).thenReturn(AlertResourceBuilder.newAlertResource()
                .withId(9999L)
                .build());

        assertAccessDenied(
                () -> classUnderTest.delete(9999L),
                () -> {
                    verify(alertPermissionRules).systemMaintenanceUserCanDeleteAlerts(isA(AlertResource.class), isA(UserResource.class));
                });
    }
}
