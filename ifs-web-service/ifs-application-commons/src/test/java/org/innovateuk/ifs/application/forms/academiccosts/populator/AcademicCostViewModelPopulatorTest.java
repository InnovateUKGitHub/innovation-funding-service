package org.innovateuk.ifs.application.forms.academiccosts.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.academiccosts.viewmodel.AcademicCostViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AcademicCostViewModelPopulatorTest extends BaseServiceUnitTest<AcademicCostViewModelPopulator> {

    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;
    private static final long SECTION_ID = 3L;

    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private SectionService sectionService;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected AcademicCostViewModelPopulator supplyServiceUnderTest() {
        return new AcademicCostViewModelPopulator();
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

        ApplicationFinanceResource finance = newApplicationFinanceResource().build();

        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(asList(SECTION_ID));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        AcademicCostViewModel viewModel = service.populate(ORGANISATION_ID, APPLICATION_ID, SECTION_ID, true);

        assertEquals(viewModel.getApplicationFinanceId(), (long) finance.getId());
        assertEquals(viewModel.getApplicationId(), (long) application.getId());
        assertEquals(viewModel.getSectionId(), SECTION_ID);
        assertEquals(viewModel.getOrganisationId(), ORGANISATION_ID);
        assertEquals(viewModel.getApplicationName(), application.getName());
        assertEquals(viewModel.getOrganisationName(), organisation.getName());
        assertEquals(viewModel.isIncludeVat(), true);
        assertEquals(viewModel.isComplete(), true);
        assertEquals(viewModel.isOpen(), false);
        assertEquals(viewModel.getFinancesUrl(), String.format("/application/%d/form/FINANCE", APPLICATION_ID));
        assertEquals(viewModel.isApplicant(), true);


    }


}
