package org.innovateuk.ifs.thread.attachment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.financecheck.security.AttachmentPermissionsRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.threads.attachments.security.AttachmentLookupStrategy;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class ProjectFinanceAttachmentServiceSecurityTest extends BaseServiceSecurityTest<ProjectFinanceAttachmentService> {

    private AttachmentPermissionsRules attachmentPermissionsRules;
    private AttachmentLookupStrategy attachmentLookupStrategy;
    private ProjectLookupStrategy projectLookupStrategy;

    @Override
    protected Class<? extends ProjectFinanceAttachmentService> getClassUnderTest() {
        return TestProjectFinanceAttachmentService.class;
    }

    @Before
    public void lookupPermissionRules() {
        attachmentPermissionsRules = getMockPermissionRulesBean(AttachmentPermissionsRules.class);
        attachmentLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AttachmentLookupStrategy.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void test_upload() throws Exception {
        final QueryResource queryResource = new QueryResource(null, null, null, null, null, false, null);
        when(projectLookupStrategy.getProjectResource(77L)).thenReturn(newProjectResource().withId(77L).build());
        assertAccessDenied(
                () -> classUnderTest.upload("application.pdf", "3234", "filename.pdf", 77L, null),
                () -> {
                    verify(attachmentPermissionsRules).projectFinanceCanUploadAttachments(isA(ProjectResource.class), isA(UserResource.class));
                    verify(attachmentPermissionsRules).projectPartnersCanUploadAttachments(isA(ProjectResource.class), isA(UserResource.class));

                    verifyNoMoreInteractions(attachmentPermissionsRules);
                });
    }

    @Test
    public void test_findOne() throws Exception {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.findOne(1L), () -> {
            verify(attachmentPermissionsRules).projectFinanceUsersCanFetchAnyAttachment(isA(AttachmentResource.class), isNull(UserResource.class));
            verify(attachmentPermissionsRules).financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(isA(AttachmentResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(attachmentPermissionsRules);
        });
    }

    @Test
    public void test_downloadAttachment() throws Exception {
        setLoggedInUser(null);
        when(attachmentLookupStrategy.findById(3L))
                .thenReturn(new AttachmentResource(3L, "file", "application/pdf", 3456));

        assertAccessDenied(() -> classUnderTest.attachmentFileAndContents(3L), () -> {
            verify(attachmentPermissionsRules).projectFinanceUsersCanDownloadAnyAttachment(isA(AttachmentResource.class), isNull(UserResource.class));
            verify(attachmentPermissionsRules).financeContactUsersCanOnlyDownloadAnAttachmentIfRelatedToItsQuery(isA(AttachmentResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(attachmentPermissionsRules);
        });

    }

    @Test
    public void test_deleteAttachment() throws Exception {
        setLoggedInUser(null);
        when(attachmentLookupStrategy.findById(3L))
                .thenReturn(new AttachmentResource(3L, "file", "application/pdf", 3456));

        assertAccessDenied(() -> classUnderTest.delete(3L), () -> {
            verify(attachmentPermissionsRules).onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(isA(AttachmentResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(attachmentPermissionsRules);
        });
    }


    public static class TestProjectFinanceAttachmentService implements ProjectFinanceAttachmentService {

        @Override
        public ServiceResult<AttachmentResource> upload(String contentType, String contentLength, String originalFilename,
                                                        Long projectId, HttpServletRequest request) {
            return ServiceResult.serviceSuccess(new AttachmentResource(33L, "name",
                    "application/pdf", 2345));
        }

        @Override
        public ServiceResult<AttachmentResource> findOne(Long attachmentId) {
            return ServiceResult.serviceSuccess(new AttachmentResource(33L, "name",
                    "application/pdf", 2345));
        }

        @Override
        public ServiceResult<FileAndContents> attachmentFileAndContents(Long attachmentId) {
            return null;
        }

        @Override
        public ServiceResult<Void> delete(Long attachmentId) {
            return ServiceResult.serviceSuccess();
        }
    }


}
