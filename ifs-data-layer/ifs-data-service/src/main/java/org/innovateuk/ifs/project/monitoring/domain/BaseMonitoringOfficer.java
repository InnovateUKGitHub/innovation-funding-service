package org.innovateuk.ifs.project.monitoring.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.Entity;


//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = MonitoringOfficer.class, name = "monitoring_officer"),
//        @JsonSubTypes.Type(value = AccMonitoringOfficer.class, name = "acc_monitoring_officer"),
//})
@Entity
public abstract class BaseMonitoringOfficer extends ProjectParticipant {

    public BaseMonitoringOfficer(ProjectParticipantRole projectParticipantRole) {
        super(null, projectParticipantRole);
    }

    public BaseMonitoringOfficer(User user, ProjectParticipantRole projectParticipantRole) {
        super(user, projectParticipantRole);
    }

}
