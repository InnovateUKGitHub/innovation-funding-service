package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link Section} related data.
 */
public interface SectionService {
    public Section getById(Long sectionId);
    public List<Long> getCompleted(Long applicationId, Long organisationId);
    public List<Long> getInCompleted(Long applicationId);
    public List<Section> getParentSections(List<Section> sections);
    public Section getByName(String name);
    public void removeSectionsQuestionsWithType(Section section, String name);
    public List<Long> getUserAssignedSections(List<Section> sections, HashMap<Long, QuestionStatus> questionAssignees, Long currentProcessRoleId);
    public Section getPreviousSection(Optional<Long> sectionId);
    public Section getNextSection(Optional<Long> sectionId);
    public Section getSectionByQuestionId(Long questionId);
}
