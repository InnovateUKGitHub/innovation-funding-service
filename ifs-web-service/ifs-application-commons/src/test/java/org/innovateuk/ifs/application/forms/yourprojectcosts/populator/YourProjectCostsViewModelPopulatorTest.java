package org.innovateuk.ifs.application.forms.yourprojectcosts.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class YourProjectCostsViewModelPopulatorTest extends BaseServiceUnitTest<YourProjectCostsViewModelPopulator> {
    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long ORGANISATION_ID = 3L;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private SectionService sectionService;


    @Override
    protected YourProjectCostsViewModelPopulator supplyServiceUnderTest() {
        return new YourProjectCostsViewModelPopulator();
    }

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .build();


        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, false, "");

        assertEquals((long) viewModel.getApplicationId(), APPLICATION_ID);
        assertEquals((long) viewModel.getSectionId(), SECTION_ID);
        assertEquals((long) viewModel.getCompetitionId(), (long) competition.getId());
        assertEquals(viewModel.getApplicationName(), "Name");
        assertEquals(viewModel.getCompetitionId(), competition.getId());
        assertEquals(viewModel.getOrganisationName(), "orgname");
        assertEquals(viewModel.isIncludeVat(), true);
        assertEquals(viewModel.isComplete(), true);
        assertEquals(viewModel.isOpen(), false);
        assertEquals(viewModel.getFinancesUrl(), String.format("/application/%d/form/FINANCE", APPLICATION_ID));
        assertEquals(viewModel.isInternal(), false);

        assertEquals(viewModel.isReadOnly(), true);
    }
}
