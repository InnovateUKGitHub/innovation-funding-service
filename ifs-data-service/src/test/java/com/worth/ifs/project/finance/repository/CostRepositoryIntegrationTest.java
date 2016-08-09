package com.worth.ifs.project.finance.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.project.finance.domain.Cost;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Repository Integration tests for Costs.
 */
public class CostRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CostRepository> {

    @Override
    @Autowired
    protected void setRepository(CostRepository repository) {
        this.repository = repository;
    }

    @Test
    @Rollback
    public void test_createStandaloneCost() {

        // save a new Cost
        Cost newCost = new Cost(new BigDecimal("12.6"));
        Cost saved = repository.save(newCost);

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        Cost retrieved = repository.findOne(saved.getId());
        assertNotSame(saved, retrieved);
        assertEquals(new BigDecimal("12.60"), retrieved.getValue());
    }

    @Test
    @Rollback
    public void test_createStandaloneCost_truncatedDecimalPart() {

        // save a new Cost
        Cost newCost = new Cost(new BigDecimal("12.656"));
        Cost saved = repository.save(newCost);

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        Cost retrieved = repository.findOne(saved.getId());
        assertNotSame(saved, retrieved);
        assertEquals(new BigDecimal("12.66"), retrieved.getValue());
    }
}
