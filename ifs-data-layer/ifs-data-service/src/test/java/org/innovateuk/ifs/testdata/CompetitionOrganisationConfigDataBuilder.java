package org.innovateuk.ifs.testdata;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.testdata.builders.BaseDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data from Competition Funders and attaches it to a competition
 */
public class CompetitionOrganisationConfigDataBuilder extends BaseDataBuilder<Void, CompetitionOrganisationConfigDataBuilder>{

    private static final Logger LOG = LoggerFactory.getLogger(CompetitionOrganisationConfigDataBuilder.class);

    public CompetitionOrganisationConfigDataBuilder withCompetitionOrganisationConfigData(String competitionName, boolean internationalOrganisation, boolean internationalLeadOrganisationAllowed) {
        return with(data -> {

            testService.doWithinTransaction(() -> {
                Competition competition = retrieveCompetitionByName(competitionName);

                CompetitionOrganisationConfig competitionOrganisationConfig;
                if (competition.getCompetitionOrganisationConfig() != null) {
                    competitionOrganisationConfig = competition.getCompetitionOrganisationConfig();
                } else {
                    competitionOrganisationConfig = new CompetitionOrganisationConfig();
                    competition.setCompetitionOrganisationConfig(competitionOrganisationConfig);
                    competitionOrganisationConfig.setCompetition(competition);
                }
                competitionOrganisationConfig.setInternationalOrganisationsAllowed(internationalOrganisation);
                competitionOrganisationConfig.setInternationalLeadOrganisationAllowed(internationalLeadOrganisationAllowed);
                competitionOrganisationConfigRepository.save(competitionOrganisationConfig);
            });
        });
    }

    public static CompetitionOrganisationConfigDataBuilder newCompetitionConfigData(ServiceLocator serviceLocator) {
        return new CompetitionOrganisationConfigDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private CompetitionOrganisationConfigDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                                     ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected CompetitionOrganisationConfigDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new CompetitionOrganisationConfigDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }

    @Override
    protected void postProcess(int index, Void instance) {
        super.postProcess(index, instance);
        LOG.info("Created Competition Funder");
    }
}
