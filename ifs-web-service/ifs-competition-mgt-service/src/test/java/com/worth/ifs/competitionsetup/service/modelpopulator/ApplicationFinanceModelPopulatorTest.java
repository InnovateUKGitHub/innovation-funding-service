package com.worth.ifs.competitionsetup.service.modelpopulator;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.competitionsetup.service.modelpopulator.application.ApplicationFinancesModelPopulator;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceModelPopulatorTest {

	@InjectMocks
	private ApplicationFinancesModelPopulator populator;

	@Mock
	private CompetitionService competitionService;

	@Mock
	private SectionService sectionService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSubsection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSubsection.FINANCES, result);
	}
	
	@Test
	public void testPopulateModel() {
		Model model = new ExtendedModelMap();
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();
		competitionResource.setFullApplicationFinance(true);
		competitionResource.setIncludeGrowthTable(false);
		ApplicationFinanceForm form = new ApplicationFinanceForm();
		form.setFullApplicationFinance(competitionResource.isFullApplicationFinance());
		form.setIncludeGrowthTable(competitionResource.isIncludeGrowthTable());


		populator.populateModel(model, competitionResource, Optional.empty());
		assertEquals(2, model.asMap().size());
		assertEquals(8L, model.asMap().get("competitionId"));
		assertEquals(form.isFullApplicationFinance(), ((ApplicationFinanceForm)model.asMap().get("competitionSetupForm")).isFullApplicationFinance());
		assertEquals(form.isIncludeGrowthTable(), ((ApplicationFinanceForm)model.asMap().get("competitionSetupForm")).isIncludeGrowthTable());
	}
}
