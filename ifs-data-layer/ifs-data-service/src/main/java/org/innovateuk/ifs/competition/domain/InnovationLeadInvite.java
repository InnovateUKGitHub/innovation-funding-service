package org.innovateuk.ifs.competition.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue("COMPETITION_INNOVATION_LEAD")
public class InnovationLeadInvite extends CompetitionInvite<InnovationLeadInvite> implements Serializable {
}