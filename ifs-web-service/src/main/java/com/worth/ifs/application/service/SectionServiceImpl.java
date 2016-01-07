package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link Section} related data,
 * through the RestService {@link SectionRestService}.
 */
@Service
public class SectionServiceImpl implements SectionService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    SectionRestService sectionRestService;

    @Override
    public Section getById(Long sectionId) {
        return sectionRestService.getById(sectionId);
    }

    @Override
    public List<Long> getInCompleted(Long applicationId) {
        return sectionRestService.getIncompletedSectionIds(applicationId);
    }

    @Override
    public List<Long> getCompleted(Long applicationId, Long organisationId) {
        return sectionRestService.getCompletedSectionIds(applicationId, organisationId);
    }

    @Override
    public List<Section> getParentSections(List<Section> sections) {
        List<Section> childSections = new ArrayList<>();
        getChildSections(sections, childSections);
        sections = sections.stream()
                .filter(s -> !childSections.stream()
                        .anyMatch(c -> c.getId().equals(s.getId())))
                .collect(Collectors.toList());
        sections.stream()
                .filter(s -> s.getChildSections()!=null);
        return sections;
    }

    private List<Section> getChildSections(List<Section> sections, List<Section>children) {
        for(Section section : sections) {
            if(section.getChildSections()!=null) {
                children.addAll(section.getChildSections());
                getChildSections(section.getChildSections(), children);
            }
        }
        return children;
    }

    @Override
    public Section getByName(String name) {
        return sectionRestService.getSection(name);
    }

    public void removeSectionsQuestionsWithType(Section section, String name) {
        section.getChildSections().stream().
                forEach(s -> s.setQuestions(s.getQuestions().stream().
                        filter(q -> q != null && !q.getFormInputs().stream().anyMatch(input -> input.getFormInputType().getTitle().equals(name))).
                        collect(Collectors.toList())));
    }

    @Override
    public List<Long> getUserAssignedSections(List<Section> sections, HashMap<Long, QuestionStatus> questionAssignees, Long userId ) {
        List<Long> userAssignedSections = new ArrayList<>();

        for(Section section : sections) {
            boolean isUserAssignedSection = section.getQuestions().stream().anyMatch(q ->
                    (questionAssignees.get(q.getId())!=null &&
                            questionAssignees.get(q.getId()).getAssignee().getUser().getId().equals(userId)));
            if(isUserAssignedSection) {
                userAssignedSections.add(section.getId());
            }
        }
        return userAssignedSections;
    }

    @Override
    public Section getPreviousSection(Optional<Long> sectionId) {
        if(sectionId!=null && sectionId.isPresent()) {
            return sectionRestService.getPreviousSection(sectionId.get());
        }
        return null;
    }

    @Override
    public Section getNextSection(Optional<Long> sectionId) {
        if(sectionId!=null && sectionId.isPresent()) {
            Section nextSection = sectionRestService.getNextSection(sectionId.get());
            return nextSection;
        }
        return null;
    }

    @Override
    public Section getSectionByQuestionId(Long questionId) {
        return sectionRestService.getSectionByQuestionId(questionId);
    }
}
