package org.innovateuk.ifs.competition.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GRANT")
public class GrantTermsAndConditions extends VersionedTemplate {
}
