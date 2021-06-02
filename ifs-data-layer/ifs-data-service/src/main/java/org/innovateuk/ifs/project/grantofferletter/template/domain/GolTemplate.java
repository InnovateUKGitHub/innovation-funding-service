package org.innovateuk.ifs.project.grantofferletter.template.domain;

import org.innovateuk.ifs.competition.domain.VersionedTemplate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GOL")
public class GolTemplate extends VersionedTemplate {
}
