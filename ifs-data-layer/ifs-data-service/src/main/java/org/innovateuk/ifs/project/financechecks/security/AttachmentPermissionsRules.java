package org.innovateuk.ifs.project.financechecks.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.repository.MessageThreadRepository;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isExternalFinanceUser;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isProjectFinanceUser;

/*
  Provides the Permission Rules to manage Queries' Attachments under the context of a ProjectFinance.
 */
@Component
@PermissionRules
public class AttachmentPermissionsRules extends BasePermissionRules {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private QueryMapper queryMapper;

    @Autowired
    private MessageThreadRepository messageThreadRepository;

    @Autowired
    private ProjectFinanceQueryPermissionRules projectFinanceQueryPermissionRules;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @PermissionRule(value = "PF_ATTACHMENT_UPLOAD", description = "Project Finance can upload attachments.")
    public boolean projectFinanceCanUploadAttachments(final ProjectResource project, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_UPLOAD", description = "Competition Finance can upload attachments.")
    public boolean competitionFinanceCanUploadAttachments(final ProjectResource project, final UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(project.getId(), user.getId());
    }

    @PermissionRule(value = "PF_ATTACHMENT_UPLOAD", description = "Project partners can upload attachments.")
    public boolean projectPartnersCanUploadAttachments(final ProjectResource project, final UserResource user) {
        return isProjectPartner(user, project);
    }

    private boolean isProjectPartner(UserResource user, ProjectResource project) {
        return ofNullable(projectUserRepository.findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_PARTNER,  user.getId()))
                    .isPresent();
    }

    @PermissionRule(value = "PF_ATTACHMENT_READ", description = "Project Finance users can fetch any Attachment saved with a post, or any attachment they have uploaded that has yet to be saved with a post.")
    public boolean projectFinanceUsersCanFetchAnyAttachment(AttachmentResource attachment, UserResource user) {
        return attachmentIsStillOrphan(attachment) ? attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) : isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_READ", description = "Competition Finance users can fetch any Attachment saved with a post, or any attachment they have uploaded that has yet to be saved with a post.")
    public boolean competitionFinanceUsersCanFetchAnyAttachment(AttachmentResource attachment, UserResource user) {
        return attachmentIsStillOrphan(attachment) ? attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) : isExternalFinanceUser(user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_READ", description = "Finance Contact users can only fetch an Attachment saved with a post they are related to, or any attachment they have uploaded that has yet to be saved with a post.")
    public boolean financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(AttachmentResource attachment, UserResource user) {
        return attachmentIsStillOrphan(attachment) ? attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) : projectPartnerIsAllowedToFetchQueryAttachment(attachment, user);
    }

    private boolean userCanAccessQueryLinkedToTheAttachment(UserResource user, AttachmentResource attachment) {
        return findQueryTheAttachmentIsLinkedTo(attachment)
                .map(query -> projectFinanceQueryPermissionRules.projectFinanceUsersCanViewQueries(queryMapper.mapToResource(query), user) ||
                        projectFinanceQueryPermissionRules.projectPartnersCanViewQueries(queryMapper.mapToResource(query), user))
                .orElse(false);
    }

    @PermissionRule(value = "PF_ATTACHMENT_DOWNLOAD", description = "Project Finance users can download any Attachment saved with a post or any attachment they have uploaded that has yet to be saved with a post.")
    public boolean projectFinanceUsersCanDownloadAnyAttachment(AttachmentResource attachment, UserResource user) {
        return attachmentIsStillOrphan(attachment) ? attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) : isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_DOWNLOAD", description = "Finance Contact users can only download an Attachment saved with a post they are related to, or any attachment they have uploaded that has yet to be saved with a post.")
    public boolean financeContactUsersCanOnlyDownloadAnAttachmentIfRelatedToItsQuery(AttachmentResource attachment, UserResource user) {
        return attachmentIsStillOrphan(attachment) ? attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) : projectPartnerIsAllowedToFetchQueryAttachment(attachment, user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_DOWNLOAD", description = "Competition Contact users can only download an Attachment saved with a post they are related to, or any attachment they have uploaded that has yet to be saved with a post.")
    public boolean competitionFinanceUsersCanOnlyDownloadAnAttachmentIfRelatedToItsQuery(AttachmentResource attachment, UserResource user) {
        return attachmentIsStillOrphan(attachment) ? attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) : isExternalFinanceUser(user);
    }

    private boolean projectPartnerIsAllowedToFetchQueryAttachment(AttachmentResource attachmentResource, UserResource user) {
        return attachmentMapper.mapToDomain(attachmentResource).wasUploadedBy(user.getId())
                || userCanAccessQueryLinkedToTheAttachment(user, attachmentResource);
    }

    @PermissionRule(value = "PF_ATTACHMENT_DELETE", description = "Project Finance and Finance Contact users can delete " +
            "any Attachment they have uploaded if it is still an orphan.")
    public boolean onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(AttachmentResource attachment, UserResource user) {
        return attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) && attachmentIsStillOrphan(attachment);
    }

    private boolean attachmentIsStillOrphan(AttachmentResource attachment) {
        return messageThreadRepository.findDistinctThreadByPostsAttachmentsId(attachment.id).isEmpty();
    }

    private Optional<Query> findQueryTheAttachmentIsLinkedTo(AttachmentResource attachment) {
        return queryRepository.findDistinctThreadByPostsAttachmentsId(attachment.id).stream().findFirst();
    }
}