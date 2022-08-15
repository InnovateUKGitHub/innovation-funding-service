package org.innovateuk.ifs.application.domain;

import lombok.*;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceStateConverter;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.repository.ApplicationStateConverter;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApplicationEoiEvidenceProcess extends Process<ProcessRole, ApplicationEoiEvidenceResponse, ApplicationEoiEvidenceState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private ApplicationEoiEvidenceResponse target;

    @Convert(converter = ApplicationEoiEvidenceStateConverter.class)
    @Column(name="activity_state_id")
    private ApplicationEoiEvidenceState processState;

    public ApplicationEoiEvidenceProcess(ProcessRole participant, ApplicationEoiEvidenceResponse target, ApplicationEoiEvidenceState initialState) {
        this.target = target;
        this.participant = participant;
        this.setProcessState(initialState);
    }
}
