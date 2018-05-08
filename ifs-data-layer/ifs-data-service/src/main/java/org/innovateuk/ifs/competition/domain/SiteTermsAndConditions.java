package org.innovateuk.ifs.competition.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SITE")
public class SiteTermsAndConditions extends TermsAndConditions {
}
