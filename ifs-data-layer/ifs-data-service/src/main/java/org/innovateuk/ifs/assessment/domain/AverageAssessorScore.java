package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.application.domain.Application;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
public class AverageAssessorScore {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicationId", referencedColumnName = "id")
    private Application application;

    private BigDecimal score;

    AverageAssessorScore() {}

    public AverageAssessorScore(Application application, BigDecimal score) {
        this.application = application;
        this.score = score;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}