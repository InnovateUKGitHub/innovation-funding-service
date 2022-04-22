package org.innovateuk.ifs.horizon.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme.CL2;
import static org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme.HORIZON_CL2_2021_DEMOCRACY_01;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HorizonWorkProgrammeRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<HorizonWorkProgrammeRepository> {

    long applicationId = 1L;

    @Autowired
    @Override
    protected void setRepository(HorizonWorkProgrammeRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        repository.save(buildDomain(applicationId, CL2));
        repository.save(buildDomain(applicationId, HORIZON_CL2_2021_DEMOCRACY_01));
    }

    @Test
    public void findAll() {
        List<ApplicationHorizonWorkProgramme> all = StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(2, all.size());
    }

    @Test
    public void deleteByApplicationId(){
        repository.deleteAllByApplicationId(1L);
        List<ApplicationHorizonWorkProgramme> byApplicationId = repository.findByApplicationId(applicationId);
        assertTrue(byApplicationId.isEmpty());
    }

    @Test
    public void findAllByApplicationId(){
        List<ApplicationHorizonWorkProgramme> byApplicationId = repository.findByApplicationId(applicationId);
        assertEquals(2, byApplicationId.size());
    }

    private ApplicationHorizonWorkProgramme buildDomain(long applicationId, HorizonWorkProgramme type) {
        ApplicationHorizonWorkProgramme programme = new ApplicationHorizonWorkProgramme();

        programme.setWorkProgramme(type);
        programme.setApplicationId(applicationId);

        return programme;
    }
}