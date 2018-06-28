package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.SiteTermsAndConditions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsBuilder.newSiteTermsAndConditions;

public class SiteTermsAndConditionsRepositoryIntegrationTest extends
        BaseRepositoryIntegrationTest<SiteTermsAndConditionsRepository> {

    @Autowired
    @Override
    protected void setRepository(SiteTermsAndConditionsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findTopByOrderByVersionDesc() {
        loginCompAdmin();

        List<SiteTermsAndConditions> siteTermsAndConditions = IntStream.range(1, 3).mapToObj(i ->
                newSiteTermsAndConditions()
                        .with(id(null))
                        .withName("Site terms")
                        .withTemplate("site-terms-v" + i)
                        .withVersion(i)
                        .build())
                .collect(toList());

        repository.saveAll(siteTermsAndConditions);

        assertThat(repository.findTopByOrderByVersionDesc()).isEqualToComparingOnlyGivenFields(siteTermsAndConditions
                .get(siteTermsAndConditions.size() - 1), "name", "template", "version");
    }

}