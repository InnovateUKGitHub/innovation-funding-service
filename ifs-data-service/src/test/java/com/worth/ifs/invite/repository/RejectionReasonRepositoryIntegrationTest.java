package com.worth.ifs.invite.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.invite.domain.RejectionReason;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class RejectionReasonRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<RejectionReasonRepository> {

    @Autowired
    @Override
    protected void setRepository(RejectionReasonRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() throws Exception {
        List<RejectionReason> expected = newRejectionReason()
                .withId(1L, 2L, 3L)
                .withActive(TRUE, TRUE, TRUE)
                .withReason("Not available", "Conflict of interest", "Not my area of expertise")
                .withPriority(1, 2, 3)
                .build(3);

        List<RejectionReason> found = repository.findAll();

        assertEquals(expected, found);
    }

    @Test
    public void findByActiveTrueOrderByPriorityAsc() throws Exception {
        repository.deleteAll();

        List<RejectionReason> saved = newRejectionReason()
                .withId(1L, 2L, 3L, 4L)
                .withActive(TRUE, TRUE, TRUE, FALSE)
                .withReason("Visible 3", "Visible 1", "Visible 2", "Hidden")
                .withPriority(3, 1, 2, 4)
                .build(4).stream().map(rejectionReason -> repository.save(rejectionReason)).collect(toList());

        List<RejectionReason> expected = asList(saved.get(1), saved.get(2), saved.get(0));

        List<RejectionReason> found = repository.findByActiveTrueOrderByPriorityAsc();

        assertEquals(expected, found);
    }
}