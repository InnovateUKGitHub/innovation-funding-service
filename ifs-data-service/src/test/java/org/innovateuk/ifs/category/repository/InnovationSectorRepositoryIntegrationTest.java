package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.junit.Assert.assertEquals;

public class InnovationSectorRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InnovationSectorRepository> {

    @Autowired
    @Override
    protected void setRepository(final InnovationSectorRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void findAllByOrderByNameAsc() {
        List<InnovationSector> innovationSectors = newInnovationSector()
                .withName("bbb", "aaa", "ccc")
                .withPriority(2, 1, 3)
                .withChildren(
                        newInnovationArea().withName("b").build(1),
                        newInnovationArea().withName("a").build(1),
                        newInnovationArea().withName("c").build(1)
                )
                .build(3);

        List<InnovationSector> expectedInnovationSectors = newInnovationSector()
                .withName("aaa", "bbb", "ccc")
                .withChildren(
                        newInnovationArea().withName("a").build(1),
                        newInnovationArea().withName("b").build(1),
                        newInnovationArea().withName("c").build(1)
                )
                .build(3);

        repository.save(innovationSectors);
        flushAndClearSession();

        List<InnovationSector> actual = repository.findAllByOrderByPriorityAsc();

        assertEquals(expectedInnovationSectors, actual);
    }

    @Test
    public void findAll() {
        List<InnovationSector> innovationSectors = newInnovationSector()
                .withName("bbb", "aaa", "ccc")
                .withChildren(
                        newInnovationArea().withName("b").build(1),
                        newInnovationArea().withName("a").build(1),
                        newInnovationArea().withName("c").build(1)
                )
                .build(3);

        repository.save(innovationSectors);
        flushAndClearSession();

        List<InnovationSector> actual = repository.findAll();

        assertEquals(innovationSectors, actual);
    }
}
