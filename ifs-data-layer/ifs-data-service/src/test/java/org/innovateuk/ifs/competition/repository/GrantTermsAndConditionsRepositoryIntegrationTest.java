package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsBuilder.newGrantTermsAndConditions;

public class GrantTermsAndConditionsRepositoryIntegrationTest extends
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

    @Test
    public void findLatestVersions() {

        List<GrantTermsAndConditions> grantTermsAndConditionsList = newGrantTermsAndConditions()
                .with(id(null))
                .withName(
                        "name-1", "name-1", "name-1",
                        "name-2", "name-2", "name-2",
                        "name-3", "name-3", "name-3"
                )
                .withTemplate(
                        "template-1-v1", "template-1-v2", "template-1-v3",
                        "template-2-v1", "template-2-v2", "template-2-v3",
                        "template-3-v1", "template-3-v2", "template-3-v3"
                )
                .withVersion(
                        1, 2, 3,
                        1, 2, 3,
                        1, 2, 3
                )
                .build(9)
                .stream()
                .map(termsAndConditions -> repository.save(termsAndConditions))
                .collect(toList());

        // Get the latest versions and filter out any that were not created as part of this test
        List<GrantTermsAndConditions> latestVersions = repository.findLatestVersions().stream().filter(
                termsAndConditions -> termsAndConditions.getName().startsWith("name-")).collect(toList());

        assertThat(latestVersions).containsExactlyInAnyOrder(grantTermsAndConditionsList.get(2),
                grantTermsAndConditionsList.get(5), grantTermsAndConditionsList.get(8));

    }

}
