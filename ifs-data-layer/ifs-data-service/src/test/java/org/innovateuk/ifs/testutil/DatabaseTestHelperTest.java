package org.innovateuk.ifs.testutil;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests to ensure that {@link DatabaseTestHelper} is successfully able to identify changes to the database when they
 * occur, and is successfully able to identify when no changes have occurred as well.
 */
public class DatabaseTestHelperTest extends BaseRepositoryIntegrationTest<OrganisationRepository> {

    @Autowired
    private DatabaseTestHelper databaseTestHelper;

    @Test
    public void assertingNoDatabaseChangesOccur() {

        databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

            // make no database updates, and this should all pass
        });
    }

    @Test
    public void assertingNoDatabaseChangesOccurFailsWhenChangesOccur() {
        final String organisationName = "DB Change Organisation";

        try {
            databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                // cause the database to change, thus making the assertingNoDatabaseChangesOccur() wrapper fail
                repository.save(new Organisation(organisationName));
            });
        } finally {
            // and clean up after ourselves
            repository.deleteAll(repository.findByNameOrderById(organisationName));
        }
    }

    @Override
    @Autowired
    protected void setRepository(OrganisationRepository repository) {
        this.repository = repository;
    }
}