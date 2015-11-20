package com.worth.ifs.security;


import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomPermissionEvaluatorTest {

    private CustomPermissionEvaluator permissionEvaluator = new CustomPermissionEvaluator();
    private List<Object> rulesBeans1 = new ArrayList<>();
    private List<Object> rulesBeans2 = new ArrayList<>();

    {

        rulesBeans1.add(new Object() {
            @PermissionRule("Read")
            public boolean hasPermission1(String dto, User user) {
                return true;
            }
        });

        rulesBeans2.add(new Object() {
            @PermissionRule("Read")
            public boolean hasPermission1(String dto, User user) {
                return true;
            }

            @PermissionRule("Read")
            public boolean hasPermission2(String dto, User user) {
                return true;
            }

            @PermissionRule("Write")
            public boolean hasPermission3(String dto, User user) {
                return true;
            }

            @PermissionRule("Read")
            public boolean hasPermission4(Integer dto, User user) {
                return true;
            }

            public boolean hasPermission5(String dto, User user) {
                return true;
            }
        });


    }


    @Test
    public void test_simpleFindRules() {
        // Method under test
        List<Method> rules = permissionEvaluator.findRules(rulesBeans1);
        assertEquals(1, rules.size());
    }

    @Test
    public void test_complexFindRules() {
        // Method under test
        List<Method> rules = permissionEvaluator.findRules(rulesBeans2);
        assertEquals(4, rules.size());
    }

    @Test
    public void test_simpledDtoClassToMethods()
    {
        List<Method> rules = permissionEvaluator.findRules(rulesBeans1);
        // Method under test
        Map<Class<?>, List<Method>> dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);
        assertEquals(1, dtoClassToMethods.size());
        assertEquals(1, dtoClassToMethods.get(String.class).size());
    }

    @Test
    public void test_complexDtoClassToMethods()
    {
        List<Method> rules = permissionEvaluator.findRules(rulesBeans2);
        // Method under test
        Map<Class<?>, List<Method>> dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);
        assertEquals(2, dtoClassToMethods.size());
        assertEquals(3, dtoClassToMethods.get(String.class).size());
        assertEquals(1, dtoClassToMethods.get(Integer.class).size());
    }

    @Test
    public void test_simpleDtoClassToPermissionToMethods()
    {
        List<Method> rules = permissionEvaluator.findRules(rulesBeans1);
        Map<Class<?>, List<Method>> dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);

        // Method under test
        Map<Class<?>, Map<String, List<Method>>> dtoClassToPermissionToMethods = permissionEvaluator.dtoClassToPermissionToMethods(dtoClassToMethods);
        assertEquals(1, dtoClassToPermissionToMethods.size());
        assertNotNull(dtoClassToPermissionToMethods.get(String.class));
        assertEquals(1, dtoClassToPermissionToMethods.get(String.class).size());
        assertNotNull(dtoClassToPermissionToMethods.get(String.class).get("Read"));
        assertEquals(1, dtoClassToPermissionToMethods.get(String.class).get("Read").size());
    }

    @Test
    public void test_complexDtoClassToPermissionToMethods()
    {
        List<Method> rules = permissionEvaluator.findRules(rulesBeans2);
        Map<Class<?>, List<Method>> dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);

        // Method under test
        Map<Class<?>, Map<String, List<Method>>> dtoClassToPermissionToMethods = permissionEvaluator.dtoClassToPermissionToMethods(dtoClassToMethods);

        assertEquals(2, dtoClassToPermissionToMethods.size());

        assertNotNull(dtoClassToPermissionToMethods.get(String.class));
        assertEquals(2, dtoClassToPermissionToMethods.get(String.class).size());
        assertNotNull(dtoClassToPermissionToMethods.get(String.class).get("Read"));
        assertEquals(2, dtoClassToPermissionToMethods.get(String.class).get("Read").size());
        assertNotNull(dtoClassToPermissionToMethods.get(String.class).get("Write"));
        assertEquals(1, dtoClassToPermissionToMethods.get(String.class).get("Write").size());


        assertNotNull(dtoClassToPermissionToMethods.get(Integer.class));
        assertEquals(1, dtoClassToPermissionToMethods.get(Integer.class).size());
        assertNotNull(dtoClassToPermissionToMethods.get(Integer.class).get("Read"));
        assertEquals(1, dtoClassToPermissionToMethods.get(Integer.class).get("Read").size());
    }


}


