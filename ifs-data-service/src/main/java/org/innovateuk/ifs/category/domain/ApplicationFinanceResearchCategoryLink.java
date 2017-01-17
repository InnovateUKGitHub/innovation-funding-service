package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Links a {@link ApplicationFinance} to an {@link ResearchCategory}.
 */
@Entity
@DiscriminatorValue("org.innovateuk.ifs.finance.domain.ApplicationFinance")
public class ApplicationFinanceResearchCategoryLink extends CategoryLink<ApplicationFinance, ResearchCategory> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_pk", referencedColumnName = "id")
    private ApplicationFinance applicationFinance;

    ApplicationFinanceResearchCategoryLink() {
        // default constructor
    }

    public ApplicationFinanceResearchCategoryLink(ApplicationFinance applicationFinance, ResearchCategory researchCategory) {

        super(researchCategory);
        if (applicationFinance == null) {
            throw new NullPointerException("Application Finance cannot be null");
        }
        this.applicationFinance = applicationFinance;
    }

    @Override
    public ApplicationFinance getEntity() {
        return applicationFinance;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationFinanceResearchCategoryLink that = (ApplicationFinanceResearchCategoryLink) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(applicationFinance, that.applicationFinance)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(applicationFinance)
                .toHashCode();
    }
}