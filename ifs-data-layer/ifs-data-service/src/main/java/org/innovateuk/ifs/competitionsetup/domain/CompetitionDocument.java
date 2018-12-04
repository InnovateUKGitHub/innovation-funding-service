package org.innovateuk.ifs.competitionsetup.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileType;

import javax.persistence.*;
import java.util.List;

@Entity
public class CompetitionDocument extends DocumentConfig {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    public Competition competition;

    public CompetitionDocument(Competition competition, String title, String guidance,
                               boolean editable, boolean enabled, List<FileType> fileTypes) {
        super(title, guidance, editable, enabled, fileTypes);
        this.competition = competition;
    }

    public CompetitionDocument() {
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
