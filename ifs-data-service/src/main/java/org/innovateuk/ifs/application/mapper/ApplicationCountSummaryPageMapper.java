package org.innovateuk.ifs.application.mapper;

import org.aspectj.weaver.ast.Or;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Organisation;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Mapper(
        config = GlobalMapperConfig.class
)
public class ApplicationCountSummaryPageMapper extends PageResourceMapper<ApplicationStatistics, ApplicationCountSummaryResource> {

    @Autowired
    private ApplicationCountSummaryMapper applicationCountSummaryMapper;

    private List<Organisation> organisations;

    public ApplicationCountSummaryPageResource mapToResource(Page<ApplicationStatistics> source, List<Organisation> organisations) {
        this.organisations = organisations;
        ApplicationCountSummaryPageResource result = new ApplicationCountSummaryPageResource();
        return mapFields(source, result);
    }

    @Override
    protected Function<ApplicationStatistics, ApplicationCountSummaryResource> contentElementConverter() {
        return as -> {
            ApplicationCountSummaryResource resource = applicationCountSummaryMapper.mapToResource(as);
            resource.setLeadOrganisation(
                    organisations.stream()
                    .filter(org -> org.getId().equals(as.getLeadOrganisationId()))
                    .findFirst()
                    .map(Organisation::getName)
                    .orElse("")
            );
            return resource;
        };
    }
}
