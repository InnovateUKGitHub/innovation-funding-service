package org.innovateuk.ifs.horizon.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.innovateuk.ifs.horizon.builder.HorizonWorkProgrammeBuilder.newHorizonWorkProgramme;


public class ApplicationHorizonWorkProgrammeResourceRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationHorizonWorkProgrammeRepository> {

    long applicationId = 1L;

    @Autowired
    @Override
    protected void setRepository(ApplicationHorizonWorkProgrammeRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        HorizonWorkProgramme r1 = newHorizonWorkProgramme().withId(1L).withName("CL2").withEnabled(true).build();
        HorizonWorkProgramme r2 = newHorizonWorkProgramme().withId(15L).withName("HORIZON-CL2-2021-DEMOCRACY-01").withParentWorkProgramme(r1).withEnabled(true).build();
        repository.save(buildDomain(applicationId, r1));
        repository.save(buildDomain(applicationId, r2));
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