package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.Section;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transactional service for application section statuses
 */
public interface SectionStatusService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getCompletedSections(final long applicationId, final long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Map<Long, Set<Long>>> getCompletedSections(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_SECTION_AS_COMPLETE')")
    ServiceResult<List<ValidationMessages>> markSectionAsComplete(Long sectionId,
                                                                  @P("applicationId") final Long id,
                                                                  Long markedAsCompleteById);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_SECTION_AS_NOT_REQUIRED')")
    ServiceResult<Void> markSectionAsNotRequired(Long sectionId, Long applicationId, Long markedAsNotRequiredById);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'MARK_SECTION_AS_INCOMPLETE')")
    ServiceResult<Void> markSectionAsInComplete(Long sectionId,
                                                @P("applicationId") final Long id,
                                                Long markedAsInCompleteById);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<Long>> getIncompleteSections(final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection);

}