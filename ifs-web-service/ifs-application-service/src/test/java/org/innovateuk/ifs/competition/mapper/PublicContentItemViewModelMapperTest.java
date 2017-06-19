package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.viewmodel.PublicContentItemViewModel;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertEquals;

public class PublicContentItemViewModelMapperTest {
    @Test
    public void mapToViewModel_expectedResourceVariablesAreMappedToViewModel() throws Exception {
        String eligibilitySummary = "eligibility summary";
        String shortDescription = "short description";
        Long competitionId = 123L;

        ZonedDateTime openDate = ZonedDateTime.now().minusDays(3L);
        ZonedDateTime closedDate = ZonedDateTime.now().plusDays(3L);
        String competitionTitle = "competition title";

        PublicContentResource publicContentResource = newPublicContentResource()
                .withEligibilitySummary(eligibilitySummary)
                .withShortDescription(shortDescription)
                .withCompetitionId(competitionId).build();

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withCompetitionOpenDate(openDate)
                .withCompetitionCloseDate(closedDate)
                .withCompetitionTitle(competitionTitle)
                .withContentSection(publicContentResource).build();

        PublicContentItemViewModelMapper contentItemViewModelMapper = new PublicContentItemViewModelMapper();
        PublicContentItemViewModel result = contentItemViewModelMapper.mapToViewModel(publicContentItemResource);

        assertEquals(closedDate, result.getCompetitionCloseDate());
        assertEquals(openDate, result.getCompetitionOpenDate());
        assertEquals(competitionTitle, result.getCompetitionTitle());

        assertEquals(eligibilitySummary, result.getEligibilitySummary());
        assertEquals(shortDescription, result.getShortDescription());
        assertEquals(competitionId, result.getCompetitionId());
    }

}