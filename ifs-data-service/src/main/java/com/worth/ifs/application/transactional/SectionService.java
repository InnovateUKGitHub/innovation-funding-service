package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transactional and secure service for Section processing work
 */
public interface SectionService {


    @NotSecured("TODO")
    ServiceResult<Section> getById(final Long sectionId);

    @NotSecured("TODO")
    ServiceResult<Map<Long, Set<Long>>> getCompletedSections(Long applicationId);

    @NotSecured("TODO")
    ServiceResult<Set<Long>> getCompletedSections(final Long applicationId,
                                   final Long organisationId);

    @NotSecured("TODO")
    ServiceResult<List<Long>> getIncompleteSections(final Long applicationId);

    @NotSecured("TODO")
    ServiceResult<Section> findByName(final String name);

    /**
     * get questions for the sections and filter out the ones that have marked as completed turned on
     */
    @NotSecured("TODO")
    ServiceResult<Boolean> isMainSectionComplete(Section section, Long applicationId, Long organisationId, boolean ignoreOtherOrganisations);

    @NotSecured("TODO")
    ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection);

    @NotSecured("TODO")
    ServiceResult<Section> getNextSection(final Long sectionId);

    @NotSecured("TODO")
    ServiceResult<Section> getNextSection(Section section);

    @NotSecured("TODO")
    ServiceResult<Section> getPreviousSection(final Long sectionId);

    @NotSecured("TODO")
    ServiceResult<Section> getPreviousSection(Section section);

    @NotSecured("TODO")
    ServiceResult<Section> getSectionByQuestionId(final Long questionId);

}
