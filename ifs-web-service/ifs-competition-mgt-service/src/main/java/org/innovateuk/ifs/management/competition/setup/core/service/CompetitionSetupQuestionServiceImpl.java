package org.innovateuk.ifs.management.competition.setup.core.service;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionSetupRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.form.LandingPageForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

@Service
public class CompetitionSetupQuestionServiceImpl implements CompetitionSetupQuestionService {

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private QuestionSetupRestService questionSetupRestService;

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

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
                .getSuccess();

        return Stream.of(CompetitionSetupSubsection.APPLICATION_DETAILS, CompetitionSetupSubsection.FINANCES)
                .map(subsection -> sectionSetupStatusAsMap.get(subsection).orElse(Boolean.FALSE))
                .anyMatch(aBoolean -> aBoolean.equals(Boolean.FALSE));
    }

    private boolean hasIncompleteQuestions(Long competitionId) {
        List<Long> allQuestions = getAllQuestionIds(competitionId);
        Map<Long, Boolean> questionSetupStatuses = questionSetupRestService.getQuestionStatuses(competitionId, CompetitionSetupSection.APPLICATION_FORM).getSuccess();

        return allQuestions.stream()
                .map(questionId -> questionSetupStatuses.getOrDefault(questionId, Boolean.FALSE))
                .anyMatch(status -> status.equals(Boolean.FALSE));
    }

    private List<Long> getAllQuestionIds(Long competitionId) {
        List<QuestionResource> questionResources = questionRestService.findByCompetition(competitionId).getSuccess();
        List<SectionResource> sections = sectionService.getAllByCompetitionId(competitionId);

        Set<SectionResource> parentSections = sections.stream()
                .filter(sectionResource -> sectionResource.getParentSection() == null)
                .collect(Collectors.toSet());

        Set<Long> projectDetailsAndApplicationSections = parentSections.stream()
                .filter(sectionResource -> "Project details".equals(sectionResource.getName()) || "Application questions".equals(sectionResource.getName()))
                .map(SectionResource::getId)
                .collect(Collectors.toSet());

        return questionResources.stream()
                .filter(question -> projectDetailsAndApplicationSections.contains(question.getSection()))
                .filter(question -> question.getType() != QuestionType.LEAD_ONLY)
                .map(QuestionResource::getId)
                .collect(Collectors.toList());
    }

}
