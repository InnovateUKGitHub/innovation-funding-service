package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.FinanceRowResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectFinanceMapper.class,
                QuestionMapper.class,
                FinanceRowMetaValueMapper.class
        }
)
public abstract class ProjectFinanceRowMapper extends BaseMapper<ProjectFinanceRow, FinanceRowResource, Long> {

        public Long mapFinanceRowToId(FinanceRow object) {
                if (object == null) {
                        return null;
                }
                return object.getId();
        }

        @Mappings({
                @Mapping(source = "applicationRowId", target = "linkedFinanceRow")
        })
        @Override
        public abstract FinanceRowResource mapToResource(ProjectFinanceRow domain);

        @Mappings({
                @Mapping(target = "question", ignore = true),
                @Mapping(target = "financeRowMetadata", ignore = true),
                @Mapping(target = "target", ignore = true),
                @Mapping(source = "linkedFinanceRow", target = "applicationRowId")
        })
        @Override
        public abstract ProjectFinanceRow mapToDomain(FinanceRowResource resource);
}
