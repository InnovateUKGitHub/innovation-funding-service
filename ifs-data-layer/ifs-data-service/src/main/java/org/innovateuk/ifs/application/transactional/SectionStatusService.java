package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;
import java.util.Set;

/**
 * Transactional service for application section statuses
 */
public interface SectionStatusService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getCompletedSections(final long applicationId, final long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Map<Long, Set<Long>>> getCompletedSections(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_SECTION')")
    ServiceResult<ValidationMessages> markSectionAsComplete(long sectionId, long applicationId, long markedAsCompleteById);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_SECTION')")
    ServiceResult<Void> markSectionAsNotRequired(long sectionId, long applicationId, long markedAsNotRequiredById);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_SECTION')")
    ServiceResult<Void> markSectionAsInComplete(long sectionId, long applicationId, long markedAsInCompleteById);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> sectionsCompleteForAllOrganisations(long applicationId);

}