package com.worth.ifs.application.service;

import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.domain.SectionType;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.form.service.FormInputService;

/**
 * This class contains methods to retrieve and store {@link Section} related data,
 * through the RestService {@link SectionRestService}.
 */
// TODO DW - INFUND-1555 - return RestResults
@Service
public class SectionServiceImpl implements SectionService {
    private static final Log LOG = LogFactory.getLog(SectionServiceImpl.class);

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private QuestionService questionService;

    @Override
    public List<ValidationMessages> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        LOG.debug(String.format("mark section as complete %s / %s /%s ", sectionId, applicationId, markedAsCompleteById));
        return sectionRestService.markAsComplete(sectionId, applicationId, markedAsCompleteById).getSuccessObject();
    }

    @Override
    public void markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById) {
        LOG.debug(String.format("mark section as incomplete %s / %s /%s ", sectionId, applicationId, markedAsInCompleteById));
        sectionRestService.markAsInComplete(sectionId, applicationId, markedAsInCompleteById);
    }

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
     * @param sections
     * @return the list of sections without a parent section.
     */
    @Override
    public List<SectionResource> filterParentSections(List<SectionResource> sections) {
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

    @Override
    public List<SectionResource> getAllByCompetitionId(final Long competitionId) {
        return sectionRestService.getByCompetition(competitionId).getSuccessObjectOrThrowException();
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
        List<QuestionResource> questions = questionService.findByCompetition(section.getCompetition());
        section.getChildSections().stream()
                .map(sectionRestService::getById)
                .map(result -> result.getSuccessObject())
                .forEach(
                s -> s.setQuestions(
                        getQuestionsBySection(s.getQuestions(), questions)
                        .stream()
                        .filter(
                                q -> q != null &&
                                !q.getFormInputs().stream()
                                    .anyMatch(
                                        input -> formInputService.getOne(input).getFormInputTypeTitle().equals(name)
                                    )
                        )
                        .map(QuestionResource::getId)
                        .collect(toList())
                )
        );
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
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
	public List<SectionResource> getSectionsForCompetitionByType(Long competitionId, SectionType type) {
		return sectionRestService.getSectionsByCompetitionIdAndType(competitionId, type).getSuccessObjectOrThrowException();
	}

}
