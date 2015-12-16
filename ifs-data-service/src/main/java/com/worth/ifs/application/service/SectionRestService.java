package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Section} related data.
 */
public interface SectionRestService {
    public Section getById(Long sectionId);
    public List<Long> getCompletedSectionIds(Long applicationId, Long organisationId);
    public List<Long> getIncompletedSectionIds(Long applicationId);
    public Section getSection(String name);
    public Section getPreviousSection(Long sectionId);
    public Section getNextSection(Long sectionId);
    public Section getSectionByQuestionId(Long questionId);
}
