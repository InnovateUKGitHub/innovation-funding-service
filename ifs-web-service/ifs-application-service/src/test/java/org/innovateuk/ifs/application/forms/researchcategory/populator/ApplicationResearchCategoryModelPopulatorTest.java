package org.innovateuk.ifs.application.forms.researchcategory.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.forms.researchcategory.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionResearchCategoryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResearchCategoryLinkResourceBuilder.newCompetitionResearchCategoryLinkResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.SMALL;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ApplicationResearchCategoryModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationResearchCategoryModelPopulator populator;

    @Mock
    private FinanceService financeService;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private UserService userService;

    @Mock
    private CompetitionResearchCategoryRestService competitionResearchCategoryRestService;

    @Test
    public void populateWithApplicationFinances() {
        long loggedInUserId = 1L;

        CompetitionResource competitionResource = newCompetitionResource().build();

        ResearchCategoryResource researchCategoryOne = newResearchCategoryResource().withName("one").build();
        CompetitionResearchCategoryLinkResource competitionResearchCategoryLinkOne = newCompetitionResearchCategoryLinkResource()
                .withCompetition(competitionResource).withCategory(researchCategoryOne).build();

        ResearchCategoryResource researchCategoryTwo = newResearchCategoryResource().withName("two").build();
        CompetitionResearchCategoryLinkResource competitionResearchCategoryLinkTwo = newCompetitionResearchCategoryLinkResource()
                .withCompetition(competitionResource).withCategory(researchCategoryTwo).build();


        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionResource.getId())
                .withCompetitionName(competitionResource.getName())
                .withResearchCategory(researchCategoryOne).build();
        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource().withApplication
                (applicationResource.getId()).withOrganisationSize(SMALL).build(3);
        QuestionResource questionResource = newQuestionResource().build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(newOrganisationResource().build()).build();
        ProcessRoleResource leadApplicantProcessRoleResource = newProcessRoleResource().build();
        UserResource leadApplicant = newUserResource()
                .withFirstName("Steve")
                .withLastName("Smith")
                .build();

        when(competitionResearchCategoryRestService.findByCompetition(applicationResource.getCompetition()))
                .thenReturn(restSuccess(asList(competitionResearchCategoryLinkOne, competitionResearchCategoryLinkTwo)));
        when(financeService.getApplicationFinanceDetails(applicationResource.getId())).thenReturn
                (applicationFinanceResource);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(applicationResource.getCompetition(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(questionResource));
        when(applicantRestService.getQuestion(loggedInUserId, applicationResource.getId(), questionResource.getId()))
                .thenReturn(newApplicantQuestionResource()
                        .withCurrentApplicant(applicantResource)
                        .withQuestion(questionResource)
                        .withApplicantQuestionStatuses(newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());
        when(userService.isLeadApplicant(loggedInUserId, applicationResource)).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource.getId())).thenReturn
                (leadApplicantProcessRoleResource);
        when(userService.findById(leadApplicantProcessRoleResource.getUser())).thenReturn(leadApplicant);

        ResearchCategoryViewModel researchCategoryViewModel = populator.populate(applicationResource, loggedInUserId,
                questionResource.getId());

        assertEquals(competitionResource.getName(), researchCategoryViewModel.getCurrentCompetitionName());
        assertEquals(asList(researchCategoryOne, researchCategoryTwo), researchCategoryViewModel.getAvailableResearchCategories());
        assertTrue(researchCategoryViewModel.getHasApplicationFinances());
        assertEquals(researchCategoryOne.getName(), researchCategoryViewModel.getResearchCategory());
        assertEquals(questionResource.getId(), researchCategoryViewModel.getQuestionId());
        assertEquals(applicationResource.getId(), researchCategoryViewModel.getApplicationId());
        assertTrue(researchCategoryViewModel.isClosed());
        assertTrue(researchCategoryViewModel.isComplete());
        assertTrue(researchCategoryViewModel.isCanMarkAsComplete());
        assertTrue(researchCategoryViewModel.isAllReadOnly());
        assertTrue(researchCategoryViewModel.isUserLeadApplicant());
        assertEquals("Steve Smith", researchCategoryViewModel.getLeadApplicantName());
    }

    @Test
    public void populateWithoutApplicationFinancesAndResearchCategorySelected() {
        long loggedInUserId = 1L;

        CompetitionResource competitionResource = newCompetitionResource().build();
        ResearchCategoryResource researchCategory = newResearchCategoryResource().withName("one").build();
        CompetitionResearchCategoryLinkResource competitionResearchCategoryLink = newCompetitionResearchCategoryLinkResource()
                .withCompetition(competitionResource).withCategory(researchCategory).build();


        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionResource.getId())
                .withCompetitionName(competitionResource.getName())
                .withResearchCategory(researchCategory).build();
        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource().withApplication
                (applicationResource.getId()).build(3);
        QuestionResource questionResource = newQuestionResource().build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(newOrganisationResource().build()).build();
        ProcessRoleResource leadApplicantProcessRoleResource = newProcessRoleResource().build();
        UserResource leadApplicant = newUserResource()
                .withFirstName("Steve")
                .withLastName("Smith")
                .build();


        when(competitionResearchCategoryRestService.findByCompetition(applicationResource.getCompetition()))
                .thenReturn(restSuccess(asList(competitionResearchCategoryLink)));
        when(financeService.getApplicationFinanceDetails(applicationResource.getId())).thenReturn
                (applicationFinanceResource);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(applicationResource.getCompetition(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(questionResource));
        when(applicantRestService.getQuestion(loggedInUserId, applicationResource.getId(), questionResource.getId()))
                .thenReturn(newApplicantQuestionResource()
                        .withCurrentApplicant(applicantResource)
                        .withQuestion(questionResource)
                        .withApplicantQuestionStatuses(newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());
        when(userService.isLeadApplicant(loggedInUserId, applicationResource)).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource.getId())).thenReturn
                (leadApplicantProcessRoleResource);
        when(userService.findById(leadApplicantProcessRoleResource.getUser())).thenReturn(leadApplicant);

        ResearchCategoryViewModel researchCategoryViewModel = populator.populate(applicationResource, loggedInUserId,
                questionResource.getId());

        assertEquals(competitionResource.getName(), researchCategoryViewModel.getCurrentCompetitionName());
        assertEquals(asList(researchCategory), researchCategoryViewModel.getAvailableResearchCategories());
        assertFalse(researchCategoryViewModel.getHasApplicationFinances());
        assertEquals(researchCategory.getName(), researchCategoryViewModel.getResearchCategory());
        assertEquals(questionResource.getId(), researchCategoryViewModel.getQuestionId());
        assertEquals(applicationResource.getId(), researchCategoryViewModel.getApplicationId());
        assertTrue(researchCategoryViewModel.isClosed());
        assertTrue(researchCategoryViewModel.isComplete());
        assertTrue(researchCategoryViewModel.isCanMarkAsComplete());
        assertTrue(researchCategoryViewModel.isAllReadOnly());
        assertTrue(researchCategoryViewModel.isUserLeadApplicant());
        assertEquals("Steve Smith", researchCategoryViewModel.getLeadApplicantName());
    }

    @Test
    public void populateWithTwoResearchCategoriesSet() {
        Long loggedInUserId = 1L;
        String researchCategoryNameOne = "researchCategoryOne";
        String researchCategoryNameTwo = "researchCategoryTwo";

        ResearchCategoryResource researchCategoryOne = newResearchCategoryResource().withName(researchCategoryNameOne).build();
        ResearchCategoryResource researchCategoryTwo = newResearchCategoryResource().withName(researchCategoryNameTwo).build();

        CompetitionResource competitionResource = newCompetitionResource()
                .withResearchCategories(asSet(researchCategoryOne.getId(), researchCategoryTwo.getId())).build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(newOrganisationResource().build()).build();
        ProcessRoleResource leadApplicantProcessRoleResource = newProcessRoleResource().build();
        UserResource leadApplicant = newUserResource()
                .withFirstName("Steve")
                .withLastName("Smith")
                .build();

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionResource.getId())
                .withCompetitionName(competitionResource.getName())
                .withResearchCategory(researchCategoryOne).build();

        QuestionResource questionResource = newQuestionResource().build();

        CompetitionResearchCategoryLinkResource competitionResearchCategoryOne = newCompetitionResearchCategoryLinkResource()
                .withCategory(researchCategoryOne)
                .withCompetition(competitionResource)
                .build();

        CompetitionResearchCategoryLinkResource competitionResearchCategoryTwo = newCompetitionResearchCategoryLinkResource()
                .withCategory(researchCategoryTwo)
                .withCompetition(competitionResource)
                .build();

        when(competitionResearchCategoryRestService.findByCompetition(applicationResource.getCompetition()))
                .thenReturn(restSuccess(asList(competitionResearchCategoryOne, competitionResearchCategoryTwo)));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(applicationResource.getCompetition(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(questionResource));
        when(applicantRestService.getQuestion(loggedInUserId, applicationResource.getId(), questionResource.getId()))
                .thenReturn(newApplicantQuestionResource()
                        .withCurrentApplicant(applicantResource)
                        .withQuestion(questionResource)
                        .withApplicantQuestionStatuses(newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());
        when(userService.isLeadApplicant(loggedInUserId, applicationResource)).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource.getId())).thenReturn
                (leadApplicantProcessRoleResource);
        when(userService.findById(leadApplicantProcessRoleResource.getUser())).thenReturn(leadApplicant);

        ResearchCategoryViewModel researchCategoryViewModel = populator.populate(applicationResource, loggedInUserId,
                questionResource.getId());

        assertEquals(asList(researchCategoryOne, researchCategoryTwo), researchCategoryViewModel.getAvailableResearchCategories());
        assertEquals(researchCategoryNameOne, researchCategoryViewModel.getAvailableResearchCategories().get(0).getName());
        assertEquals(researchCategoryNameTwo, researchCategoryViewModel.getAvailableResearchCategories().get(1).getName());
    }
}