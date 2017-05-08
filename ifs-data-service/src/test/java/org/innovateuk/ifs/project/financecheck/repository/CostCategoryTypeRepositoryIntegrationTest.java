package org.innovateuk.ifs.project.financecheck.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryGroup;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryGroupRepository;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryRepository;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryTypeRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

/**
 * Repository Integration tests for Costs.
 */
public class CostCategoryTypeRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CostCategoryTypeRepository> {

    @Override
    @Autowired
    protected void setRepository(CostCategoryTypeRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CostCategoryRepository costCategoryRepository;

    @Autowired
    private CostCategoryGroupRepository costCategoryGroupRepository;

    @Test
    @Rollback
    public void test_createCostCategoryType() {

        List<CostCategory> costCategories = asList(new CostCategory("Labour"), new CostCategory("Materials"));
        CostCategoryGroup costCategoryGroup = new CostCategoryGroup("Industrial Cost Categories group", costCategories);
        CostCategoryType newCostCategoryType = new CostCategoryType("CR&D Industrial Cost Categories", costCategoryGroup);

        // save a new CostCategoryType
        CostCategoryType saved = repository.save(newCostCategoryType);

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        CostCategoryType retrieved = repository.findOne(saved.getId());
        assertNotSame(saved, retrieved);
        assertEquals("CR&D Industrial Cost Categories", retrieved.getName());

        CostCategoryGroup retrievedCostCategoryGroup = retrieved.getCostCategoryGroup();
        assertEquals("Industrial Cost Categories group", retrievedCostCategoryGroup.getDescription());
        assertEquals(retrievedCostCategoryGroup.getCostCategories(), retrieved.getCostCategories());
        List<String> retrievedCostCategoryNames = simpleMap(retrieved.getCostCategories(), CostCategory::getName);
        assertEquals(asList("Labour", "Materials"), retrievedCostCategoryNames);
    }

    @Test
    @Rollback
    public void test_deleteCostCategoryType() {

        List<CostCategory> costCategories = asList(new CostCategory("Labour"), new CostCategory("Materials"));
        CostCategoryGroup costCategoryGroup = new CostCategoryGroup("Industrial Cost Categories group", costCategories);
        CostCategoryType newCostCategoryType = new CostCategoryType("CR&D Industrial Cost Categories", costCategoryGroup);

        // save a new CostCategoryType
        CostCategoryType saved = repository.save(newCostCategoryType);

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again
        CostCategoryType retrieved = repository.findOne(saved.getId());
        Long costCategoryGroupId = retrieved.getCostCategoryGroup().getId();
        List<Long> costCategoryIds = simpleMap(retrieved.getCostCategoryGroup().getCostCategories(), CostCategory::getId);

        assertNotNull(costCategoryGroupRepository.findOne(costCategoryGroupId));
        costCategoryIds.forEach(id -> assertNotNull(costCategoryRepository.findOne(id)));

        // delete it and ensure the delete cascades
        repository.delete(retrieved.getId());
        flushAndClearSession();

        assertNull(costCategoryGroupRepository.findOne(costCategoryGroupId));
        costCategoryIds.forEach(id -> assertNull(costCategoryRepository.findOne(id)));
    }
}
