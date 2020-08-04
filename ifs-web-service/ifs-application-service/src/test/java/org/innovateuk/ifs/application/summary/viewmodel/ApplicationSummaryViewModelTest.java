package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class ApplicationSummaryViewModelTest {

    @Test
    public void testKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withInnovationAreas(Collections.emptySet()).build();
        InnovationAreaResource innovationAreaResource = InnovationAreaResourceBuilder.newInnovationAreaResource().build();
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withInnovationArea(innovationAreaResource).build();

        ApplicationSummaryViewModel viewModel = new ApplicationSummaryViewModel(null,
                applicationResource, competitionResource, false);

        assertTrue(viewModel.isKtpCompetition());
    }
}
