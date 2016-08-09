package com.worth.ifs.project.finance.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.project.finance.domain.Cost;
import com.worth.ifs.project.finance.domain.CostTimePeriod;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static com.worth.ifs.project.finance.domain.CostTimePeriod.TimeUnit.DAY;
import static com.worth.ifs.project.finance.domain.CostTimePeriod.TimeUnit.YEAR;
import static org.junit.Assert.*;

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

        // assert that there are no temporal parts to this cost
        assertFalse(retrieved.getCostTimePeriod().isPresent());
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

    @Test
    @Rollback
    public void test_createCostWithTimePeriod() {

        // save a new Cost
        Cost newCost = new Cost(new BigDecimal("12.6"), new CostTimePeriod(2, DAY, 4, YEAR));
        Cost saved = repository.save(newCost);

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        Cost retrieved = repository.findOne(saved.getId());
        assertEquals(new BigDecimal("12.60"), retrieved.getValue());

        // ensure the temporal aspect is as expected
        assertTrue(retrieved.getCostTimePeriod().isPresent());

        CostTimePeriod period = retrieved.getCostTimePeriod().get();

        assertEquals(Integer.valueOf(2), period.getOffsetAmount());
        assertEquals(DAY, period.getOffsetUnit());
        assertEquals(Integer.valueOf(4), period.getDurationAmount());
        assertEquals(YEAR, period.getDurationUnit());
    }
}
