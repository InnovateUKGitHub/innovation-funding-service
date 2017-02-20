package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.function.Function;

@Mapper(
        config = GlobalMapperConfig.class
)
public class ApplicationCountSummaryPageMapper extends PageResourceMapper<ApplicationStatistics, ApplicationCountSummaryResource> {

    @Autowired
    private ApplicationCountSummaryMapper applicationCountSummaryMapper;

    public ApplicationCountSummaryPageResource mapToResource(Page<ApplicationStatistics> source) {
        ApplicationCountSummaryPageResource result = new ApplicationCountSummaryPageResource();
        return mapFields(source, result);
    }

    @Override
    protected Function<ApplicationStatistics, ApplicationCountSummaryResource> contentElementConverter() {
        return applicationCountSummaryMapper::mapToResource;
    }
}
