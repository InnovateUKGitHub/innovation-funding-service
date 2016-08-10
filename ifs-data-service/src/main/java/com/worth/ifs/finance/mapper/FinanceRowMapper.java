package com.worth.ifs.finance.mapper;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.resource.FinanceRowResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ApplicationFinanceMapper.class,
                QuestionMapper.class,
                FinanceRowMetaValueMapper.class
        }
)
public abstract class FinanceRowMapper extends BaseMapper<FinanceRow, FinanceRowResource, Long> {

        public Long mapFinanceRowToId(FinanceRow object) {
                if (object == null) {
                        return null;
                }
                return object.getId();
        }

}
