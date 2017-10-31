package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.QuestionSetupRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.viewmodel.ApplicationLandingViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

/**
 * populates the model for the Application Questions landing page of the competition setup section.
 */
@Service
public class ApplicationLandingModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private QuestionSetupRestService questionSetupRestService;

    @Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.APPLICATION_FORM;
	}

	@Override
	public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
		List<SectionResource> sections = sectionService.getAllByCompetitionId(competitionResource.getId());
		List<QuestionResource> questionResources = questionService.findByCompetition(competitionResource.getId());
        List<SectionResource> generalSections = sections.stream()
                .filter(sectionResource -> sectionResource.getType() == SectionType.GENERAL)
                .collect(Collectors.toList());
        List<SectionResource> parentSections = generalSections.stream()
                .filter(sectionResource -> sectionResource.getParentSection() == null)
                .collect(Collectors.toList());

        Map<CompetitionSetupSubsection, Boolean> subSectionsStatuses = convertWithDefaultsIfNotPresent(competitionSetupRestService.getSubsectionStatuses(competitionResource.getId()).getSuccessObjectOrThrowException());
        Map<Long, Boolean> questionStatuses = questionSetupRestService.getQuestionStatuses(competitionResource.getId(), sectionToPopulateModel()).getSuccessObjectOrThrowException();

        List<QuestionResource> questions = getSortedQuestions(questionResources, parentSections);
        List<QuestionResource> projectDetails = getSortedProjectDetails(questionResources, parentSections);

        Boolean allStatusesComplete = checkStatusesComplete(subSectionsStatuses, questionStatuses, questions, projectDetails);

        return new ApplicationLandingViewModel(generalViewModel,
                questions,
                projectDetails,
                subSectionsStatuses,
                questionStatuses,
                allStatusesComplete);
    }

    private Map<CompetitionSetupSubsection,Boolean> convertWithDefaultsIfNotPresent(Map<CompetitionSetupSubsection, Optional<Boolean>> subSectionsStatuses) {
        return subSectionsStatuses.entrySet().stream().collect(toMap(o -> o.getKey(), o -> o.getValue().orElse(Boolean.FALSE)));
    }

    private List<QuestionResource> getSortedQuestions(List<QuestionResource> questionResources, List<SectionResource> parentSections) {
        Optional<SectionResource> section = parentSections.stream().filter(sectionResource -> sectionResource.getName().equals("Application questions")).findFirst();
        return section.isPresent() ? questionResources.stream().filter(questionResource -> section.get().getQuestions().contains(questionResource.getId())).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private List<QuestionResource> getSortedProjectDetails(List<QuestionResource> questionResources, List<SectionResource> parentSections) {
        Optional<SectionResource> section = parentSections.stream().filter(sectionResource -> sectionResource.getName().equals("Project details")).findFirst();
        return section.isPresent() ? questionResources.stream()
                .filter(questionResource ->  section.get().getQuestions().contains(questionResource.getId()))
                .filter(questionResource -> !questionResource.getShortName().equals(CompetitionSetupQuestionType.APPLICATION_DETAILS.getShortName()))
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    private Boolean checkStatusesComplete(Map<CompetitionSetupSubsection, Boolean> subSectionsStatuses, Map<Long, Boolean> questionStatuses,
                                          List<QuestionResource> questions, List<QuestionResource> projectDetails) {
        return !hasIncompleteSections(subSectionsStatuses) &&
                !hasIncompleteQuestions(questionStatuses, questions) &&
                !hasIncompleteQuestions(questionStatuses, projectDetails);
    }

    private boolean hasIncompleteQuestions(Map<Long, Boolean> questionStatuses, List<QuestionResource> questions) {
        return questions.stream()
                .map(questionResource -> questionStatuses.getOrDefault(questionResource.getId(), Boolean.FALSE))
                .anyMatch(aBoolean -> aBoolean.equals(Boolean.FALSE));
    }

    private boolean hasIncompleteSections(Map<CompetitionSetupSubsection, Boolean> subSectionsStatuses) {
        return subSectionsStatuses.entrySet().stream()
                .filter(entry -> asList(CompetitionSetupSubsection.FINANCES, CompetitionSetupSubsection.APPLICATION_DETAILS).contains(entry.getKey()))
                .anyMatch(entry -> entry.getValue().equals(Boolean.FALSE));
    }
}
