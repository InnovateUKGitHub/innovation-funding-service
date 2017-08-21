package org.innovateuk.ifs.testdata;

import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Ignore;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.testdata.builders.BaseDataBuilder.COMP_ADMIN_EMAIL;

/**
 * Generates web test data based upon csvs in /src/test/resources/testdata using data builders
 */
@Ignore
public class GenerateTestData extends BaseGenerateTestData {

    @Override
    protected boolean cleanDbFirst() {
        return true;
    }

    /**
     * We might need to fix up the database before we start generating data.
     * This can happen if for example we have pushed something out to live that would make this generation step fail.
     * Note that if we make a fix is made here it will most likely need a corresponding fix in sql script
     * VX_Y_Z__Remove_old_competition.sql
     * To repeat the fix when running up the full flyway mechanism
     */
    @Override
    public void fixUpDatabase() {
        // Remove the public content that is in place for competition one so that generation does not fail with
        // PUBLIC_CONTENT_ALREADY_INITIALISED
        PublicContent publicContentForCompetitionOne = publicContentRepository.findByCompetitionId(1L);
        publicContentRepository.delete(publicContentForCompetitionOne.getId());
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource systemRegistrar() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(UserRoleType.SYSTEM_REGISTRATION_USER).build(1)).build();
    }
}
