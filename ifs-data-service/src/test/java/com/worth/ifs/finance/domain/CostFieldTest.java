package com.worth.ifs.finance.domain;

        import org.junit.Assert;
        import org.junit.Before;
        import org.junit.Test;

public class CostFieldTest {
    Long id;
    String title;
    String type;
    CostField costField;

    @Before
    public void setUp() throws Exception {
        id = 123456L;
        title = "field title";
        type = "field type";
        costField = new CostField(id, title, type);
    }

    @Test
    public void gettersShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costField.getId(), id);
        Assert.assertEquals(costField.getTitle(), title);
        Assert.assertEquals(costField.getType(), type);
    }
}