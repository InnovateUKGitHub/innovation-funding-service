package org.innovateuk.ifs.schedule.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ScheduleStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;

    @CreatedDate
    private ZonedDateTime createdOn;

    ScheduleStatus() {}

    public ScheduleStatus(String jobName) {
        this.jobName = jobName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }
}
