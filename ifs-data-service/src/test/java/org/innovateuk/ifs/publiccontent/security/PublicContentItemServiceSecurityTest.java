package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentItemService;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.EnumSet;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;

public class PublicContentItemServiceSecurityTest extends BaseServiceSecurityTest<PublicContentItemService> {
    @Override
    protected Class<? extends PublicContentItemService> getClassUnderTest() {
        return TestPublicContentItemService.class;
    }

    @Test
    public void testGetByCompetitionId() {
        runAsRole(SYSTEM_REGISTRATION_USER, () -> classUnderTest.findFilteredItems(Optional.of(1L), Optional.of("test"), Optional.of(1), 1));
    }

    @Test
    public void testInitialise() {
        runAsRole(SYSTEM_REGISTRATION_USER, () -> classUnderTest.byCompetitionId(1L));
    }

    private void runAsAllowedRoles(EnumSet<UserRoleType> allowedRoles, Runnable serviceCall) {
        allowedRoles.forEach(roleType -> runAsRole(roleType, serviceCall));
        complementOf(allowedRoles).forEach(roleType -> assertAccessDeniedAsRole(roleType, serviceCall, () -> {}));
    }

    private void runAsRole(UserRoleType roleType, Runnable serviceCall) {
        setLoggedInUser(
                newUserResource()
                        .withRolesGlobal(singletonList(
                                newRoleResource()
                                        .withType(roleType)
                                        .build()
                                )
                        )
                        .build());
        serviceCall.run();
    }

    private void assertAccessDeniedAsRole(UserRoleType roleType, Runnable serviceCall, Runnable verifications) {
        runAsRole(roleType, () -> assertAccessDenied(serviceCall, verifications) );
    }

    public static class TestPublicContentItemService implements PublicContentItemService {
        @Override
        public ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Integer pageSize) { return null; }

        @Override
        public ServiceResult<PublicContentItemResource> byCompetitionId(Long id) { return null; }

    }
}
