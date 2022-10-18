package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grantofferletter.populator.GrantOfferLetterTemplatePopulator;
import org.innovateuk.ifs.project.grantofferletter.populator.KtpGrantOfferLetterTemplatePopulator;
import org.innovateuk.ifs.project.grantofferletter.template.resource.GolTemplateResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(Parameterized.class)
public class KtpGrantOfferLetterControllerTest extends BaseControllerMockMVCTest<GrantOfferLetterController> {

    private final FundingType fundingType;

    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private GrantOfferLetterService grantOfferLetterService;

    @Mock
    private GrantOfferLetterTemplatePopulator populator;

    @Mock
    private KtpGrantOfferLetterTemplatePopulator ktpGrantOfferLetterTemplatePopulator;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    public KtpGrantOfferLetterControllerTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Override
    protected GrantOfferLetterController supplyControllerUnderTest() {
        return new GrantOfferLetterController();
    }

    @Test
    public void viewGrantOfferLetterTemplate_KTP() throws Exception {
        long projectId = 123L;

        GolTemplateResource golTemplateResource = new GolTemplateResource();
        golTemplateResource.setName(fundingType.getGolType());
        golTemplateResource.setTemplate("gol-template");

        CompetitionResource competition = newCompetitionResource()
                .withGolTemplate(golTemplateResource)
                .build();
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        ProjectResource projectResource = newProjectResource()
                .withCompetition(competition.getId())
                .build();

        when(projectService.getById(projectId)).thenReturn(projectResource);

        KtpGrantOfferLetterTemplateViewModel viewModel = mock(KtpGrantOfferLetterTemplateViewModel.class);

        when(ktpGrantOfferLetterTemplatePopulator.populate(projectResource)).thenReturn(viewModel);

        mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/template"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/gol-template"))
                .andExpect(model().attribute("model", viewModel));
    }
}
