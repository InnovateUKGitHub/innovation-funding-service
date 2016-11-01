package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;

/**
 * TODO DW - document this class
 */
public class ApplicationData {

    private CompetitionResource competition;
    private ApplicationResource application;

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public ApplicationResource getApplication() {
        return application;
    }
}
