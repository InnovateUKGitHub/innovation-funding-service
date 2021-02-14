package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Interface for public content actions.
 */
public interface PublicContentService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "GET_PUBLIC_CONTENT",
            description = "All internal users can get the public content for a competition.")
    ServiceResult<PublicContentResource> findByCompetitionId(long id);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "INITIALISE_PUBLIC_CONTENT",
            description = "The Competition Admin, or project finance user can initialise the public content for a competition.")
    ServiceResult<Void> initialiseByCompetitionId(long id);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "PUBLISH_PUBLIC_CONTENT",
            description = "The Competition Admin, or project finance user can publish the public content for a competition.")
    ServiceResult<Void> publishByCompetitionId(long id);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "UPDATE_SECTION",
            description = "The Competition Admin, or project finance user can save the public content section for a competition.")
    ServiceResult<Void> updateSection(PublicContentResource resource, PublicContentSectionType section);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "MARK_SECTION_AS_COMPLETE",
            description = "The Competition Admin, or project finance user can mark the public content section as complete for a competition.")
    ServiceResult<Void> markSectionAsComplete(PublicContentResource resource, PublicContentSectionType section);
}
