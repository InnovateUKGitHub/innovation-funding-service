package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.YOUR_FINANCE;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThirdPartyTemplateTest {

    @InjectMocks
    private ThirdPartyTemplate thirdPartyTemplate;

    @Mock
    private CommonBuilders commonBuilders;

    @Captor
    ArgumentCaptor<List<FinanceRowType>> financeRowTypesCaptor;

    @Test
    public void type() {
        assertEquals(FundingType.THIRDPARTY, thirdPartyTemplate.type());
    }

    @Test
    public void setGolTemplate() {
        Competition competition = newCompetition()
                .withCompetitionType(newCompetitionType().withName("Ofgem").build())
                .build();

        when(commonBuilders.getGolTemplate(any(Competition.class)))
                .thenReturn(competition);

        thirdPartyTemplate.setGolTemplate(competition);

        verify(commonBuilders).getGolTemplate(eq(competition));
    }

    @Test
    public void initialiseFinanceTypes() {
        Competition competition = newCompetition()
                .withCompetitionType(newCompetitionType().withName("Ofgem").build())
                .build();

        List<FinanceRowType> expectedFinanceTypes =
                newArrayList(
                        LABOUR,
                        MATERIALS,
                        SUBCONTRACTING_COSTS,
                        TRAVEL,
                        OTHER_COSTS,
                        GRANT_CLAIM_AMOUNT,
                        OTHER_FUNDING,
                        YOUR_FINANCE);

        when(commonBuilders.saveFinanceRows(any(Competition.class), Mockito.anyList()))
                .thenReturn(competition);

        thirdPartyTemplate.initialiseFinanceTypes(competition);

        verify(commonBuilders).saveFinanceRows(eq(competition), financeRowTypesCaptor.capture());

        List<FinanceRowType> financeRowTypes = financeRowTypesCaptor.getValue();

        assertNotNull(financeRowTypes);
        assertThat(financeRowTypes, containsInAnyOrder(expectedFinanceTypes.toArray()));
    }
}