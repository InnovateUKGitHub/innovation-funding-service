package org.innovateuk.ifs.testdata;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.testdata.builders.BaseDataBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Generates web test data based upon csvs in /src/test/resources/testdata using data builders
 */
@ActiveProfiles({"integration-test,seeding-db"})
@DirtiesContext
@Ignore
public class ApplyTestDataToProductionData extends BaseGenerateTestData {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected boolean cleanDbFirst() {
        return false;
    }

    @Override
    public void fixUpDatabase() {
        Competition competition = competitionRepository.findByName("Connected digital additive manufacturing").get(0);
        competition.setName("Connected digital additive manufacturing PRODUCTION");
        competitionRepository.save(competition);

        User systemRegistrar = userRepository.findByEmail("ifxxxxxxxxxxxxxxxxxxxxxxxxxx15@inxx.example.com").get();
        systemRegistrar.setEmail(BaseDataBuilder.IFS_SYSTEM_MAINTENANCE_USER_EMAIL);
        userRepository.save(systemRegistrar);
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
