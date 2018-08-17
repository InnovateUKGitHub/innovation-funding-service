package org.innovateuk.ifs.competition.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue("COMPETITION_STAKEHOLDER")
public class StakeholderInvite extends CompetitionInvite<StakeholderInvite> implements Serializable {
}
