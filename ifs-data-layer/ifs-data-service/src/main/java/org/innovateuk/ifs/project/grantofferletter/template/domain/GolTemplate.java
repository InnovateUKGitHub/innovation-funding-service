package org.innovateuk.ifs.project.grantofferletter.template.domain;

import org.innovateuk.ifs.competition.domain.TermsAndConditions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GOL")
public class GolTemplate extends TermsAndConditions {
}
