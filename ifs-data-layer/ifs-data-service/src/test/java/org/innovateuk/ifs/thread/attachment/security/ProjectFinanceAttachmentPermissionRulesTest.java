package org.innovateuk.ifs.thread.attachment.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.financechecks.security.AttachmentPermissionsRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.repository.MessageThreadRepository;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.thread.security.ProjectFinanceThreadsTestData.projectFinanceWithUserAsFinanceContact;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectFinanceAttachmentPermissionRulesTest extends BasePermissionRulesTest<AttachmentPermissionsRules> {
    private AttachmentResource attachmentResource;
    private ProjectResource projectResource;
    private UserResource projectFinanceUser;
    private UserResource projectPartnerUser;
    private UserResource intruder;

    @Mock
    private ProjectFinanceQueryPermissionRules queryPermissionRules;

    @Mock
    private QueryRepository queryRepository;

    @Mock
    private MessageThreadRepository threadRepository;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private QueryMapper queryMapper;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Before
    public void setUp() throws Exception {
        projectResource = newProjectResource().withId(77L).build();
        attachmentResource = new AttachmentResource(9283L, "fileName", "application/json", 1024, null);
        projectFinanceUser = projectFinanceUser();
        projectPartnerUser = getUserWithRole(PARTNER);

        intruder = newUserResource().withId(1993L).withRolesGlobal(singletonList(PARTNER)).build();
        intruder.setId(1993L);
    }

    @Override
    protected AttachmentPermissionsRules supplyPermissionRulesUnderTest() {
        return new AttachmentPermissionsRules();
    }

    @Test
    public void thatOnlyProjectFinanceAndFinanceContactUsersCanUploadAttachments() {
        assertTrue(rules.projectFinanceCanUploadAttachments(projectResource, projectFinanceUser));
        when(projectUserRepository.findByProjectIdAndRoleAndUserId(projectResource.getId(), PROJECT_PARTNER, projectPartnerUser.getId()))
                .thenReturn(newProjectUser().withUser(newUser().withId(projectPartnerUser.getId()).build()).build());
        assertTrue(rules.projectPartnersCanUploadAttachments(projectResource, projectPartnerUser));
    }

    @Test
    public void thatANonProjectFinanceOrFinanceContactUserCanNotUploadAttachments() {
        when(projectUserRepository.findByProjectIdAndRoleAndUserId(projectResource.getId(), PROJECT_PARTNER, intruder.getId()))
                .thenReturn(null);
        assertFalse(rules.projectPartnersCanUploadAttachments(projectResource, intruder));
    }

    @Test
    public void thatProjectFinanceUsersCanFetchOrphanAttachmentTheyHaveUploaded() {
        when(attachmentMapper.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        assertTrue(rules.projectFinanceUsersCanFetchAnyAttachment(attachmentResource, projectFinanceUser));
    }

    @Test
    public void thatProjectFinanceUsersCanFetchAnySavedQueryAttachment() {
        when(threadRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(mock(Thread.class)));
        assertTrue(rules.projectFinanceUsersCanFetchAnyAttachment(attachmentResource, projectFinanceUser));
    }

    @Test
    public void thatFinanceContactUsersCanAlwaysFetchTheOrphanAttachmentsTheyHaveUploaded() {
        when(attachmentMapper.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectPartnerUser.getId()));
        assertTrue(rules.financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(attachmentResource, projectPartnerUser));
    }

    @Test
    public void thatFinanceContactUsersCanFetchAttachmentsOfQueriesTheyAreRelatedTo() {
        final Query query = query();
        final QueryResource queryResource = toResource(query);
        when(threadRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(mock(Thread.class)));
        when(queryRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(query));
        when(queryMapper.mapToResource(query)).thenReturn(queryResource);
        when(attachmentMapper.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        when(projectFinanceRepository.findById(query.contextClassPk())).thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(projectPartnerUser)));
        when(queryPermissionRules.projectFinanceUsersCanViewQueries(queryResource, projectPartnerUser)).thenReturn(true);
        assertTrue(rules.financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(attachmentResource, projectPartnerUser));
    }

    @Test
    public void thatFinanceContactUsersCannotFetchAttachmentsOfQueriesTheyAreNotRelatedTo() {
        final Query query = query();
        final QueryResource queryResource = toResource(query);
        when(queryRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(query()));
        when(queryMapper.mapToResource(query)).thenReturn(queryResource);
        when(attachmentMapper.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        final UserResource unrelatedFinanceContactUser = newUserResource().withId(projectPartnerUser.getId() * 7).build();
        when(projectFinanceRepository.findById(query.contextClassPk())).thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(projectPartnerUser)));
        when(queryPermissionRules.projectFinanceUsersCanViewQueries(queryResource, unrelatedFinanceContactUser)).thenReturn(false);
        assertFalse(rules.financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(attachmentResource, unrelatedFinanceContactUser));
    }

    @Test
    public void thatUserCanDeleteAttachmentWhenTheUploaderAndWhenTheAttachmentIsStillOrphan() {
        when(attachmentMapper.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        when(queryRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(emptyList());
        assertTrue(rules.onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(attachmentResource, projectFinanceUser));
    }

    @Test
    public void thatUserCanNotDeleteAttachmentIfNotTheUploaderEvenIfAttachmentIsStillOrphan() {
        when(attachmentMapper.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectFinanceUser.getId()));
        when(queryRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(emptyList());
        assertFalse(rules.onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(attachmentResource, projectPartnerUser));
    }

    @Test
    public void thatUserCanNotDeleteAttachmentEvenIfUploaderOnceTheAttachmentIsNoLongerOrphan() {
        when(attachmentMapper.mapToDomain(attachmentResource)).thenReturn(asDomain(attachmentResource, projectPartnerUser.getId()));
        when(threadRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(mock(Thread.class)));
        when(queryRepository.findDistinctThreadByPostsAttachmentsId(attachmentResource.id)).thenReturn(singletonList(query()));
        assertFalse(rules.onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(attachmentResource, projectPartnerUser));
    }

    private QueryResource toResource(Query query) {
        return new QueryResource(query.id(), query.contextClassPk(), emptyList(),
                query.section(), query.title(), query.isAwaitingResponse(), query.createdOn(), null, null);
    }

    private Query query() {
        return new Query(92L, 1993L, "", null, null, "", ZonedDateTime.now());
    }

    private Attachment asDomain(AttachmentResource attachmentResource, Long uploaderId) {
        return new Attachment(attachmentResource.id, newUser().withId(uploaderId).build(),
                newFileEntry().withFilesizeBytes(attachmentResource.sizeInBytes).withMediaType(attachmentResource.mediaType).build(), null);
    }

}