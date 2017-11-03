package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.QuestionSetupRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupQuestionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.form.LandingPageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

@Service
public class CompetitionSetupQuestionServiceImpl implements CompetitionSetupQuestionService {

	@Autowired
	private CompetitionSetupQuestionRestService competitionSetupQuestionRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private QuestionSetupRestService questionSetupRestService;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> createDefaultQuestion(Long competitionId) {
        return competitionSetupQuestionRestService.addDefaultToCompetition(competitionId).toServiceResult();
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getQuestion(final Long questionId) {
        return competitionSetupQuestionRestService.getByQuestionId(questionId).toServiceResult();
    }

    @Override
	public ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return competitionSetupQuestionRestService.save(competitionSetupQuestionResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> deleteQuestion(Long questionId) {
        return competitionSetupQuestionRestService.deleteById(questionId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> validateApplicationQuestions(CompetitionResource competitionResource, LandingPageForm form, BindingResult bindingResult) {
        boolean incompleteQuestions = hasIncompleteQuestions(competitionResource.getId());
        boolean incompleteSections = hasIncompleteSections(competitionResource.getId());

        if (incompleteQuestions || incompleteSections) {
            return serviceFailure(Collections.emptyList());
        } else {
            return competitionSetupRestService.markSectionComplete(competitionResource.getId(), CompetitionSetupSection.APPLICATION_FORM).toServiceResult();
        }
    }

    private boolean hasIncompleteSections(Long competitionId) {
        Map<CompetitionSetupSubsection, Optional<Boolean>> sectionSetupStatusAsMap = competitionSetupRestService
                .getSubsectionStatuses(competitionId)
                .getSuccessObjectOrThrowException();

        return asList(CompetitionSetupSubsection.APPLICATION_DETAILS, CompetitionSetupSubsection.FINANCES)
                .stream()
                .map(subsection -> sectionSetupStatusAsMap.get(subsection).orElse(Boolean.FALSE))
                .anyMatch(aBoolean -> aBoolean.equals(Boolean.FALSE));
    }

    private boolean hasIncompleteQuestions(Long competitionId) {
        List<Long> allQuestions = getAllQuestionIds(competitionId);
        Map<Long, Boolean> questionSetupStatuses = questionSetupRestService.getQuestionStatuses(competitionId, CompetitionSetupSection.APPLICATION_FORM).getSuccessObjectOrThrowException();

        boolean hasNotCompletedQuestion = allQuestions.stream()
                .map(questionId -> questionSetupStatuses.getOrDefault(questionId, Boolean.FALSE))
                .anyMatch(status -> status.equals(Boolean.FALSE));

        return hasNotCompletedQuestion;
    }

    private List<Long> getAllQuestionIds(Long competitionId) {
        List<QuestionResource> questionResources = questionService.findByCompetition(competitionId);
        List<SectionResource> sections = sectionService.getAllByCompetitionId(competitionId);

        Set<SectionResource> parentSections = sections.stream()
                .filter(sectionResource -> sectionResource.getParentSection() == null)
                .collect(Collectors.toSet());

        Set<Long> projectDetailsAndApplicationSections = parentSections.stream()
                .filter(sectionResource -> sectionResource.getName().equals("Project details") || sectionResource.getName().equals("Application questions"))
                .map(SectionResource::getId)
                .collect(Collectors.toSet());

        return questionResources.stream()
                .filter(question -> projectDetailsAndApplicationSections.contains(question.getSection()) && !question.getName().equals("Application details"))
                .map(questionResource -> questionResource.getId())
                .collect(Collectors.toList());
    }

}
