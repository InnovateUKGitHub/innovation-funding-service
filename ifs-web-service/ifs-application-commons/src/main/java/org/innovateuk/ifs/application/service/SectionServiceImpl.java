package org.innovateuk.ifs.application.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.SectionType.ORGANISATION_FINANCES;
import static org.innovateuk.ifs.form.resource.SectionType.TERMS_AND_CONDITIONS;
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
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Override
    public ValidationMessages markAsComplete(long sectionId, long applicationId, long markedAsCompleteById) {
        LOG.debug(String.format("mark section as complete %s / %s /%s ", sectionId, applicationId, markedAsCompleteById));
        return sectionStatusRestService.markAsComplete(sectionId, applicationId, markedAsCompleteById).getSuccess();
    }

    @Override
    public void markAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        LOG.debug(String.format("mark section as not required %s / %s /%s ", sectionId, applicationId, markedAsCompleteById));
        sectionStatusRestService.markAsNotRequired(sectionId, applicationId, markedAsCompleteById).getSuccess();
    }

    @Override
    public void markAsInComplete(Long sectionId, Long applicationId, Long markedAsInCompleteById) {
        LOG.debug(String.format("mark section as incomplete %s / %s /%s ", sectionId, applicationId, markedAsInCompleteById));
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, markedAsInCompleteById);
    }

    @Override
    public SectionResource getById(Long sectionId) {
        return sectionRestService.getById(sectionId).getSuccess();
    }

    @Override
    public List<Long> getCompleted(Long applicationId, Long organisationId) {
        return sectionStatusRestService.getCompletedSectionIds(applicationId, organisationId).getSuccess();
    }

    @Override
    public Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId) {
        return sectionStatusRestService.getCompletedSectionsByOrganisation(applicationId).getSuccess();
    }

    @Override
    public Boolean allSectionsMarkedAsComplete(Long applicationId) {
        return sectionStatusRestService.allSectionsMarkedAsComplete(applicationId).getSuccess();
    }

    /**
     * Get Sections that have no parent section.
     *
     * @param sections
     * @return the list of sections without a parent section.
     */
    @Override
    public List<SectionResource> filterParentSections(List<SectionResource> sections) {
        if (sections == null) {
            return new ArrayList<>();
        }

        List<SectionResource> childSections = getChildSections(sections, new ArrayList<>());

        return sections.stream()
                .filter(s -> childSections.stream()
                        .noneMatch(c -> c.getId().equals(s.getId())))
                .sorted(Comparator.comparing(SectionResource::getPriority))
                .collect(toList());
    }

    @Override
    public List<SectionResource> getAllByCompetitionId(final long competitionId) {
        return sectionRestService.getByCompetition(competitionId).getSuccess();
    }

    private List<SectionResource> getChildSections(List<SectionResource> sections, List<SectionResource> children) {
        if (sections != null && !sections.isEmpty()) {
            List<SectionResource> allSections = this.getAllByCompetitionId(sections.get(0).getCompetition());
            getChildSectionsFromList(sections, children, allSections);
        }
        return children;
    }

    private List<SectionResource> getChildSectionsFromList(List<SectionResource> sections, List<SectionResource> children, final List<SectionResource> all) {
        sections.stream().filter(section -> section.getChildSections() != null).forEach(section -> {
            List<SectionResource> childSections = findResourceByIdInList(section.getChildSections(), all);
            children.addAll(childSections);
            getChildSections(childSections, children);
        });
        return children;
    }

    public List<SectionResource> findResourceByIdInList(final List<Long> ids, final List<SectionResource> list) {
        return simpleFilter(list, item -> ids.contains(item.getId()));
    }

    @Override
    public void removeSectionsQuestionsWithType(SectionResource section, FormInputType type) {
        List<QuestionResource> questions = questionRestService.findByCompetition(section.getCompetition()).getSuccess();
        List<SectionResource> sections = this.getAllByCompetitionId(section.getCompetition());
        List<FormInputResource> formInputResources = formInputRestService.getByCompetitionIdAndScope(section.getCompetition(), APPLICATION).getSuccess();
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

    private List<SectionResource> filterByIdList(final List<Long> ids, final List<SectionResource> list) {
        return simpleFilter(list, section -> ids.contains(section.getId()));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    @Override
    public SectionResource getSectionByQuestionId(long questionId) {
        return sectionRestService.getSectionByQuestionId(questionId).getSuccess();
    }

    @Override
    public Set<Long> getQuestionsForSectionAndSubsections(long sectionId) {
        return sectionRestService.getQuestionsForSectionAndSubsections(sectionId).getSuccess();
    }

    @Override
    public List<SectionResource> getSectionsForCompetitionByType(long competitionId, SectionType type) {
        return sectionRestService.getSectionsByCompetitionIdAndType(competitionId, type).getSuccess();
    }

    @Override
    public SectionResource getFinanceSection(long competitionId) {
        return getSingleSectionByType(competitionId, SectionType.FINANCE);
    }

    @Override
    public SectionResource getTermsAndConditionsSection(long competitionId) {
        return getSingleSectionByType(competitionId, TERMS_AND_CONDITIONS);
    }

    @Override
    public SectionResource getOrganisationFinanceSection(long competitionId) {
        return getSingleSectionByType(competitionId, ORGANISATION_FINANCES);
    }

    private SectionResource getSingleSectionByType(long competitionId, SectionType type) {
        List<SectionResource> sections = getSectionsForCompetitionByType(competitionId, type);
        if (sections.isEmpty()) {
            LOG.error("no " + type + " section found for competition " + competitionId);
            return null;
        }
        if (sections.size() > 1) {
            LOG.warn("multiple " + type + " sections found for competition " + competitionId);
        }
        return sections.get(0);
    }
}
