package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.EnumSet;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.user.resource.UserRoleType.SUPPORT;

public class PublicContentServiceSecurityTest extends BaseServiceSecurityTest<PublicContentService> {

    private static final EnumSet<UserRoleType> COMP_ADMIN_ROLES = EnumSet.of(COMP_ADMIN, PROJECT_FINANCE);
    private static final EnumSet<UserRoleType> ALL_INTERNAL_USERS = EnumSet.of(COMP_ADMIN, PROJECT_FINANCE, SUPPORT);


    @Override
    protected Class<? extends PublicContentService> getClassUnderTest() {
        return TestPublicContentService.class;
    }

    @Test
    public void testGetByCompetitionId() {
        runAsAllowedRoles(ALL_INTERNAL_USERS, () -> classUnderTest.findByCompetitionId(1L));
    }

    @Test
    public void testInitialise() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.initialiseByCompetitionId(1L));
    }

    @Test
    public void testUpdateSection() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.updateSection(newPublicContentResource().build(), PublicContentSectionType.DATES));
    }

    @Test
    public void testPublish() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.publishByCompetitionId(1L));
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

    public static class TestPublicContentService implements PublicContentService {

        @Override
        public ServiceResult<PublicContentResource> findByCompetitionId(Long id) {
            return null;
        }

        @Override
        public ServiceResult<Void> initialiseByCompetitionId(Long id) {
            return null;
        }

        @Override
        public ServiceResult<Void> publishByCompetitionId(Long id) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateSection(PublicContentResource resource, PublicContentSectionType section) {
            return null;
        }

        @Override
        public ServiceResult<Void> markSectionAsComplete(PublicContentResource resource, PublicContentSectionType section) {
            return null;
        }
    }
}
