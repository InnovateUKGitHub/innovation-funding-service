package org.innovateuk.ifs.form.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.form.domain.Section;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.util.CollectionFunctions.asListOfPairs;
import static org.innovateuk.ifs.util.CollectionFunctions.mapWithIndex;
import static org.junit.Assert.assertEquals;

public class SectionRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<SectionRepository> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    @Override
    protected void setRepository(SectionRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc() {

        setLoggedInUser(getCompAdmin());

        Competition compOne = competitionRepository.save(newCompetition().with(id(null)).build());
        Competition compTwo = competitionRepository.save(newCompetition().with(id(null)).build());

        List<Pair<Competition, Boolean>> compIdVisibilityPairs = asListOfPairs(compOne, TRUE,
                                                                               compOne, TRUE,
                                                                               compOne, FALSE,
                                                                               compTwo, TRUE,
                                                                               compTwo, TRUE,
                                                                               compTwo, FALSE);

        List<Section> saved =
                mapWithIndex(compIdVisibilityPairs, (index, compIdVisibilityPair) ->
                        newSection()
                                .withId(Long.valueOf(index))
                                .withCompetition(newCompetition().with(id(compIdVisibilityPair.getLeft().getId())).build())
                                .withDisplayInAssessmentApplicationSummary(compIdVisibilityPair.getRight())
                                .withPriority(index)
                                .build())
                        .stream()
                        .map(section -> repository.save(section))
                        .collect(toList());

        // The expected sections are those matching the competition id which are also visible for assessment
        List<Section> expected = saved.subList(0, 2);
        List<Section> actual = repository.findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc(compOne.getId());

        assertEquals(expected, actual);

    }
}
