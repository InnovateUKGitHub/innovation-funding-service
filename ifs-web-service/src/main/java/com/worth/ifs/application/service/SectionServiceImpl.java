package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link Section} related data,
 * through the RestService {@link SectionRestService}.
 */
// TODO DW - INFUND-1555 - return RestResults
@Service
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionRestService sectionRestService;

    @Override
    public Section getById(Long sectionId) {
        return sectionRestService.getById(sectionId).getSuccessObjectOrNull();
    }

    @Override
    public List<Long> getInCompleted(Long applicationId) {
        return sectionRestService.getIncompletedSectionIds(applicationId).getSuccessObjectOrNull();
    }

    @Override
    public List<Long> getCompleted(Long applicationId, Long organisationId) {
        return sectionRestService.getCompletedSectionIds(applicationId, organisationId).getSuccessObjectOrNull();
    }

    @Override
    public Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId) {
        return sectionRestService.getCompletedSectionsByOrganisation(applicationId).getSuccessObjectOrNull();
    }

    @Override
    public Boolean allSectionsMarkedAsComplete(Long applicationId) {
        return sectionRestService.allSectionsMarkedAsComplete(applicationId).getSuccessObjectOrNull();
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
        sections.stream().filter(section -> section.getChildSections() != null).forEach(section -> {
            children.addAll(section.getChildSections());
            getChildSections(section.getChildSections(), children);
        });
        return children;
    }

    @Override
    public Section getByName(String name) {
        return sectionRestService.getSection(name).getSuccessObjectOrNull();
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
                questionAssignees.get(q.getId())!=null &&
                questionAssignees.get(q.getId()).getAssignee().getUser().getId().equals(userId)
            );
            if(isUserAssignedSection) {
                userAssignedSections.add(section.getId());
            }
        }
        return userAssignedSections;
    }

    @Override
    public Section getPreviousSection(Optional<Section> section) {
        if(section!=null && section.isPresent()) {
            return sectionRestService.getPreviousSection(section.get().getId()).getSuccessObjectOrNull();
        }
        return null;
    }

    @Override
    public Section getNextSection(Optional<Section> section) {
        if(section!=null && section.isPresent()) {
            return sectionRestService.getNextSection(section.get().getId()).getSuccessObjectOrNull();
        }
        return null;
    }

    @Override
    public Section getSectionByQuestionId(Long questionId) {
        return sectionRestService.getSectionByQuestionId(questionId).getSuccessObjectOrNull();
    }
}
