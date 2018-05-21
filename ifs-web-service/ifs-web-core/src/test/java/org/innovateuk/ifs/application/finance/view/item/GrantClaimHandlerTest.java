package org.innovateuk.ifs.application.finance.view.item;

import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Test;

import java.math.BigDecimal;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class GrantClaimHandlerTest {

    private GrantClaimHandler handler = new GrantClaimHandler();

    @Test
    public void testHappyPath() {

        String value = "1000";

        FinanceFormField field = new FinanceFormField("field1", value, "field-1-id", "456", "grant-claim", "key-type");
        FinanceRowItem item = handler.toFinanceRowItem(123L, singletonList(field));

        assertThat(item.getCostType()).isEqualTo(FinanceRowType.FINANCE);
        assertThat(item.getId()).isEqualTo(123L);
        assertThat(item.getMinRows()).isEqualTo(0);
        assertThat(item.getName()).isEqualTo(FinanceRowType.FINANCE.getType());
        assertThat(item.getTotal()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    public void testTooManyDigitsTruncatesInputToNineDigits() {

        String value = "123456789012";

        FinanceFormField field = new FinanceFormField("field1", value, "field-1-id", "456", "grant-claim", "key-type");
        FinanceRowItem item = handler.toFinanceRowItem(123L, singletonList(field));

        assertThat(item.getCostType()).isEqualTo(FinanceRowType.FINANCE);
        assertThat(item.getId()).isEqualTo(123L);
        assertThat(item.getMinRows()).isEqualTo(0);
        assertThat(item.getName()).isEqualTo(FinanceRowType.FINANCE.getType());
        assertThat(item.getTotal()).isEqualTo(BigDecimal.valueOf(123456789));
    }

    @Test
    public void testInvalidCharactersAndSpacesFilteredOut() {

        String value = " 1s2d3 f4a ";

        FinanceFormField field = new FinanceFormField("field1", value, "field-1-id", "456", "grant-claim", "key-type");
        FinanceRowItem item = handler.toFinanceRowItem(123L, singletonList(field));

        assertThat(item.getCostType()).isEqualTo(FinanceRowType.FINANCE);
        assertThat(item.getId()).isEqualTo(123L);
        assertThat(item.getMinRows()).isEqualTo(0);
        assertThat(item.getName()).isEqualTo(FinanceRowType.FINANCE.getType());
        assertThat(item.getTotal()).isEqualTo(BigDecimal.valueOf(1234));
    }
}
