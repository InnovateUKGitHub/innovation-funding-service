package com.worth.ifs.alert.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.transactional.AlertService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlertServiceSecurityTest extends BaseServiceSecurityTest<AlertService> {

    private AlertPermissionRules alertPermissionRules;
    private AlertLookupStrategy alertLookupStrategy;

    @Override
    protected Class<? extends AlertService> getClassUnderTest() {
        return TestAlertService.class;
    }

    @Before
    public void setUp() throws Exception {
        alertPermissionRules = getMockPermissionRulesBean(AlertPermissionRules.class);
        alertLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AlertLookupStrategy.class);
    }

    @Test
    public void test_create() throws Exception {
        final AlertResource alertResource = newAlertResource()
                .build();
        assertAccessDenied(
                () -> classUnderTest.create(alertResource),
                () -> {
                    verify(alertPermissionRules).systemMaintenanceUserCanCreateAlerts(isA(AlertResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void test_delete() throws Exception {
        when(alertLookupStrategy.getAlertResource(9999L)).thenReturn(newAlertResource()
                .withId(9999L)
                .build());

        assertAccessDenied(
                () -> classUnderTest.delete(9999L),
                () -> {
                    verify(alertPermissionRules).systemMaintenanceUserCanDeleteAlerts(isA(AlertResource.class), isA(UserResource.class));
                });
    }

    public static class TestAlertService implements AlertService {
        @Override
        public ServiceResult<List<AlertResource>> findAllVisible() {
            return null;
        }

        @Override
        public ServiceResult<List<AlertResource>> findAllVisibleByType(final AlertType type) {
            return null;
        }

        @Override
        public ServiceResult<AlertResource> findById(final Long id) {
            return null;
        }

        @Override
        public ServiceResult<AlertResource> create(final AlertResource alertResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> delete(final Long id) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteAllByType(final AlertType type) {
            return null;
        }
    }
}
