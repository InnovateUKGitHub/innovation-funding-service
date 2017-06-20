package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ContentGroupServiceSecurityTest extends BaseServiceSecurityTest<ContentGroupService> {

    private static final EnumSet<UserRoleType> COMP_ADMIN_ROLES = EnumSet.of(COMP_ADMIN, PROJECT_FINANCE);
    private ContentGroupPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(ContentGroupPermissionRules.class);

        initMocks(this);
    }


    @Override
    protected Class<? extends ContentGroupService> getClassUnderTest() {
        return TestContentGroupService.class;
    }

    @Test
    public void testUploadFile() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.uploadFile(1L, mock(FileEntryResource.class), mock(Supplier.class)));
    }

    @Test
    public void testRemoveFile() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.removeFile(1L));
    }

    @Test
    public void testSaveContentGroups() {
        runAsAllowedRoles(COMP_ADMIN_ROLES, () -> classUnderTest.saveContentGroups(newPublicContentResource().build(), newPublicContent().build(), PublicContentSectionType.DATES));
    }

    @Test
    public void testGetFileDetails() {
        assertAccessDenied(() -> classUnderTest.getFileDetails(1L), this::verifyPermissionRules);
    }

    @Test
    public void testGetFileContents() {
        assertAccessDenied(() -> classUnderTest.getFileContents(1L), this::verifyPermissionRules);
    }

    private void verifyPermissionRules() {
        verify(rules).externalUsersCanViewPublishedContentGroupFiles(any(), any());
        verify(rules).internalUsersCanViewAllContentGroupFiles(any(), any());
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

    public static class TestContentGroupService implements ContentGroupService {

        @Override
        public ServiceResult<Void> uploadFile(long contentGroupId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> removeFile(Long contentGroupId) {
            return null;
        }

        @Override
        public ServiceResult<Void> saveContentGroups(PublicContentResource resource, PublicContent publicContent, PublicContentSectionType section) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> getFileDetails(long contentGroupId) {
            return null;
        }

        @Override
        public ServiceResult<FileAndContents> getFileContents(long contentGroupId) {
            return null;
        }
    }
}
