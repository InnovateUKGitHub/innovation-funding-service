package org.innovateuk.ifs.application.forms.questions.applicantdetails.model;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertTrue;

public class ApplicationDetailsViewModelTest {

    @Test
    public void testKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withEndDate(ZonedDateTime.now().plusMonths(12))
                .withMinProjectDuration(15)
                .withMaxProjectDuration(30)
                .withFundingType(FundingType.KTP).build();
        InnovationAreaResource innovationAreaResource = InnovationAreaResourceBuilder.newInnovationAreaResource().build();
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withInnovationArea(innovationAreaResource).build();

        ApplicationDetailsViewModel viewModel = new ApplicationDetailsViewModel(applicationResource, competitionResource,
                false, false);

        assertTrue(viewModel.isKtpCompetition());
    }
}
