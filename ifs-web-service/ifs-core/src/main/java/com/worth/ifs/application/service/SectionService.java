package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link Section} related data.
 */
public interface SectionService {
    SectionResource getById(Long sectionId);
    List<Long> getCompleted(Long applicationId, Long organisationId);
    List<Long> getInCompleted(Long applicationId);
    Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId);
    Boolean allSectionsMarkedAsComplete(Long applicationId);
    List<SectionResource> filterParentSections(List<Long> sections);
    void removeSectionsQuestionsWithType(SectionResource section, String name);
    Future<SectionResource> getPreviousSection(Optional<SectionResource> sectionId);
    Future<SectionResource> getNextSection(Optional<SectionResource> sectionId);
    SectionResource getSectionByQuestionId(Long questionId);
    Set<Long> getQuestionsForSectionAndSubsections(Long sectionId);
	SectionResource getFinanceSectionForCompetition(Long competitionId);
}
