package org.innovateuk.ifs.project.financechecks.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

/*
  Provides the Permission Rules to manage Queries' Attachments under the context of a ProjectFinance.
 */
@Component
@PermissionRules
public class AttachmentPermissionsRules {
    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private QueryMapper queryMapper;

    @Autowired
    private ProjectFinanceQueryPermissionRules projectFinanceQueryPermissionRules;

    @Autowired
    private ProjectUserRepository projectUserRepository;


    @PermissionRule(value = "PF_ATTACHMENT_UPLOAD", description = "Project Finance can upload attachments.")
    public boolean projectFinanceCanUploadAttachments(final ProjectResource project, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_UPLOAD", description = "Project partners can upload attachments.")
    public boolean projectPartnersCanUploadAttachments(final ProjectResource project, final UserResource user) {
        return isProjectPartner(user, project);
    }

    private boolean isProjectPartner(UserResource user, ProjectResource project) {
        return ofNullable(projectUserRepository.findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_PARTNER,  user.getId()))
                    .isPresent();
    }

    @PermissionRule(value = "PF_ATTACHMENT_READ", description = "Project Finance users can fetch any Attachment.")
    public boolean projectFinanceUsersCanFetchAnyAttachment(AttachmentResource attachment, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_READ", description = "Finance Contact users can only fetch an Attachment they are related to.")
    public boolean financeContactUsersCanOnlyFetchAnAttachmentIfUploaderOrIfRelatedToItsQuery(AttachmentResource attachment, UserResource user) {
        return projectPartnerIsAllowedToFetchAttachment(attachment, user);
    }

    private boolean userCanAccessQueryLinkedToTheAttachment(UserResource user, AttachmentResource attachment) {
        return findQueryTheAttachmentIsLinkedTo(attachment)
                .map(query -> projectFinanceQueryPermissionRules.projectFinanceUsersCanViewQueries(queryMapper.mapToResource(query), user) ||
                        projectFinanceQueryPermissionRules.projectPartnersCanViewQueries(queryMapper.mapToResource(query), user))
                .orElse(false);
    }

    @PermissionRule(value = "PF_ATTACHMENT_DOWNLOAD", description = "Project Finance users can download any Attachment.")
    public boolean projectFinanceUsersCanDownloadAnyAttachment(AttachmentResource attachment, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_ATTACHMENT_DOWNLOAD", description = "Finance Contact users can only download an Attachment they are related to.")
    public boolean financeContactUsersCanOnlyDownloadAnAttachmentIfRelatedToItsQuery(AttachmentResource attachment, UserResource user) {
        return projectPartnerIsAllowedToFetchAttachment(attachment, user);
    }

    private boolean projectPartnerIsAllowedToFetchAttachment(AttachmentResource attachmentResource, UserResource user) {
        return attachmentMapper.mapToDomain(attachmentResource).wasUploadedBy(user.getId())
                || userCanAccessQueryLinkedToTheAttachment(user, attachmentResource);
    }

    @PermissionRule(value = "PF_ATTACHMENT_DELETE", description = "Project Finance and Finance Contact users can delete " +
            "any of their Attachment they have uploaded if still orphan.")
    public boolean onlyTheUploaderOfAnAttachmentCanDeleteItIfStillOrphan(AttachmentResource attachment, UserResource user) {
        return attachmentMapper.mapToDomain(attachment).wasUploadedBy(user.getId()) && attachmentIsStillOrphan(attachment);
    }

    private boolean attachmentIsStillOrphan(AttachmentResource attachment) {
        return !findQueryTheAttachmentIsLinkedTo(attachment).isPresent();
    }

    private Optional<Query> findQueryTheAttachmentIsLinkedTo(AttachmentResource attachment) {
        return queryRepository.findDistinctThreadByPostsAttachmentsId(attachment.id).stream().findFirst();
    }

}