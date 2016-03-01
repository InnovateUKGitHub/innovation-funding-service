package com.worth.ifs.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;

/**
 * Interface for CRUD operations on {@link Section} related data.
 */
public interface SectionService {
    SectionResource getById(Long sectionId);
    List<Long> getCompleted(Long applicationId, Long organisationId);
    List<Long> getInCompleted(Long applicationId);
    Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId);
    Boolean allSectionsMarkedAsComplete(Long applicationId);
    List<SectionResource> getParentSections(List<Long> sections);
    SectionResource getByName(String name);
    void removeSectionsQuestionsWithType(SectionResource section, String name);
    List<Long> getUserAssignedSections(List<SectionResource> sections, HashMap<Long, QuestionStatus> questionAssignees, Long currentProcessRoleId);
    Future<SectionResource> getPreviousSection(Optional<SectionResource> sectionId);
    Future<SectionResource> getNextSection(Optional<SectionResource> sectionId);
    SectionResource getSectionByQuestionId(Long questionId);
}
