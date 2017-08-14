package org.innovateuk.ifs.testdata;

import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Generates web test data based upon csvs in /src/test/resources/testdata using data builders
 */
@ActiveProfiles({"integration-test,seeding-db"})
@DirtiesContext
//@Ignore
public class GenerateTestData extends BaseGenerateTestData {

    @Autowired
    private PublicContentRepository publicContentRepository;

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







//    private void createCompetitions() {
//        competitionLines.forEach(line -> {
////            if ("Connected digital additive manufacturing".equals(line.name)) {
////                createCompetitionWithApplications(line, Optional.of(1L));
////            } else {
//            createCompetitionWithApplications(line, Optional.empty());
////            }
//        });
//    }

}
