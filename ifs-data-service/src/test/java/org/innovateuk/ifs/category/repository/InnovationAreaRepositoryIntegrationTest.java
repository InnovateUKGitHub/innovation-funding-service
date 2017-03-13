package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.junit.Assert.assertEquals;

public class InnovationAreaRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InnovationAreaRepository> {

    @Autowired
    @Override
    protected void setRepository(final InnovationAreaRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void findByName() {
        String innovationAreaName = "Software Engineering";
        InnovationArea innovationArea = newInnovationArea()
                .with(id(null))
                .withName(innovationAreaName)
                .withDescription("Integration tests are the future")
                .build();

        repository.save(innovationArea);
        flushAndClearSession();

        InnovationArea actual = repository.findByName(innovationAreaName);

        assertEquals(innovationArea, actual);
    }

    @Test
    public void findAllByOrderByNameAsc() {
        List<InnovationArea> innovationAreas = newInnovationArea()
                .with(id(null))
                .withName("bbb", "aaa", "ccc")
                .withDescription("bbb description", "aaa description", "ccc description")
                .withPriority(2, 1, 3)
                .build(3);

        repository.save(innovationAreas);
        flushAndClearSession();

        List<InnovationArea> expectedInnovationAreas = newInnovationArea()
                .with(id(null))
                .withName("aaa", "bbb", "ccc")
                .withDescription("aaa description", "bbb description", "ccc description")
                .build(3);

        List<InnovationArea> actual = repository.findAllByOrderByPriorityAsc();

        assertEquals(expectedInnovationAreas, actual);
    }

    @Test
    public void findAll() {
        List<InnovationArea> innovationAreas = newInnovationArea()
                .with(id(null))
                .withName("bbb", "aaa", "ccc")
                .withDescription("aaa description", "bbb description", "ccc description")
                .build(3);

        repository.save(innovationAreas);
        flushAndClearSession();

        List<InnovationArea> actual = repository.findAll();

        assertEquals(innovationAreas, actual);
    }
}