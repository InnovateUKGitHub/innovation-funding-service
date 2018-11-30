package org.innovateuk.ifs.application.forms.yourprojectcosts.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.ManagementYourProjectCostsViewModel;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class YourProjectCostsViewModelPopulatorTest extends BaseServiceUnitTest<YourProjectCostsViewModelPopulator> {
    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Override
    protected YourProjectCostsViewModelPopulator supplyServiceUnderTest() {
        return new YourProjectCostsViewModelPopulator();
    }

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .build();
        SectionResource sectionResource = newSectionResource()
                .withId(SECTION_ID)
                .withChildSections(Collections.emptyList())
                .withCompetition(competition.getId())
                .withType(SectionType.PROJECT_COST_FINANCES).build();
        UserResource user = newUserResource().build();

        ApplicantResource applicant = newApplicantResource()
                .withProcessRole(newProcessRoleResource()
                        .withUser(user)
                        .withRoleName("leadapplicant")
                        .build())
                .withOrganisation(newOrganisationResource()
                        .withName("orgname")
                        .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                        .build())
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withName("Name")
                .build();
        ApplicantSectionResource section = newApplicantSectionResource()
                .withApplication(application)
                .withCompetition(competition)
                .withCurrentApplicant(applicant)
                .withApplicants(asList(applicant))
                .withSection(sectionResource)
                .withCurrentUser(user)
                .build();

        when(applicantRestService.getSection(user.getId(), APPLICATION_ID, SECTION_ID)).thenReturn(section);

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, user);

        assertEquals(viewModel.getApplicationId(), APPLICATION_ID);
        assertEquals(viewModel.getSectionId(), SECTION_ID);
        assertEquals(viewModel.getCompetitionId(), (long) competition.getId());
        assertEquals(viewModel.getApplicationName(), "Name");
        assertEquals((Long) viewModel.getCompetitionId(), competition.getId());
        assertEquals(viewModel.getOrganisationName(), "orgname");
        assertEquals(viewModel.isIncludeVat(), true);
        assertEquals(viewModel.isComplete(), true);
        assertEquals(viewModel.isOpen(), false);
        assertEquals(viewModel.getFinancesUrl(), String.format("/application/%d/form/FINANCE", APPLICATION_ID));
        assertEquals(viewModel.isInternal(), false);

        assertEquals(viewModel.isReadOnly(), true);
    }

    @Test
    public void populateManagement() {
        long organisationId = 3L;
        long competitionId = 4L;
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(newApplicationResource()
                .withId(APPLICATION_ID)
                .withName("name")
                .withCompetition(competitionId)
                .build()));

        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(newOrganisationResource().withName("orgname").build()));

        ManagementYourProjectCostsViewModel viewModel = service.populateManagement(APPLICATION_ID, SECTION_ID, organisationId, "?query");

        assertEquals(viewModel.getApplicationId(), APPLICATION_ID);
        assertEquals(viewModel.getCompetitionId(), competitionId);
        assertEquals(viewModel.getApplicationName(),"name");
        assertEquals(viewModel.getOrganisationName(),"orgname");

        assertEquals(viewModel.getFinancesUrl(), String.format("/application/%d/form/FINANCE/%d%s", APPLICATION_ID, organisationId, "?query"));


    }
}
