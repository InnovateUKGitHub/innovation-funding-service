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

        repository.deleteAll();
        List<Contract> contracts = newContract()
                .withId(null, null)
                .withText("foo", "bar")
                .withAnnexA("annexA1", "annexA2")
                .withAnnexB("annexB1", "annexB2")
                .withAnnexC("annexC1", "annexC2")
                .withCurrent(true, false)
                .build(2);
        repository.save(contracts);

        Contract expectedContract = contracts.get(0);
        Contract currentContract = repository.findByCurrentTrue();

        assertEquals(expectedContract.getText(), currentContract.getText());
        assertEquals(expectedContract.getAnnexA(), currentContract.getAnnexA());
        assertEquals(expectedContract.getAnnexB(), currentContract.getAnnexB());
        assertEquals(expectedContract.getAnnexC(), currentContract.getAnnexC());
        assertTrue(currentContract.isCurrent());

        assertEquals(getPaulPlum().getId(), currentContract.getCreatedBy().getId());
        assertEquals(getPaulPlum().getId(), currentContract.getModifiedBy().getId());
        assertNotNull(currentContract.getCreatedOn());
        assertNotNull(currentContract.getModifiedOn());
    }
}