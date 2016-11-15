package com.worth.ifs.competition.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.CompetitionType;
import com.worth.ifs.competition.domain.CompetitionTypeAssessorOption;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompetitionTypeAssessorOptionRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionTypeAssessorOptionRepository> {

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionTypeAssessorOptionRepository repository) {
        this.repository = repository;
    }

    @Test
    public void testProgrammeAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Programme");
        List<CompetitionTypeAssessorOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getAssessorOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getAssessorOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getAssessorOptionValue());
    }

    @Test
    public void testAdditiveManufacturingAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Additive Manufacturing");
        List<CompetitionTypeAssessorOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getAssessorOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getAssessorOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getAssessorOptionValue());
    }

    @Test
    public void testSBRIAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("SBRI");
        List<CompetitionTypeAssessorOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getAssessorOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getAssessorOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getAssessorOptionValue());
    }

    @Test
    public void testSpecialAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Special");
        List<CompetitionTypeAssessorOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getAssessorOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getAssessorOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getAssessorOptionValue());
    }

    @Test
    public void testSectorAssessorOptions() {
        List<CompetitionType> competitionTypes = competitionTypeRepository.findByName("Sector");
        List<CompetitionTypeAssessorOption> assessorOptionList = repository.findByCompetitionTypeId(competitionTypes.get(0).getId());
        assertEquals(3, assessorOptionList.size());
        // Test three options.
        assertEquals(Integer.valueOf(1), assessorOptionList.get(0).getAssessorOptionValue());
        assertEquals(Integer.valueOf(3), assessorOptionList.get(1).getAssessorOptionValue());
        assertEquals(Integer.valueOf(5), assessorOptionList.get(2).getAssessorOptionValue());
    }

}
