package com.worth.ifs.application.transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Section processing work
 */
public interface SectionService {

    @PreAuthorize("hasPermission(#sectionId, 'com.worth.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getById(final Long sectionId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Map<Long, Set<Long>>> getCompletedSections(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')"
            + " && hasPermission(#organisationId, 'com.worth.ifs.user.resource.OrganisationResource', 'READ')")
    ServiceResult<Set<Long>> getCompletedSections(final Long applicationId,
                                   final Long organisationId);

    @PreAuthorize("hasPermission(#sectionId, 'com.worth.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<Set<Long>> getQuestionsForSectionAndSubsections(final Long sectionId);

    @NotSecured("TODO")
    ServiceResult<List<ValidationMessages>> markSectionAsComplete(Long sectionId,
                                                                  Long applicationId,
                                                                  Long markedAsCompleteById);

    @NotSecured("TODO")
    ServiceResult<Void> markSectionAsInComplete(Long sectionId,
                                                Long applicationId,
                                                Long markedAsInCompleteById);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<Long>> getIncompleteSections(final Long applicationId);

    @PostAuthorize("hasPermission(filterObject, 'READ')")
	ServiceResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type);
    
    /**
     * get questions for the sections and filter out the ones that have marked as completed turned on
     */
    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')"
            + " && hasPermission(#organisationId, 'com.worth.ifs.user.resource.OrganisationResource', 'READ')")
    ServiceResult<Boolean> isMainSectionComplete(Section section, Long applicationId, Long organisationId, boolean ignoreOtherOrganisations);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection);

    @PreAuthorize("hasPermission(#sectionId, 'com.worth.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getNextSection(final Long sectionId);

    @PreAuthorize("hasPermission(#section, 'READ')")
    ServiceResult<SectionResource> getNextSection(SectionResource section);

    @PreAuthorize("hasPermission(#sectionId, 'com.worth.ifs.application.resource.SectionResource', 'READ')")
    ServiceResult<SectionResource> getPreviousSection(final Long sectionId);

    @PreAuthorize("hasPermission(#section, 'READ')")
    ServiceResult<SectionResource> getPreviousSection(SectionResource section);

    @PreAuthorize("hasPermission(#questionId, 'com.worth.ifs.application.resource.QuestionResource', 'READ')")
    ServiceResult<SectionResource> getSectionByQuestionId(final Long questionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<SectionResource>> getByCompetionId(final Long CompetitionId);

}
