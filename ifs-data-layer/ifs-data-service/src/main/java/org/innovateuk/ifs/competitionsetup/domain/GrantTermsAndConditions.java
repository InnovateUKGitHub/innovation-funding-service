package org.innovateuk.ifs.competitionsetup.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GRANT")
public class GrantTermsAndConditions extends TermsAndConditions {
}
