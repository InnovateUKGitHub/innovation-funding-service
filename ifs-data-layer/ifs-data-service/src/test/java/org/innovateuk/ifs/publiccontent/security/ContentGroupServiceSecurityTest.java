package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupCompositeId;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.function.Supplier;

import static java.util.EnumSet.complementOf;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ContentGroupServiceSecurityTest extends BaseServiceSecurityTest<ContentGroupService> {

    private static final EnumSet<Role> COMP_ADMIN_ROLES = EnumSet.of(COMP_ADMIN, PROJECT_FINANCE, IFS_ADMINISTRATOR, SUPER_ADMIN_USER, SYSTEM_MAINTAINER);
    private ContentGroupPermissionRules rules;
    private ContentGroupLookupStrategy contentGroupLookupStrategies;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(ContentGroupPermissionRules.class);
        contentGroupLookupStrategies = getMockPermissionEntityLookupStrategiesBean(ContentGroupLookupStrategy.class);
        initMocks(this);
    }


    @Override
    protected Class<? extends ContentGroupService> getClassUnderTest() {
        return ContentGroupServiceImpl.class;
    }

    @Test
    public void uploadFile() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.uploadFile(1L, mock(FileEntryResource.class), mock(Supplier.class)));
    }

    @Test
    public void removeFile() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.removeFile(1L));
    }

    @Test
    public void saveContentGroups() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.saveContentGroups(newPublicContentResource().build(), newPublicContent().build(), PublicContentSectionType.DATES));
    }

    @Test
    public void getFileDetails() {
        when(contentGroupLookupStrategies.getContentGroupCompositeId(1L)).thenReturn(ContentGroupCompositeId.id(1L));
        assertAccessDenied(() -> classUnderTest.getFileDetails(1L), this::verifyPermissionRules);
    }

    @Test
    public void getFileContents() {
        when(contentGroupLookupStrategies.getContentGroupCompositeId(1L)).thenReturn(ContentGroupCompositeId.id(1L));
        assertAccessDenied(() -> classUnderTest.getFileContents(1L), this::verifyPermissionRules);
    }

    private void verifyPermissionRules() {
        verify(rules).externalUsersCanViewPublishedContentGroupFiles(any(), any());
        verify(rules).internalUsersCanViewAllContentGroupFiles(any(), any());
    }

    private void runAsAllowedRoles(EnumSet<Role> allowedRoles, Runnable serviceCall) {
        if (allowedRoles.contains(Role.ASSESSOR)) {
            allowedRoles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);
        } else if (allowedRoles.contains(Role.PROJECT_FINANCE) || allowedRoles.contains(Role.IFS_ADMINISTRATOR)) {
            allowedRoles.add(Role.SYSTEM_MAINTAINER);
        }
        allowedRoles.forEach(roleType -> runAsRole(roleType, serviceCall));
        complementOf(allowedRoles).forEach(roleType -> assertAccessDeniedAsRole(roleType, serviceCall, () -> {}));
    }

    private void runAsRole(Role roleType, Runnable serviceCall) {
        setLoggedInUser(
                newUserResource()
                        .withRoleGlobal(roleType)
                        .build());
        serviceCall.run();
    }

    private void assertAccessDeniedAsRole(Role roleType, Runnable serviceCall, Runnable verifications) {
        runAsRole(roleType, () -> assertAccessDenied(serviceCall, verifications) );
    }
}
