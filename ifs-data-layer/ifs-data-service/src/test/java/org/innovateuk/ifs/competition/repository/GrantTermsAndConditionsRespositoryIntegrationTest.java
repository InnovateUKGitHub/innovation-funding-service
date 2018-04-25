package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsBuilder.newGrantTermsAndConditions;

public class GrantTermsAndConditionsRespositoryIntegrationTest extends
        BaseRepositoryIntegrationTest<GrantTermsAndConditionsRepository> {

    @Autowired
    @Override
    protected void setRepository(GrantTermsAndConditionsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findOneByTemplate() {
        loginCompAdmin();

        GrantTermsAndConditions grantTermsAndConditions = newGrantTermsAndConditions()
                .with(id(null))
                .withName("test terms and conditions")
                .withTemplate("test-terms-and-conditions")
                .withVersion(1)
                .build();

        repository.save(grantTermsAndConditions);

        assertThat(repository.findOneByTemplate("test-terms-and-conditions"))
                .isEqualToComparingOnlyGivenFields(grantTermsAndConditions,
                        "name", "template", "version");
    }

}
