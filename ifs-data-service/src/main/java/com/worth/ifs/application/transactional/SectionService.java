package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.security.NotSecured;

import java.util.List;
import java.util.Set;

/**
 * Transactional and secure service for Section processing work
 */
public interface SectionService {


    @NotSecured("TODO")
    Section getById(final Long sectionId);

    @NotSecured("TODO")
    Set<Long> getCompletedSections(final Long applicationId,
                                   final Long organisationId);

    @NotSecured("TODO")
    List<Long> getIncompleteSections(final Long applicationId);

    @NotSecured("TODO")
    Section findByName(final String name);

    /**
     * get questions for the sections and filter out the ones that have marked as completed turned on
     */
    @NotSecured("TODO")
    boolean isMainSectionComplete(Section section, Long applicationId, Long organisationId);

    @NotSecured("TODO")
    boolean childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection);

    @NotSecured("TODO")
    Section getNextSection(final Long sectionId);

    @NotSecured("TODO")
    Section getNextSection(Section section);

    @NotSecured("TODO")
    Section getPreviousSection(final Long sectionId);

    @NotSecured("TODO")
    Section getPreviousSection(Section section);

    @NotSecured("TODO")
    Section getSectionByQuestionId(final Long questionId);

}
