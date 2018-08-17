package org.innovateuk.ifs.competitionsetup.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileType;

import javax.persistence.*;
import java.util.List;

@Entity
public class ProjectDocument extends DocumentConfig {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    public Competition competition;

    public ProjectDocument(Competition competition, String title, String guidance,
                           boolean editable, boolean enabled, List<FileType> fileTypes) {
        super(title, guidance, editable, enabled, fileTypes);
        this.competition = competition;
    }

    public ProjectDocument() {
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
