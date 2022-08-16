package org.innovateuk.ifs.application.forms.sections.yourprojectfinances.populator;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.finance.populator.FinanceSummaryTableViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel.YourFinancesRowViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel.YourProjectFinancesViewModel;
import org.innovateuk.ifs.application.readonly.populator.FinanceReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationExpressionOfInterestConfigResourceBuilder.newApplicationExpressionOfInterestConfigResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class YourProjectFinancesModelPopulatorTest {

    @InjectMocks
    private YourProjectFinancesModelPopulator populator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private SectionRestService sectionRestService;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationUrlHelper applicationUrlHelper;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private FinanceSummaryTableViewModelPopulator financeSummaryTableViewModelPopulator;

    private final Long applicationId = 1L;
    private final Long sectionId = 2L;
    private final Long organisationId = 3L;
    private final Long organisationTypeId = 4L;
    private final Long eoiApplicationId = 5L;
    private final String sectionUrl = "section url";

    @Test
    public void populate() {

        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.HECP)
                .withEnabledForExpressionOfInterest(true)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withApplicationExpressionOfInterestConfigResource(newApplicationExpressionOfInterestConfigResource()
                        .withApplicationId(applicationId)
                        .withEnabledForExpressionOfInterest(false)
                        .withEoiApplicationId(eoiApplicationId)
                        .build())
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(organisationTypeId)
                .build();
        List<SectionResource> childSections = newSectionResource()
                .withName("Project cost", "Project location", "Your funding")
                .withChildSections(Collections.emptyList(), Collections.emptyList(), Collections.emptyList())
                .withType(SectionType.PROJECT_COST_FINANCES, SectionType.PROJECT_LOCATION, SectionType.FUNDING_FINANCES)
                .withEnabledForPreRegistration(true)
                .build(3);

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionStatusRestService.getCompletedSectionIds(applicationId, organisationId)).thenReturn(restSuccess(Collections.singletonList(sectionId)));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));
        when(sectionRestService.getChildSectionsByParentId(sectionId)).thenReturn(restSuccess(childSections));
        when(applicationUrlHelper.getSectionUrl(any(SectionType.class), anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(sectionUrl));

        YourProjectFinancesViewModel viewModel = populator.populate(applicationId, sectionId, organisationId);

        assertNotNull(viewModel);
        assertTrue(viewModel.isEoiFullApplication());

        List<YourFinancesRowViewModel> financesRowsViewModel =  viewModel.getRows();

        assertEquals(3, financesRowsViewModel.size());
        assertTrue(financesRowsViewModel.get(0).isEnabledForPreRegistration());
        assertTrue(financesRowsViewModel.get(1).isEnabledForPreRegistration());
        assertTrue(financesRowsViewModel.get(2).isEnabledForPreRegistration());
    }
}
