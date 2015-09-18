package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Section;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SectionService {
    public List<Long> getCompleted(Long applicationId, Long organisationId);
    public List<Long> getInCompleted(Long applicationId);
    public List<Section> getParentSections(List<Section> sections);
    public Section getByName(String name);
    public void removeSectionsQuestionsWithType(Section section, String name);
}
