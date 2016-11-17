package com.worth.ifs.finance.domain;

import com.worth.ifs.application.domain.Question;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class FinanceRowTest {
    FinanceRow costItem;
    Long id;
    String item;
    String name;
    String description;
    Integer quantity;
    BigDecimal cost;
    ApplicationFinance applicationFinance;
    Question question;
    FinanceType financeType;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        item = "cost item";
        name = "labour";
        description = "description of cost item";
        quantity = 10;
        cost = new BigDecimal(1000);
        applicationFinance = new ApplicationFinance();
        question = new Question();
        financeType = new FinanceType();
        costItem = new ApplicationFinanceRow(id, name, item, description, quantity, cost, applicationFinance, question);
    }

    @Test
    public void costShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(costItem.getId(), id);
        Assert.assertEquals(costItem.getName(), name);
        Assert.assertEquals(costItem.getItem(), item);
        Assert.assertEquals(costItem.getDescription(), description);
        Assert.assertEquals(costItem.getQuantity(), quantity);
        Assert.assertEquals(costItem.getCost(), cost);
        //TODO: mock getCostValues()
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() throws Exception {
        BigDecimal value = new BigDecimal(2.2);
        new ApplicationFinanceRow();
        new ApplicationFinanceRow(19274617892346L, "key", "item2","description2",1,value,applicationFinance, question);
    }

    @Test
    public void costShouldReturnCorrectAttributeValuesAfterSetters() throws Exception {
        String item2 = "cost item";
        String name2 = "labour";
        String description2 = "description of cost item";
        Integer quantity2 = 10;
        BigDecimal cost2 = new BigDecimal(2000);
        Question question2 = new Question();
        ApplicationFinance applicationFinance2 = new ApplicationFinance();

        costItem.setItem(item2);
        costItem.setName(name2);
        costItem.setDescription(description2);
        costItem.setQuantity(quantity2);
        costItem.setCost(cost2);
        costItem.setQuestion(question2);
        costItem.setTarget(applicationFinance2);

        Assert.assertEquals(costItem.getItem(), item2);
        Assert.assertEquals(costItem.getName(), name2);
        Assert.assertEquals(costItem.getDescription(), description2);
        Assert.assertEquals(costItem.getQuantity(), quantity2);
        Assert.assertEquals(costItem.getCost(), cost2);
        Assert.assertEquals(costItem.getQuestion(),question2);
    }
}