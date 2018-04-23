package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.EnumSet;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;

public class PublicContentServiceSecurityTest extends BaseServiceSecurityTest<PublicContentService> {

    private static final EnumSet<Role> COMP_ADMIN_ROLES = EnumSet.of(COMP_ADMIN, PROJECT_FINANCE);
    private static final EnumSet<Role> ALL_INTERNAL_USERS = EnumSet.of(COMP_ADMIN, PROJECT_FINANCE, SUPPORT, INNOVATION_LEAD);


    @Override
    protected Class<? extends PublicContentService> getClassUnderTest() {
        return PublicContentServiceImpl.class;
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

    private void runAsAllowedRoles(EnumSet<Role> allowedRoles, Runnable serviceCall) {
        allowedRoles.forEach(roleType -> runAsRole(roleType, serviceCall));
        complementOf(allowedRoles).forEach(roleType -> assertAccessDeniedAsRole(roleType, serviceCall, () -> {}));
    }

    private void runAsRole(Role roleType, Runnable serviceCall) {
        setLoggedInUser(
                newUserResource()
                        .withRolesGlobal(singletonList(
                                Role.getByName(roleType.getName())
                        ))
                        .build());
        serviceCall.run();
    }

    private void assertAccessDeniedAsRole(Role roleType, Runnable serviceCall, Runnable verifications) {
        runAsRole(roleType, () -> assertAccessDenied(serviceCall, verifications) );
    }
}
