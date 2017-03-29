package org.innovateuk.ifs.thread.attachment.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.financecheck.security.AttachmentPermissionsRules;

import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.thread.security.ProjectFinanceThreadsTestData.projectFinanceWithUserAsFinanceContact;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectFinanceAttachmentPermissionRulesTest extends BasePermissionRulesTest<AttachmentPermissionsRules> {
    private AttachmentResource attachmentResource;
    private ProjectResource projectResource;
    private UserResource projectFinanceUser;
    private UserResource projectPartnerUser;
    private UserResource intruder;

    @Mock
    private ProjectFinanceQueryPermissionRules queryPermissionRulesMock;

    @Before
    public void setUp() throws Exception {
        projectResource = newProjectResource().withId(77L).build();
        attachmentResource = new AttachmentResource(9283L, "fileName", "application/json", 1024);
        projectFinanceUser = projectFinanceUser();
        projectPartnerUser = getUserWithRole(PARTNER);

        intruder = newUserResource().withId(1993L).withRolesGlobal(newRoleResource()
                .withType(PARTNER).build(1)).build();
        intruder.setId(1993L);
    }

    @Override
    protected AttachmentPermissionsRules supplyPermissionRulesUnderTest() {
        return new AttachmentPermissionsRules();
    }

    @Test
    public void testThatOnlyProjectFinanceAndFinanceContactUsersCanUploadAttachments() throws Exception {
        assertTrue(rules.projectFinanceCanUploadAttachments(projectResource, projectFinanceUser));
        when(projectUserRepositoryMock.findByProjectIdAndRoleAndUserId(projectResource.getId(), PROJECT_PARTNER, projectPartnerUser.getId()))
                .thenReturn(newProjectUser().withUser(newUser().withId(projectPartnerUser.getId()).build()).build());
        assertTrue(rules.projectPartnersCanUploadAttachments(projectResource, projectPartnerUser));
    }

    @Test
    public void testThatANonProjectFinanceOrFinanceContactUserCanNotUploadAttachments() throws Exception {
        when(projectUserRepositoryMock.findByProjectIdAndRoleAndUserId(projectResource.getId(), PROJECT_PARTNER, intruder.getId()))
                .thenReturn(null);
        assertFalse(rules.projectPartnersCanUploadAttachments(projectResource, intruder));
    }

    @Test
    public void testThatProjectFinanceUsersCanFetchAnyAttachment() throws Exception {
        assertTrue(rules.projectFinanceUsersCanFetchAnyAttachment(attachmentResource, projectFinanceUser));
    }

    @Test
    public void testThatFinanceContactUsersCanAlwaysFetchTheAttachmentsTheyHaveUploaded() throws Exception {
        when(attachmentMapperMock.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectPartnerUser.getId()));
        assertTrue(rules.financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(attachmentResource, projectPartnerUser));
    }

    @Test
    public void testThatFinanceContactUsersCanFetchAttachmentsOfQueriesTheyAreRelatedTo() throws Exception {
        final Query query = query();
        final QueryResource queryResource = toResource(query);
        when(queryRepositoryMock.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(query));
        when(queryMapper.mapToResource(query)).thenReturn(queryResource);
        when(attachmentMapperMock.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        when(projectFinanceRepositoryMock.findOne(query.contextClassPk())).thenReturn(projectFinanceWithUserAsFinanceContact(projectPartnerUser));
        when(queryPermissionRulesMock.projectFinanceUsersCanViewQueries(queryResource, projectPartnerUser)).thenReturn(true);
        assertTrue(rules.financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(attachmentResource, projectPartnerUser));
    }

    @Test
    public void testThatFinanceContactUsersCannotFetchAttachmentsOfQueriesTheyAreNotRelatedTo() throws Exception {
        final Query query = query();
        final QueryResource queryResource = toResource(query);
        when(queryRepositoryMock.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(query()));
        when(queryMapper.mapToResource(query)).thenReturn(queryResource);
        when(attachmentMapperMock.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        final UserResource unrelatedFinanceContactUser = newUserResource().withId(projectPartnerUser.getId() * 7).build();
        when(projectFinanceRepositoryMock.findOne(query.contextClassPk())).thenReturn(projectFinanceWithUserAsFinanceContact(projectPartnerUser));
        when(queryPermissionRulesMock.projectFinanceUsersCanViewQueries(queryResource, unrelatedFinanceContactUser)).thenReturn(false);
        assertFalse(rules.financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(attachmentResource, unrelatedFinanceContactUser));
    }

    @Test
    public void testThatUserCanDeleteAttachmentWhenTheUploaderAndWhenTheAttachmentIsStillOrphan() {
        when(attachmentMapperMock.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        when(queryRepositoryMock.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(emptyList());
        assertTrue(rules.onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(attachmentResource, projectFinanceUser));
    }

    @Test
    public void testThatUserCanNotDeleteAttachmentIfNotTheUploaderEvenIfAttachmentIsStillOrphan() {
        when(attachmentMapperMock.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        when(queryRepositoryMock.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(emptyList());
        assertFalse(rules.onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(attachmentResource, projectPartnerUser));
    }

    @Test
    public void testThatUserCanNotDeleteAttachmentEvenIfUploaderOnceTheAttachmentIsNoLongerOrphan() {
        when(attachmentMapperMock.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectPartnerUser.getId()));
        when(queryRepositoryMock.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(query()));
        assertFalse(rules.onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(attachmentResource, projectPartnerUser));
    }

    private QueryResource toResource(Query query) {
        return new QueryResource(query.id(), query.contextClassPk(), emptyList(),
                query.section(), query.title(), query.isAwaitingResponse(), query.createdOn());
    }

    private Query query() {
        return new Query(92L, 1993L, "", null, null, "", LocalDateTime.now());
    }

    private Attachment asDomain(AttachmentResource attachmentResource, Long uploaderId) {
        return new Attachment(attachmentResource.id, newUser().withId(uploaderId).build(),
                newFileEntry().withFilesizeBytes(attachmentResource.sizeInBytes).withMediaType(attachmentResource.mediaType).build());
    }

}