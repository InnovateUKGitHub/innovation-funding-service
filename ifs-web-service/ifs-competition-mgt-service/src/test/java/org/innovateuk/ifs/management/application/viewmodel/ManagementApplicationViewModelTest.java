package org.innovateuk.ifs.management.application.viewmodel;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.application.view.viewmodel.ManagementApplicationViewModel;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ManagementApplicationViewModelTest {

    @Test
    public void testKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withInnovationAreas(Collections.EMPTY_SET).build();
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withInnovationArea(InnovationAreaResourceBuilder.newInnovationAreaResource().build()).build();

        ManagementApplicationViewModel viewModel = new ManagementApplicationViewModel(applicationResource, competitionResource,
                null, null, Collections.emptyList(), false, false, false, null, false);

        assertTrue(viewModel.isKtpCompetition());
    }

    @Test
    public void testNonKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withInnovationAreas(Collections.EMPTY_SET).build();
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withInnovationArea(InnovationAreaResourceBuilder.newInnovationAreaResource().build()).build();

        ManagementApplicationViewModel viewModel = new ManagementApplicationViewModel(applicationResource, competitionResource,
                null, null, Collections.emptyList(), false, false, false, null, false);

        assertFalse(viewModel.isKtpCompetition());
    }
}
