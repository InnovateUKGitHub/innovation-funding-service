package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions for public content.
 */
@Component
@PermissionRules
public class PublicContentItemPermissionRules extends BasePermissionRules {

    @Autowired
    private PublicContentRepository contentRepository;

    @PermissionRule(value = "READ_PUBLISHED", description = "All users can access published competitions public content")
    public boolean allUsersCanViewPublishedContent(Long competitionId, final UserResource user) {
        PublicContent content = contentRepository.findByCompetitionId(competitionId);
        if (content != null) {
            return isPublished(content);
        }
        return false;
    }

    private boolean isPublished(PublicContent content) {
        return content.getPublishDate() != null;
    }
}
