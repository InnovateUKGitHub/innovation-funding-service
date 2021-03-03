package org.innovateuk.ifs.competitionsetup.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competitionsetup.domain.AssessorCountOption;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssessorCountOptionRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessorCountOptionRepository> {

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    @Override
    protected void setRepository(AssessorCountOptionRepository repository) {
        this.repository = repository;
    }

    @Test
    public void testProgrammeAssessorOptions() {
        CompetitionType competitionType = competitionTypeRepository.findByName("Programme");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionType.getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testAdditiveManufacturingAssessorOptions() {
        CompetitionType competitionType = competitionTypeRepository.findByName("Additive Manufacturing");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionType.getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testSBRIAssessorOptions() {
        CompetitionType competitionType = competitionTypeRepository.findByName("SBRI");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionType.getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testSpecialAssessorOptions() {
        CompetitionType competitionType = competitionTypeRepository.findByName("Special");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionType.getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testSectorAssessorOptions() {
        CompetitionType competitionType = competitionTypeRepository.findByName("Sector");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionType.getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

}
