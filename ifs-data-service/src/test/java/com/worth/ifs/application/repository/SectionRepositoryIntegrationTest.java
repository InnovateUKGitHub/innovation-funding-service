package com.worth.ifs.application.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.repository.CompetitionRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.util.CollectionFunctions.asListOfPairs;
import static com.worth.ifs.util.CollectionFunctions.mapWithIndex;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
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
    public void test_findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc() throws Exception {
        final Long competitionId = competitionRepository.save(newCompetition().withId(2L).build()).getId();
        final Long otherCompetitionId = competitionRepository.save(newCompetition().withId(3L).build()).getId();

        List<Pair<Long, Boolean>> compIdVisibilityPairs = asListOfPairs(competitionId, TRUE, competitionId, TRUE, competitionId, FALSE, otherCompetitionId, TRUE, otherCompetitionId, TRUE, otherCompetitionId, FALSE);
        List<Section> saved = mapWithIndex(compIdVisibilityPairs, (index, compIdVisibilityPair) -> {
            Section section = new Section();
            section.setId(Long.valueOf(index));
            section.setCompetition(newCompetition().with(id(compIdVisibilityPair.getLeft())).build());
            section.setDisplayInAssessmentApplicationSummary(compIdVisibilityPair.getRight());
            section.setPriority(index);
            return section;
        }).stream().map(section -> repository.save(section)).collect(toList());

        // The expected sections are those matching the competition id which are also visible for assessment
        List<Section> expected = saved.subList(0, 2);

        assertEquals(expected, repository.findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc(competitionId));
    }
}