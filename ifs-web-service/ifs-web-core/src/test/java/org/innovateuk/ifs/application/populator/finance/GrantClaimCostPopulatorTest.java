package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.finance.view.FinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.viewmodel.finance.GrantClaimCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResponseResourceBuilder.newApplicantFormInputResponseResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link GrantClaimCostPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class GrantClaimCostPopulatorTest {
    private static final Integer MAXIMUM_GRANT_CLAIM = 50;
    private static final Integer GRANT_CLAIM = 10;
    private static final Long GRANT_CLAIM_ID = 1L;

    @InjectMocks
    private GrantClaimCostPopulator grantClaimCostPopulator;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private FinanceHandler financeHandler;

    @Test
    public void testPopulate() {
        ApplicantResource currentApplicant = newApplicantResource().withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build();
        ApplicantSectionResource section = newApplicantSectionResource()
                .withCurrentApplicant(currentApplicant)
                .withCurrentUser(newUserResource().build())
                .withCompetition(newCompetitionResource().build())
                .withApplication(newApplicationResource().build())
                .build();
        ApplicantSectionResource childSection = newApplicantSectionResource()
                .build();
        ApplicantQuestionResource question = newApplicantQuestionResource()
                .withApplicantQuestionStatuses(
                        newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .withMarkedAsCompleteBy(currentApplicant)
                                .build(1)
                )
                .withQuestion(newQuestionResource().withType(QuestionType.COST).build())
                .build();
        ApplicantFormInputResource applicantFormInput = newApplicantFormInputResource().build();
        ApplicantFormInputResponseResource applicantResponse = newApplicantFormInputResponseResource().build();

        ApplicationFinanceResource financeResource = mock(ApplicationFinanceResource.class);
        FinanceFormHandler formHandler = mock(FinanceFormHandler.class);
        FinanceRowCostCategory category = mock(FinanceRowCostCategory.class);
        FinanceRowItem rowItem = mock(FinanceRowItem.class);
        GrantClaim grantClaim = mock(GrantClaim.class);
        when(applicationFinanceRestService.getFinanceDetails(section.getApplication().getId(), currentApplicant.getOrganisation().getId())).thenReturn(restSuccess(financeResource));
        when(financeResource.getFinanceOrganisationDetails(FinanceRowType.FINANCE)).thenReturn(category);
        when(financeHandler.getFinanceFormHandler(currentApplicant.getOrganisation().getOrganisationType())).thenReturn(formHandler);
        when(formHandler.addCostWithoutPersisting(section.getApplication().getId(), section.getCurrentUser().getId(), question.getQuestion().getId())).thenReturn(rowItem);
        when(financeResource.getMaximumFundingLevel()).thenReturn(MAXIMUM_GRANT_CLAIM);
        when(financeResource.getGrantClaim()).thenReturn(grantClaim);
        when(grantClaim.getGrantClaimPercentage()).thenReturn(GRANT_CLAIM);
        when(grantClaim.getId()).thenReturn(GRANT_CLAIM_ID);

        GrantClaimCostViewModel viewModel = grantClaimCostPopulator.populate(section, childSection, question, applicantFormInput, applicantResponse);

        assertThat(viewModel.isComplete(), equalTo(true));
        assertThat(viewModel.getCostCategory(), equalTo(category));
        assertThat(viewModel.getViewmode(), equalTo("readonly"));
        assertThat(viewModel.getMaximumGrantClaimPercentage(), equalTo(MAXIMUM_GRANT_CLAIM));
        assertThat(viewModel.getOrganisationGrantClaimPercentage(), equalTo(GRANT_CLAIM));
        assertThat(viewModel.getOrganisationGrantClaimPercentageId(), equalTo(GRANT_CLAIM_ID));

        verify(category).addCost(rowItem);
    }
}
