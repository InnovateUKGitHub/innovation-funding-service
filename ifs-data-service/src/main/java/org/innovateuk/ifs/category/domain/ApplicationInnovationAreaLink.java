package org.innovateuk.ifs.category.domain;

import org.innovateuk.ifs.application.domain.Application;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Links a {@link Application} to an {@link InnovationArea}.
 */
@Entity
@DiscriminatorValue("org.innovateuk.ifs.application.domain.Application#innovationArea")
public class ApplicationInnovationAreaLink extends CategoryLink<Application, InnovationArea> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private Application application;

    ApplicationInnovationAreaLink() { }

    public ApplicationInnovationAreaLink(Application application, InnovationArea category) {
        super(category);

        if (application == null) {
            throw new NullPointerException("application cannot be null");
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
        if (!super.equals(o)) return false;

        ApplicationInnovationAreaLink that = (ApplicationInnovationAreaLink) o;

        return application != null ? application.equals(that.application) : that.application == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (application != null ? application.hashCode() : 0);
        return result;
    }
}
