package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Links a {@link Application} to an {@link ResearchCategory}.
 */
@Entity
@DiscriminatorValue("org.innovateuk.ifs.application.domain.Application#researchCategory")
public class ApplicationResearchCategoryLink extends CategoryLink<Application, ResearchCategory> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private Application application;

    ApplicationResearchCategoryLink() {
    }

    public ApplicationResearchCategoryLink(Application application, ResearchCategory researchCategory) {

        super(researchCategory);
        if (application == null) {
            throw new NullPointerException("Application cannot be null");
        }
        this.application = application;
    }

    @Override
    public Application getEntity() {
        return application;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationResearchCategoryLink that = (ApplicationResearchCategoryLink) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(application, that.application)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(application)
                .toHashCode();
    }
}