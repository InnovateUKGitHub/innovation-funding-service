package com.worth.ifs.finance.mapper;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.resource.FinanceRowResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ApplicationFinanceMapper.class,
                QuestionMapper.class,
                FinanceRowMetaValueMapper.class
        }
)
public abstract class ApplicationFinanceRowMapper extends BaseMapper<ApplicationFinanceRow, FinanceRowResource, Long> {

        public Long mapFinanceRowToId(FinanceRow object) {
                if (object == null) {
                        return null;
                }
                return object.getId();
        }

        @Mappings({
                @Mapping(target = "question", ignore = true),
                @Mapping(target = "costValues", ignore = true),
                @Mapping(target = "target", ignore = true)
        })
        @Override
        public abstract ApplicationFinanceRow mapToDomain(FinanceRowResource resource);
}
