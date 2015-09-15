package com.worth.ifs.application.domain.Process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.tomcat.jni.Proc;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.SourceType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by nunoalexandre on 15/09/15.
 */

@Entity
public class Process implements IProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "involvedId")
    private Long involvedId;

    @Column(name = "targetId")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @Enumerated(EnumType.STRING)
    private ProcessType type;

    @Version
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastModified;


    public Process(){}

    public Process(ProcessType type, ProcessStatus status, Long involved, Long target) {
        this.type = type;
        this.status = status;
        this.involvedId = involved;
        this.targetId = target;
    }

    /** Getters **/

    @Override
    public Long getInvolved() {
        return involvedId;
    }
    @Override
    public Long getTarget() {
        return targetId;
    }
    @Override
    public Long getId() {
        return id;
    }
    @Override
    public ProcessStatus getStatus() {
        return status;
    }
    @Override
    public ProcessType getType() {
        return type;
    }

    @JsonIgnore
    @Override
    public Calendar getVersion() {
        return lastModified;
    }



    /** Setters **/

    @Override
    public void setInvolved(Long involved) {
        this.involvedId = involved;
    }
    @Override
    public void setTarget(Long target) {
        this.targetId = target;
    }

    @Override
    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
    @Override
    public void setType(ProcessType type) {
        this.type = type;
    }


}
