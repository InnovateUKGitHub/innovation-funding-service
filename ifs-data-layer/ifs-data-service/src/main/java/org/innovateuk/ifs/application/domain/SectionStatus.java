package org.innovateuk.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class SectionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean markedAsComplete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "markedAsCompleteById", referencedColumnName = "id")
    private ProcessRole markedAsCompleteBy;

    private ZonedDateTime markedAsCompleteOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionId", referencedColumnName = "id")
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    SectionStatus() {
    }

    public SectionStatus(Application application, Section section) {
        this.application = application;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public boolean isMarkedAsComplete() {
        return markedAsComplete;
    }

    public ProcessRole getMarkedAsCompleteBy() {
        return markedAsCompleteBy;
    }

    public SectionStatus markAsComplete(ProcessRole markedAsCompleteBy, ZonedDateTime markedAsCompleteOn) {
        this.markedAsComplete = true;
        this.markedAsCompleteBy = markedAsCompleteBy;
        this.markedAsCompleteOn = markedAsCompleteOn;
        return this;
    }

    public void markAsInComplete() {
        this.markedAsComplete = false;
    }

    public void setApplication(Application application) {
        this.application = application;
    }


    @JsonIgnore
    public Section getSection() {
        return this.section;
    }

    @JsonIgnore
    public Application getApplication() {
        return this.application;
    }

    public ZonedDateTime getMarkedAsCompleteOn() {
        return markedAsCompleteOn;
    }
}