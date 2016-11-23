package com.worth.ifs.competition.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.competition.domain.AssessorCountOption;
import com.worth.ifs.competition.domain.CompetitionType;
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
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Programme");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testAdditiveManufacturingAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Additive Manufacturing");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testSBRIAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("SBRI");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testSpecialAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Special");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

    @Test
    public void testSectorAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Sector");
        List<AssessorCountOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getOptionValue());
    }

}
