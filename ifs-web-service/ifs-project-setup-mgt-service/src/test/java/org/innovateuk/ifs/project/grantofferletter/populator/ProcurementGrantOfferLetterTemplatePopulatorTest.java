package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcurementGrantOfferLetterTemplatePopulatorTest {

    @InjectMocks
    private ProcurementGrantOfferLetterTemplatePopulator populator;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Test
    public void populate() {
        ProjectResource project = newProjectResource()
                .withApplication(1L)
                .build();
        CompetitionResource competition = newCompetitionResource().build();
        PartnerOrganisationResource org = newPartnerOrganisationResource()
                .withOrganisationName("Organisation")
                .build();
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId())).thenReturn(RestResult.restSuccess(newArrayList(org)));

        ProcurementGrantOfferLetterTemplateViewModel viewModel = populator.populate(project, competition);

        assertThat(viewModel.getApplicationId(), is(equalTo(project.getApplication())));
        assertThat(viewModel.getOrganisationName(), is(equalTo(org.getOrganisationName())));
    }
}