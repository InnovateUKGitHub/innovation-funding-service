package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transactional and secure service for Section processing work
 */
public interface SectionService {

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getById(final Long sectionId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Map<Long, Set<Long>>> getCompletedSections(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getCompletedSections(final long applicationId, final long organisationId);

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<Set<Long>> getQuestionsForSectionAndSubsections(final Long sectionId);

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

    @PostAuthorize("hasPermission(filterObject, 'READ')")
	ServiceResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection);

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getNextSection(final Long sectionId);

    @PreAuthorize("hasPermission(#section, 'READ')")
    ServiceResult<SectionResource> getNextSection(SectionResource section);

    @PreAuthorize("hasPermission(#sectionId, 'org.innovateuk.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getPreviousSection(final Long sectionId);

    @PreAuthorize("hasPermission(#section, 'READ')")
    ServiceResult<SectionResource> getPreviousSection(SectionResource section);

    @PreAuthorize("hasPermission(#questionId, 'org.innovateuk.ifs.application.resource.QuestionResource', 'READ')")
    ServiceResult<SectionResource> getSectionByQuestionId(final Long questionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<SectionResource>> getByCompetitionId(final Long CompetitionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(final Long competitionId);

}
