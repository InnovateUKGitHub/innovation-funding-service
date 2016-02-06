package com.worth.ifs.finance.transactional;

import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.domain.CostValueId;
import com.worth.ifs.security.NotSecured;

public interface CostValueService {
    @NotSecured("TODO")
    CostValue findOne(CostValueId id);
}