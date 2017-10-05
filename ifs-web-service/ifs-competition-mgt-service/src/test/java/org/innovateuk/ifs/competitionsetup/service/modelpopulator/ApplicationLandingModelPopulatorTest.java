package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.ApplicationLandingViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationLandingModelPopulatorTest {

	@InjectMocks
	private ApplicationLandingModelPopulator populator;

	@Mock
	private QuestionService questionService;

	@Mock
	private FormInputRestService formInputRestService;

	@Mock
	private SectionService sectionService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSection.APPLICATION_FORM, result);
	}
	
	@Test
	public void testPopulateModel() {
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();
		Long questionId = 100L;

		List<SectionResource> sections = newSectionResource().build(1);
		when(sectionService.getAllByCompetitionId(competition.getId())).thenReturn(sections);
		List<QuestionResource> questionResources = asList(newQuestionResource().withId(questionId).build());
		when(questionService.findByCompetition(competition.getId())).thenReturn(questionResources);

		List<FormInputResource> formInputResources = newFormInputResource().withScope(APPLICATION).build(1);
		when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(formInputResources));
		List<FormInputResource> formInputResourcesAssessment = newFormInputResource().withScope(ASSESSMENT).build(1);
		when(formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT)).thenReturn(restSuccess(formInputResourcesAssessment));

		ApplicationLandingViewModel viewModel = (ApplicationLandingViewModel) populator.populateModel(getBasicGeneralSetupView(competition), competition);
		
		assertEquals(new ArrayList(), viewModel.getQuestions());
		assertEquals(new ArrayList(), viewModel.getProjectDetails());
        assertEquals(CompetitionSetupSection.APPLICATION_FORM, viewModel.getGeneral().getCurrentSection());
	}

    @Test
    public void testPopulateModelWithQuestions() {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("code")
                .withName("name")
                .withId(8L)
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .build();
        Long questionId = 100L;

        List<SectionResource> sections = newSectionResource()
                .withName("Application questions")
                .withType(SectionType.GENERAL)
                .withQuestions(asList(questionId))
                .build(1);
        when(sectionService.getAllByCompetitionId(competition.getId())).thenReturn(sections);
        List<QuestionResource> questionResources = asList(newQuestionResource().withId(questionId).build());
        when(questionService.findByCompetition(competition.getId())).thenReturn(questionResources);

        List<FormInputResource> formInputResources = newFormInputResource().withScope(APPLICATION).build(1);
        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(formInputResources));
        List<FormInputResource> formInputResourcesAssessment = newFormInputResource().withScope(ASSESSMENT).build(1);
        when(formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT)).thenReturn(restSuccess(formInputResourcesAssessment));

        ApplicationLandingViewModel viewModel = (ApplicationLandingViewModel) populator.populateModel(getBasicGeneralSetupView(competition), competition);

        assertEquals(questionResources, viewModel.getQuestions());
        assertEquals(new ArrayList(), viewModel.getProjectDetails());
        assertEquals(CompetitionSetupSection.APPLICATION_FORM, viewModel.getGeneral().getCurrentSection());
    }

	private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
		return new GeneralSetupViewModel(Boolean.FALSE, competition, CompetitionSetupSection.APPLICATION_FORM, CompetitionSetupSection.values(), Boolean.TRUE);
	}
}
