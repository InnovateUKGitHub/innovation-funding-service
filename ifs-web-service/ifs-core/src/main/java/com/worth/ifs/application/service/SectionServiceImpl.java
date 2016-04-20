package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;

import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.stream.Collectors.toList;

/**
 * This class contains methods to retrieve and store {@link Section} related data,
 * through the RestService {@link SectionRestService}.
 */
// TODO DW - INFUND-1555 - return RestResults
@Service
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private QuestionService questionService;

    @Override
    public SectionResource getById(Long sectionId) {
        return sectionRestService.getById(sectionId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<Long> getInCompleted(Long applicationId) {
        return sectionRestService.getIncompletedSectionIds(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<Long> getCompleted(Long applicationId, Long organisationId) {
        return sectionRestService.getCompletedSectionIds(applicationId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId) {
        return sectionRestService.getCompletedSectionsByOrganisation(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Boolean allSectionsMarkedAsComplete(Long applicationId) {
        return sectionRestService.allSectionsMarkedAsComplete(applicationId).getSuccessObjectOrThrowException();
    }

    /**
     * Get Sections that have no parent section.
     * @param sectionIds
     * @return the list of sections without a parent section.
     */
    @Override
    public List<SectionResource> filterParentSections(List<Long> sectionIds) {
        List<SectionResource> sections = simpleMap(sectionIds, this::getById);
        List<SectionResource> childSections = new ArrayList<>();
        getChildSections(sections, childSections);
        sections = sections.stream()
                .filter(s -> !childSections.stream()
                        .anyMatch(c -> c.getId().equals(s.getId())))
                .collect(toList());
        sections.stream()
                .filter(s -> s.getChildSections()!=null);
        return sections;
    }

    private List<SectionResource> getChildSections(List<SectionResource> sections, List<SectionResource>children) {
        sections.stream().filter(section -> section.getChildSections() != null).forEach(section -> {
            List<SectionResource> childSections = simpleMap(section.getChildSections(), sectionId -> sectionRestService.getById(sectionId).getSuccessObject());
            children.addAll(childSections);
            getChildSections(childSections, children);
        });
        return children;
    }

    @Override
    public void removeSectionsQuestionsWithType(SectionResource section, String name) {
        section.getChildSections().stream()
                .map(sectionRestService::getById)
                .map(result -> result.getSuccessObject())
                .forEach(
                s -> s.setQuestions(
                        s.getQuestions()
                                .stream()
                                .map(questionService::getById)
                                .filter(
                                        q -> q != null &&
                                                !q.getFormInputs().stream()
                                                .anyMatch(
                                                        input -> input.getFormInputType().getTitle().equals(name)
                                                )
                                )
                                .map(Question::getId)
                                .collect(toList())
                )
        );
    }

    @Override
    public Future<SectionResource> getPreviousSection(Optional<SectionResource> section) {
        if(section!=null && section.isPresent()) {
            return adapt(sectionRestService.getPreviousSection(section.get().getId()), RestResult::getSuccessObjectOrNull);
        }
        return null;
    }

    @Override
    public Future<SectionResource> getNextSection(Optional<SectionResource> section) {
        if(section!=null && section.isPresent()) {
            return adapt(sectionRestService.getNextSection(section.get().getId()), RestResult::getSuccessObjectOrThrowException);
        }
        return null;
    }

    @Override
    public SectionResource getSectionByQuestionId(Long questionId) {
        return sectionRestService.getSectionByQuestionId(questionId).getSuccessObjectOrThrowException();
    }

    @Override
    public Set<Long> getQuestionsForSectionAndSubsections(Long sectionId) {
        return sectionRestService.getQuestionsForSectionAndSubsections(sectionId).getSuccessObjectOrThrowException();
    }

	@Override
	public SectionResource getFinanceSectionForCompetition(Long competitionId) {
		return sectionRestService.getFinanceSectionForCompetition(competitionId).getSuccessObjectOrThrowException();
	}
}
