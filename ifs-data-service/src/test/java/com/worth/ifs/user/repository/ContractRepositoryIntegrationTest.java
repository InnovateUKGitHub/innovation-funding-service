package com.worth.ifs.user.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.user.domain.Contract;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.user.builder.ContractBuilder.newContract;
import static org.junit.Assert.*;

public class ContractRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ContractRepository> {

    @Override
    @Autowired
    protected void setRepository(ContractRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByCurrentTrue() throws Exception {
        loginPaulPlum();

        List<Contract> contracts = newContract()
                .withText("foo", "bar")
                .withAppendixOne("appendix1", "appendix1")
                .withAppendixTwo("appendix2", "appendix2")
                .withCurrent(true, false)
                .build(2);
        repository.save(contracts);

        Contract expectedContract = contracts.get(0);
        Contract currentContract = repository.findByCurrentTrue();

        assertEquals(expectedContract.getText(), currentContract.getText());
        assertEquals(expectedContract.getAppendixOne(), currentContract.getAppendixOne());
        assertEquals(expectedContract.getAppendixTwo(), currentContract.getAppendixTwo());
        assertTrue(currentContract.isCurrent());

        assertEquals(getPaulPlum().getId(), currentContract.getCreatedBy().getId());
        assertEquals(getPaulPlum().getId(), currentContract.getModifiedBy().getId());
        assertNotNull(currentContract.getCreatedOn());
        assertNotNull(currentContract.getModifiedOn());
    }
}