package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KtpTemplateTest {

    @InjectMocks
    private KtpTemplate template;

    @Mock
    private CommonBuilders commonBuilders;

    @Captor
    ArgumentCaptor<List<FinanceRowType>> financeRowTypesCaptor;

    @Test
    public void assessorGuidanceOverriddenForKTP() {
        SectionBuilder sectionBuilder = aSection()
                .withAssessorGuidanceDescription("This should be overridden")
                .withType(SectionType.APPLICATION_QUESTIONS);

        List<SectionBuilder> sections = template.sections(newArrayList(sectionBuilder));

        assertEquals("", sections.get(0).getAssessorGuidanceDescription());
    }

    @Test
    public void initialiseFinanceTypesForKTP() {
        Competition competition = CompetitionBuilder
                .newCompetition()
                .build();
        List<FinanceRowType> expectedKtpFinanceRows = FinanceRowType.getKtpFinanceRowTypes();

        when(commonBuilders.saveFinanceRows(any(Competition.class), Mockito.anyList()))
                .thenReturn(competition);

        template.initialiseFinanceTypes(competition);

        verify(commonBuilders).saveFinanceRows(eq(competition), financeRowTypesCaptor.capture());

        List<FinanceRowType> ktpFinanceRows = financeRowTypesCaptor.getValue();

        assertNotNull(ktpFinanceRows);
        assertThat(ktpFinanceRows, containsInAnyOrder(expectedKtpFinanceRows.toArray()));
    }
}