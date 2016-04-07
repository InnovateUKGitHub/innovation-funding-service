package com.worth.ifs.finance.mapper;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
            CostValueMapper.class,
            ApplicationFinanceMapper.class,
            QuestionMapper.class
        }

)
public abstract class CostMapper extends BaseMapper<Cost, Long, Long> {

    public Long map(Cost object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    abstract List<Long> costListToLongList(List<Cost> costs);
}
