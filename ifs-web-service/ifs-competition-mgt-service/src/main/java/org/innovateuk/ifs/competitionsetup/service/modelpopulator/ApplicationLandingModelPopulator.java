package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.ApplicationLandingViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * populates the model for the Application Questions landing page of the competition setup section.
 */
@Service
public class ApplicationLandingModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.APPLICATION_FORM;
	}

	@Override
	public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
		List<SectionResource> sections = sectionService.getAllByCompetitionId(competitionResource.getId());
		List<QuestionResource> questionResources = questionService.findByCompetition(competitionResource.getId());
        List<SectionResource> generalSections = sections.stream().filter(sectionResource -> sectionResource.getType() == SectionType.GENERAL).collect(Collectors.toList());
        List<SectionResource> parentSections = generalSections.stream().filter(sectionResource -> sectionResource.getParentSection() == null).collect(Collectors.toList());
        return new ApplicationLandingViewModel(generalViewModel, getSortedQuestions(questionResources, parentSections), getSortedProjectDetails(questionResources, parentSections));
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
}
