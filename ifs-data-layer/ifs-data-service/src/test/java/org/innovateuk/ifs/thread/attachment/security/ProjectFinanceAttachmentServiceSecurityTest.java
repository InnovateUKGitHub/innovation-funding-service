package org.innovateuk.ifs.thread.attachment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.financechecks.security.AttachmentPermissionsRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.attachments.security.AttachmentLookupStrategy;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentsServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class ProjectFinanceAttachmentServiceSecurityTest extends BaseServiceSecurityTest<ProjectFinanceAttachmentService> {

    private AttachmentPermissionsRules attachmentPermissionsRules;
    private AttachmentLookupStrategy attachmentLookupStrategy;
    private ProjectLookupStrategy projectLookupStrategy;

    @Override
    protected Class<? extends ProjectFinanceAttachmentService> getClassUnderTest() {
        return ProjectFinanceAttachmentsServiceImpl.class;
    }

    @Before
    public void lookupPermissionRules() {
        attachmentPermissionsRules = getMockPermissionRulesBean(AttachmentPermissionsRules.class);
        attachmentLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AttachmentLookupStrategy.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void upload() throws Exception {
        when(projectLookupStrategy.getProjectResource(77L))
                .thenReturn(newProjectResource().withId(77L).build());

        assertAccessDenied(
                () -> classUnderTest.upload("application.pdf", "3234", "filename.pdf", 77L, null),
                () -> {
                    verify(attachmentPermissionsRules)
                            .projectFinanceCanUploadAttachments(isA(ProjectResource.class), isA(UserResource.class));
                    verify(attachmentPermissionsRules)
                            .projectPartnersCanUploadAttachments(isA(ProjectResource.class), isA(UserResource.class));
                    verify(attachmentPermissionsRules)
                            .competitionFinanceCanUploadAttachments(isA(ProjectResource.class), isA(UserResource.class));

                    verifyNoMoreInteractions(attachmentPermissionsRules);
                });
    }

    @Test
    public void findOne() throws Exception {
        UserResource user = new UserResource();
        setLoggedInUser(user);

        when(classUnderTestMock.findOne(1L))
                .thenReturn(serviceSuccess(
                        new AttachmentResource(33L, "name", "application/pdf", 2345, null)
                ));

        assertAccessDenied(() -> classUnderTest.findOne(1L), () -> {
            verify(attachmentPermissionsRules)
                    .projectFinanceUsersCanFetchAnyAttachment(isA(AttachmentResource.class), eq(user));
            verify(attachmentPermissionsRules)
                    .financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(isA(AttachmentResource.class), eq(user));
            verify(attachmentPermissionsRules)
                    .competitionFinanceUsersCanFetchAnyAttachment(isA(AttachmentResource.class), eq(user));
            verifyNoMoreInteractions(attachmentPermissionsRules);
        });
    }

    @Test
    public void downloadAttachment() throws Exception {
        UserResource user = new UserResource();
        setLoggedInUser(user);
        when(attachmentLookupStrategy.findById(3L))
                .thenReturn(new AttachmentResource(3L, "file", "application/pdf", 3456, null));

        assertAccessDenied(() -> classUnderTest.attachmentFileAndContents(3L), () -> {
            verify(attachmentPermissionsRules)
                    .projectFinanceUsersCanDownloadAnyAttachment(isA(AttachmentResource.class), eq(user));
            verify(attachmentPermissionsRules)
                    .financeContactUsersCanOnlyDownloadAnAttachmentIfRelatedToItsQuery(isA(AttachmentResource.class), eq(user));
            verify(attachmentPermissionsRules)
                    .competitionFinanceUsersCanOnlyDownloadAnAttachmentIfRelatedToItsQuery(isA(AttachmentResource.class), eq(user));
            verifyNoMoreInteractions(attachmentPermissionsRules);
        });

    }

    @Test
    public void deleteAttachment() throws Exception {
        UserResource user = new UserResource();
        setLoggedInUser(user);
        when(attachmentLookupStrategy.findById(3L))
                .thenReturn(new AttachmentResource(3L, "file", "application/pdf", 3456, null));

        assertAccessDenied(() -> classUnderTest.delete(3L), () -> {
            verify(attachmentPermissionsRules)
                    .onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(isA(AttachmentResource.class), eq(user));
            verifyNoMoreInteractions(attachmentPermissionsRules);
        });
    }
}
