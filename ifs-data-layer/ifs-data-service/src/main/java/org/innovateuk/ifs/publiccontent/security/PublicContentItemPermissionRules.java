package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provides the permissions for public content.
 */
@Component
@PermissionRules
public class PublicContentItemPermissionRules extends BasePermissionRules {

    @Autowired
    private PublicContentRepository contentRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @PermissionRule(value = "READ_PUBLISHED", description = "All users can access published competitions public content")
    public boolean allUsersCanViewPublishedContent(CompetitionCompositeId competitionCompositeId, final UserResource user) {
        PublicContent content = contentRepository.findByCompetitionId(competitionCompositeId.id());
        Optional<Competition> competition = competitionRepository.findById(competitionCompositeId.id());

        if (content != null) {
            if (competitionIsAssessmentOnlyAndHasNoPublishedContent(content, competition)) {
                return true;
            }
            return isPublished(content);
        }
        return false;
    }

    private boolean isPublished(PublicContent content) {
        return content.getPublishDate() != null;
    }

    private boolean competitionIsAssessmentOnlyAndHasNoPublishedContent(PublicContent content, Optional<Competition> competition) {
        return competition.filter(value -> value.isAssessmentOnly() && content.getPublishDate() == null).isPresent();
    }
}
