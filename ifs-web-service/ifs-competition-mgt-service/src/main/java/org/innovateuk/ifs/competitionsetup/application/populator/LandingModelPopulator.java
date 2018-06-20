package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.QuestionSetupRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.viewmodel.LandingViewModel;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.QuestionType.LEAD_ONLY;

/**
 * populates the model for the Application Questions landing page of the competition setup section.
 */
@Service
public class LandingModelPopulator implements CompetitionSetupSectionModelPopulator {

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

        Map<CompetitionSetupSubsection, Boolean> subSectionsStatuses = convertWithDefaultsIfNotPresent(competitionSetupRestService.getSubsectionStatuses(competitionResource.getId()).getSuccess());
        Map<Long, Boolean> questionStatuses = questionSetupRestService.getQuestionStatuses(competitionResource.getId(), sectionToPopulateModel()).getSuccess();

        List<QuestionResource> questions = getSortedQuestions(questionResources, parentSections);
        List<QuestionResource> projectDetails = getSortedProjectDetails(questionResources, parentSections);

        Boolean allStatusesComplete = checkStatusesComplete(subSectionsStatuses, questionStatuses, questions, projectDetails);

        return new LandingViewModel(generalViewModel,
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
        Optional<SectionResource> section = parentSections.stream().filter(sectionResource -> "Application questions".equals(sectionResource.getName())).findFirst();
        return section.isPresent() ? questionResources.stream().filter(questionResource -> section.get().getQuestions().contains(questionResource.getId())).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private List<QuestionResource> getSortedProjectDetails(List<QuestionResource> questionResources, List<SectionResource> parentSections) {
        Optional<SectionResource> section = parentSections.stream().filter(sectionResource -> "Project details".equals(sectionResource.getName())).findFirst();
        return section.isPresent() ? questionResources.stream()
                .filter(questionResource ->  section.get().getQuestions().contains(questionResource.getId()))
                .filter(questionResource -> questionResource.getType() != LEAD_ONLY)
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
