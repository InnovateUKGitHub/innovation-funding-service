package org.innovateuk.ifs.application.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * This class contains methods to retrieve and store {@link SectionResource} related data,
 * through the RestService {@link SectionRestService}.
 */
@Service
public class SectionServiceImpl implements SectionService {
    private static final Log LOG = LogFactory.getLog(SectionServiceImpl.class);

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private QuestionService questionService;

    @Override
    public List<ValidationMessages> markAsComplete(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        LOG.debug(String.format("mark section as complete %s / %s /%s ", sectionId, applicationId, markedAsCompleteById));
        return sectionRestService.markAsComplete(sectionId, applicationId, markedAsCompleteById).getSuccessObjectOrThrowException();
    }

    @Override
    public void markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        LOG.debug(String.format("mark section as not required %s / %s /%s ", sectionId, applicationId, markedAsCompleteById));
        sectionRestService.markAsNotRequired(sectionId, applicationId, markedAsCompleteById).getSuccessObjectOrThrowException();
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
        List<SectionResource> childSections = getChildSections(sections, new ArrayList<>());
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
        if(sections!= null && sections.size()>0) {
            List<SectionResource> allSections = this.getAllByCompetitionId(sections.get(0).getCompetition());
            getChildSectionsFromList(sections, children, allSections);
        }
        return children;
    }

    private List<SectionResource> getChildSectionsFromList(List<SectionResource> sections, List<SectionResource>children, final List<SectionResource> all) {
        sections.stream().filter(section -> section.getChildSections() != null).forEach(section -> {
            List<SectionResource> childSections = findResourceByIdInList(section.getChildSections(), all);
            children.addAll(childSections);
            getChildSections(childSections, children);
        });
        return children;
    }

    public List<SectionResource> findResourceByIdInList(final List<Long> ids, final List<SectionResource> list){
        return simpleFilter(list, item -> ids.contains(item.getId()));
    }

    @Override
    public void removeSectionsQuestionsWithType(SectionResource section, FormInputType type) {
        List<QuestionResource> questions = questionService.findByCompetition(section.getCompetition());
        List<SectionResource> sections = this.getAllByCompetitionId(section.getCompetition());
        List<FormInputResource> formInputResources = formInputRestService.getByCompetitionIdAndScope(section.getCompetition(), APPLICATION).getSuccessObjectOrThrowException();
        filterByIdList(section.getChildSections(), sections).stream()
                .forEach(
                s -> s.setQuestions(
                        getQuestionsBySection(s.getQuestions(), questions)
                        .stream()
                        .filter(
                                q -> q != null &&
                                !q.getFormInputs().stream()
                                    .anyMatch(
                                        input -> type == simpleFilter(formInputResources, i -> input.equals(i.getId())).get(0).getType()
                                    )
                        )
                        .map(QuestionResource::getId)
                        .collect(toList())
                )
        );
    }

    private List<SectionResource> filterByIdList(final List<Long> ids, final List<SectionResource> list){
        return simpleFilter(list, section -> ids.contains(section.getId()));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
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
    
    @Override
	public SectionResource getFinanceSection(Long competitionId) {
    	return getSingleSectionByType(competitionId, SectionType.FINANCE);

	}
    
    @Override
	public SectionResource getOrganisationFinanceSection(Long competitionId) {
    	return getSingleSectionByType(competitionId, SectionType.ORGANISATION_FINANCES);
	}
    
	private SectionResource getSingleSectionByType(Long competitionId, SectionType type) {
    	List<SectionResource> sections = getSectionsForCompetitionByType(competitionId, type);
    	if(sections.isEmpty()) {
    		LOG.error("no " + type + " section found for competition " + competitionId);
    		return null;
    	}
    	if(sections.size() > 1) {
    		LOG.warn("multiple " + type + " sections found for competition " + competitionId);
    	}
    	return sections.get(0);
	}

}
