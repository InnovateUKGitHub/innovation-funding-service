package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.EmployeesAndTurnoverResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for EmployeesAndTurnoverResource entities.
 */
public class EmployeesAndTurnoverResourceBuilder extends BaseBuilder<EmployeesAndTurnoverResource, EmployeesAndTurnoverResourceBuilder> {

    public EmployeesAndTurnoverResourceBuilder withTurnover(BigDecimal... turnovers) {
        return withArray((turnover, employeesAndTurnoverResource) -> employeesAndTurnoverResource.setTurnover(turnover), turnovers);
    }
    public EmployeesAndTurnoverResourceBuilder withEmployees(Long... employees) {
        return withArray((employee, employeesAndTurnoverResource) -> employeesAndTurnoverResource.setEmployees(employee), employees);
    }
    private EmployeesAndTurnoverResourceBuilder(List<BiConsumer<Integer, EmployeesAndTurnoverResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static EmployeesAndTurnoverResourceBuilder newEmployeesAndTurnoverResource() {
        return new EmployeesAndTurnoverResourceBuilder(emptyList());
    }

    @Override
    protected EmployeesAndTurnoverResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EmployeesAndTurnoverResource>> actions) {
        return new EmployeesAndTurnoverResourceBuilder(actions);
    }

    @Override
    protected EmployeesAndTurnoverResource createInitial() {
        return new EmployeesAndTurnoverResource();
    }
}