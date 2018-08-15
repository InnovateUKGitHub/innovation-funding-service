package org.innovateuk.ifs.finance.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FinanceRowMetaFieldTest {
    Long id;
    String title;
    String type;
    FinanceRowMetaField financeRowMetaField;

    @Before
    public void setUp() throws Exception {
        title = "field title";
        type = "field type";
        financeRowMetaField = new FinanceRowMetaField(title, type);
    }

    @Test
    public void constructorsShouldCreateInstanceOnValidInput() throws Exception{
        new FinanceRowMetaField();
        new FinanceRowMetaField("title", "Type");
    }

    @Test
    public void gettersShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(financeRowMetaField.getId(), id);
        Assert.assertEquals(financeRowMetaField.getTitle(), title);
        Assert.assertEquals(financeRowMetaField.getType(), type);
    }
}
