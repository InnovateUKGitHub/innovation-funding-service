package org.innovateuk.ifs.application.readonly.populator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsRowReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermsAndConditionsReadOnlyPopulatorTest {

    @InjectMocks
    private TermsAndConditionsReadOnlyPopulator populator;

    @Mock
    private SectionService sectionService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Test
    public void populate() {
        ReflectionTestUtils.setField(populator, "northernIrelandSubsidyControlToggle", true);

        long sectionId = 123L;
        QuestionResource question = newQuestionResource()
                .withSection(sectionId)
                .build();
        List<OrganisationResource> organisations = newOrganisationResource()
                .withName("England", "Northern Ireland")
                .build(2);
        ApplicationResource application = newApplicationResource()
                .withApplicationState(ApplicationState.OPENED)
                .withLeadOrganisationId(organisations.get(0).getId())
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withNonFinanceType(false)
                .withTermsAndConditions(newGrantTermsAndConditionsResource().withName("Subsidy Control Terms").build())
                .withOtherFundingRulesTermsAndConditions(newGrantTermsAndConditionsResource().withName("State Aid Terms").build())
                .build();
        ApplicationReadOnlyData data = mock(ApplicationReadOnlyData.class);

        Multimap<Long, QuestionStatusResource> multi = mock(Multimap.class);
        when(multi.get(question.getId())).thenReturn(Collections.emptyList());
        when(data.getQuestionToQuestionStatus()).thenReturn(multi);
        when(data.getApplication()).thenReturn(application);
        when(data.getCompetition()).thenReturn(competition);
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(organisations));
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(ImmutableMap.<Long, Set<Long>>builder()
            .put(organisations.get(0).getId(), newHashSet(sectionId))
            .put(organisations.get(1).getId(), newHashSet(sectionId))
        .build());
        when(applicationFinanceRestService.getFinanceDetails(application.getId())).thenReturn(restSuccess(newApplicationFinanceResource()
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId())
                .withNorthernIrelandDeclaration(false, true)
                .build(2)));

        TermsAndConditionsReadOnlyViewModel viewModel = populator.populate(question, data, defaultSettings());

        assertThat(viewModel.getAccordionSectionId(), is(equalTo("terms-and-conditions")));
        assertThat(viewModel.getTermsAndConditionsTerminology(), is(equalTo("View award terms and conditions")));
        assertThat(viewModel.isIncludeFundingRules(), is(true));
        assertThat(viewModel.getPartners().size(), is(equalTo(2)));

        TermsAndConditionsRowReadOnlyViewModel lead = viewModel.getPartners().get(0);

        assertThat(lead.isLead(), is(true));
        assertThat(lead.isAccepted(), is(true));
        assertThat(lead.getFundingRules(), is(equalTo(FundingRules.SUBSIDY_CONTROL)));
        assertThat(lead.getPartnerName(), is(equalTo("England")));
        assertThat(lead.getTermsName(), is(equalTo("Subsidy Control Terms")));

        TermsAndConditionsRowReadOnlyViewModel partner = viewModel.getPartners().get(1);

        assertThat(partner.isLead(), is(false));
        assertThat(partner.isAccepted(), is(true));
        assertThat(partner.getFundingRules(), is(equalTo(FundingRules.STATE_AID)));
        assertThat(partner.getPartnerName(), is(equalTo("Northern Ireland")));
        assertThat(partner.getTermsName(), is(equalTo("State Aid Terms")));


    }
}