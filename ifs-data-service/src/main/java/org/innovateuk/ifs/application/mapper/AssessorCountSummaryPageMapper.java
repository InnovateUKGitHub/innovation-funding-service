package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.AssessorStatistics;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.function.Function;

@Mapper(
        config = GlobalMapperConfig.class
)
public class AssessorCountSummaryPageMapper extends PageResourceMapper<AssessorStatistics, AssessorCountSummaryResource> {

    @Autowired
    private AssessorCountSummaryMapper assessorCountSummaryMapper;

    public AssessorCountSummaryPageResource mapToResource(Page<AssessorStatistics> source) {
        AssessorCountSummaryPageResource result = new AssessorCountSummaryPageResource();
        return mapFields(source, result);
    }

    @Override
    protected Function<AssessorStatistics, AssessorCountSummaryResource> contentElementConverter() {
        return as -> {
            AssessorCountSummaryResource resource = assessorCountSummaryMapper.mapToResource(as);
            return resource;
        };
    }
}
