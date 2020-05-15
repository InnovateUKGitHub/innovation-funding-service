package org.innovateuk.ifs.management.competition.setup.eligibility.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.populator.ProjectEligibilityModelPopulator;
import org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.service.CategoryFormatter;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.viewmodel.ProjectEligibilityViewModel;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectEligibilityModelPopulatorTest {

    @InjectMocks
    private ProjectEligibilityModelPopulator populator;

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private CategoryFormatter categoryFormatter;

    @Mock
    private OrganisationTypeRestService organisationTypeRestService;

    @Test
    public void testSectionToPopulateModel() {
        CompetitionSetupSection result = populator.sectionToPopulateModel();

        assertEquals(CompetitionSetupSection.PROJECT_ELIGIBILITY, result);
    }

    @Test
    public void populateModelWithResearchParticipationAmounts() {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("code")
                .withName("name")
                .withId(8L)
                .withLeadApplicantType(asList(1L, 2L))
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .build();

        List<ResearchCategoryResource> researchCategories = emptyList();
        when(categoryRestService.getResearchCategories()).thenReturn(restSuccess(researchCategories));
        when(categoryFormatter.format(CollectionFunctions.asLinkedSet(2L, 3L), researchCategories))
                .thenReturn("formattedcategories");

        when(organisationTypeRestService.getAll()).thenReturn(RestResult.restSuccess(newOrganisationTypeResource()
                .withId(1L, 2L, 3L)
                .withName("Business", "Research", "Something else")
                .withVisibleInSetup(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE)
                .build(3)));

        ProjectEligibilityViewModel viewModel = (ProjectEligibilityViewModel) populator.populateModel(
                getBasicGeneralSetupView(competition),
                competition
        );

        assertArrayEquals(ResearchParticipationAmount.values(), viewModel.getResearchParticipationAmounts());
        assertArrayEquals(CollaborationLevel.values(), viewModel.getCollaborationLevels());
        assertEquals(researchCategories, viewModel.getResearchCategories());
        assertEquals("Business, Research", viewModel.getLeadApplicantTypesText());
        assertEquals("formattedcategories", viewModel.getResearchCategoriesFormatted());
        assertEquals(CompetitionSetupSection.PROJECT_ELIGIBILITY, viewModel.getGeneral().getCurrentSection());
    }

    @Test
    public void populateModelWithNoResearchParticipationAmounts() {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("code")
                .withName("name")
                .withNonFinanceType(true)
                .withLeadApplicantType(asList(1L, 2L))
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .build();

        List<ResearchCategoryResource> researchCategories = emptyList();
        when(categoryRestService.getResearchCategories()).thenReturn(restSuccess(researchCategories));
        when(categoryFormatter.format(CollectionFunctions.asLinkedSet(2L, 3L), researchCategories))
                .thenReturn("formattedcategories");

        when(organisationTypeRestService.getAll()).thenReturn(RestResult.restSuccess(newOrganisationTypeResource()
                .withId(1L, 2L, 3L)
                .withName("Business", "Research", "Something else")
                .withVisibleInSetup(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE)
                .build(3)));

        ProjectEligibilityViewModel viewModel = (ProjectEligibilityViewModel) populator.populateModel(
                getBasicGeneralSetupView(competition),
                competition
        );

        assertArrayEquals(new ResearchParticipationAmount[]{}, viewModel.getResearchParticipationAmounts());
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
        return new GeneralSetupViewModel(
                Boolean.FALSE,
                competition,
                CompetitionSetupSection.PROJECT_ELIGIBILITY,
                CompetitionSetupSection.values(),
                Boolean.TRUE
        );
    }
}
