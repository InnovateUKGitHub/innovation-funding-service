package org.innovateuk.ifs.management.competition.setup.fundingeligibility.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.fundingeligibility.form.FundingEligibilityResearchCategoryForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FundingEligibilityFormPopulatorTest {

    @InjectMocks
    private FundingEligibiltyPopulator service;

    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(newHashSet(1L, 2L, 3L))
                .build();

        when(questionRestService
                .getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), RESEARCH_CATEGORY))
                .thenReturn(restSuccess(newQuestionResource().build()));

        FundingEligibilityResearchCategoryForm form = (FundingEligibilityResearchCategoryForm) service.populateForm(competition);

        assertEquals(form.getResearchCategoryId(), competition.getResearchCategories());
        assertTrue(form.getResearchCategoriesApplicable());
    }
}
