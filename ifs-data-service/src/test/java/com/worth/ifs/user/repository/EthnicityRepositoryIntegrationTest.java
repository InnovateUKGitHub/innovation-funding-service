package com.worth.ifs.user.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.user.domain.Ethnicity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.invite.builder.EthnicityBuilder.newEthnicity;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class EthnicityRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<EthnicityRepository> {

    @Override
    @Autowired
    protected void setRepository(EthnicityRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByActiveTrueOrderByPriorityAsc() throws Exception {
        repository.deleteAll();

        super.flushAndClearSession();

        List<Ethnicity> saved = newEthnicity()
                .withId(null, null, null, null)
                .withActive(true, true, true, false)
                .withName("VisibleName 3", "VisibleName 1", "VisibleName 2", "HiddenName")
                .withDescription("Visible 3", "Visible 1", "Visible 2", "Hidden")
                .withPriority(3, 1, 2, 4)
                .build(4).stream().map(ethnicity -> repository.save(ethnicity)).collect(toList());

        List<Ethnicity> expected = asList(saved.get(1), saved.get(2), saved.get(0));

        List<Ethnicity> found = repository.findByActiveTrueOrderByPriorityAsc();

        assertEquals(expected, found);
    }
}