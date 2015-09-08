package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.application.finance.cost.LabourCost;
import com.worth.ifs.domain.Cost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LabourCostCategory implements CostCategory {
    public static final String WORKING_DAYS_PER_YEAR = "Working days per year";
    private Integer workingDaysPerYear = 0;
    private final Log log = LogFactory.getLog(getClass());

    List<CostItem> costs = new ArrayList<>();
    Double total = 0D;

    @Override
    public List<CostItem> getCosts() {
        return costs;
    }

    @Override
    public void saveCosts(HttpServletRequest request, CostType costType) {
        List<String> labourKeys = request.getParameterMap().keySet().stream().
                filter(k -> k.startsWith(CostType.LABOUR.getType())).collect(Collectors.toList());
    }

    public void update(HttpServletRequest request, CostItem cost) {
        List<String> labourKeys = request.getParameterMap().keySet().stream().
                filter(k -> k.startsWith(CostType.LABOUR.getType()) && k.endsWith(String.valueOf(cost.getId()))).collect(Collectors.toList());
        for(String labourKey : labourKeys) {
            String value = request.getParameter(labourKey);
            String[] keyParts = labourKey.split("-");
            String field = keyParts[1];
        }
    }

    @Override
    public Double getTotal() {
        total = costs.stream().mapToDouble(c -> ((LabourCost)c).getTotal(workingDaysPerYear)).sum();
        return total;
    }

    public Integer getWorkingDaysPerYear() {
        return workingDaysPerYear;
    }

    @Override
    public void addCost(CostItem costItem) {
        LabourCost labourCost = (LabourCost) costItem;
        if(labourCost.getDescription().equals(WORKING_DAYS_PER_YEAR)) {
            workingDaysPerYear = ((LabourCost) costItem).getLabourDays();
        }
        else if(costItem!=null){
            costs.add(costItem);
        }
    }


}
