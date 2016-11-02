package com.worth.ifs.commons.security;


import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.security.CustomPermissionEvaluator.ListOfOwnerAndMethod;
import com.worth.ifs.commons.security.CustomPermissionEvaluator.PermissionedObjectClassToPermissionsMethods;
import com.worth.ifs.commons.security.CustomPermissionEvaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import com.worth.ifs.commons.security.authentication.user.UserAuthentication;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;

public class CustomPermissionEvaluatorTest extends BaseUnitTestMocksTest {

    @Mock
    private ApplicationContext applicationContextMock;

    @InjectMocks
    private CustomPermissionEvaluator permissionEvaluator = new CustomPermissionEvaluator();

    private UserResource noRightsUser = newUserResource().build();
    private UserResource readOnlyUser = newUserResource().build();
    private UserResource readWriteUser = newUserResource().build();

    private List<UserResource> readUsers = asList(readOnlyUser, readWriteUser);
    private List<UserResource> writeUsers = singletonList(readWriteUser);

    private Object rulesBeans1 = new Object() {
        @PermissionRule("Read")
        public boolean hasPermission1(String dto, UserResource user) {
            return readUsers.contains(user);
        }
    };

    private Object rulesBeans2 = new Object() {
        @PermissionRule("Read")
        public boolean hasPermission1(String dto, UserResource user) {
            return readUsers.contains(user);
        }

        @PermissionRule("Read")
        public boolean hasPermission2(String dto, UserResource user) {
            return readUsers.contains(user);
        }

        @PermissionRule("Write")
        public boolean hasPermission3(String dto, UserResource user) {
            return writeUsers.contains(user);
        }

        @PermissionRule("Read")
        public boolean hasPermission4(Integer dto, UserResource user) {
            return readUsers.contains(user);
        }

        public boolean hasPermission5(String dto, UserResource user) {
            throw new IllegalArgumentException("Should not have called non-annotated method");
        }
    };

    private Object rulesBeans3 = new Object() {
        @PermissionRule("Read")
        public boolean hasPermission1(Long dto, UserResource user) {
            return false;
        }

        @PermissionRule("Read")
        public boolean hasPermission2(Long dto, UserResource user) {
            return false;
        }

        @PermissionRule("Write")
        public boolean hasPermission3(Long dto, UserResource user) {
            return false;
        }

        @PermissionRule("Write")
        public boolean hasPermission4(Long dto, UserResource user) {
            return false;
        }
    };

    private Object userAuthenticationParameterRulesBeans = new Object() {
        @PermissionRule("Read")
        public boolean hasPermission1(Long dto, UserAuthentication user) {
            return false;
        }

        @PermissionRule("Write")
        public boolean hasPermission2(Long dto, UserAuthentication user) {
            return true;
        }
    };

    private Object invalidParameterRulesBeans = new Object() {
        @PermissionRule("Read")
        public boolean hasPermission1(Long dto, Double invalidSecondParameter) {
            return false;
        }
    };

    private Object permissionEntityLookupBeans1 = new Object() {

        @PermissionEntityLookupStrategy
        public String lookupString(Long id) {
            return id != Long.MAX_VALUE ? "A string " + id : null;
        }
    };

    private Object permissionEntityLookupBeans2 = new Object() {

        @PermissionEntityLookupStrategy
        public Long lookupLong(Long id) {
            return id != Long.MAX_VALUE ? 100 + id : null;
        }

        @PermissionEntityLookupStrategy
        public Integer lookupInteger(Long id) {
            return id != Long.MAX_VALUE ? (int) (200 + id) : null;
        }
    };

    private Object duplicatePermissionEntityLookupBeans3 = new Object() {

        @PermissionEntityLookupStrategy
        public Long lookupLong(Long id) {
            return id != Long.MAX_VALUE ? 100 + id : null;
        }
    };

    @Before
    public void setup() {

        List<Object> allPermissionBeans = asList(rulesBeans1, rulesBeans2, rulesBeans3);
        Map<String, Object> allPermissionBeansToMap = allPermissionBeans.stream().collect(Collectors.toMap(bean -> bean.hashCode() + "", identity()));

        List<Object> allPermissionLookupBeans = asList(permissionEntityLookupBeans1, permissionEntityLookupBeans2);
        Map<String, Object> allPermissionLookupBeansToMap = allPermissionLookupBeans.stream().collect(Collectors.toMap(bean -> bean.hashCode() + "", identity()));

        when(applicationContextMock.getBeansWithAnnotation(PermissionRules.class)).thenReturn(allPermissionBeansToMap);
        when(applicationContextMock.getBeansWithAnnotation(PermissionEntityLookupStrategies.class)).thenReturn(allPermissionLookupBeansToMap);
    }


    @Test
    public void test_simpleFindRules() {
        // Method under test
        List<Pair<Object, Method>> rules = permissionEvaluator.findRules(singletonList(rulesBeans1));
        assertEquals(1, rules.size());
    }

    @Test
    public void test_complexFindRules() {
        // Method under test
        List<Pair<Object, Method>> rules = permissionEvaluator.findRules(singletonList(rulesBeans2));
        assertEquals(4, rules.size());
    }

    @Test
    public void test_simpledDtoClassToMethods() {
        List<Pair<Object, Method>> rules = permissionEvaluator.findRules(singletonList(rulesBeans1));
        // Method under test
        PermissionedObjectClassToPermissionsMethods dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);
        assertEquals(1, dtoClassToMethods.size());
        assertEquals(1, dtoClassToMethods.get(String.class).size());
    }

    @Test
    public void test_complexDtoClassToMethods() {
        List<Pair<Object, Method>> rules = permissionEvaluator.findRules(singletonList(rulesBeans2));
        // Method under test
        PermissionedObjectClassToPermissionsMethods dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);
        assertEquals(2, dtoClassToMethods.size());
        assertEquals(3, dtoClassToMethods.get(String.class).size());
        assertEquals(1, dtoClassToMethods.get(Integer.class).size());
    }

    @Test
    public void test_simpleDtoClassToPermissionToMethods() {
        List<Pair<Object, Method>> rules = permissionEvaluator.findRules(singletonList(rulesBeans1));
        PermissionedObjectClassToPermissionsMethods dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);

        // Method under test
        PermissionedObjectClassToPermissionsToPermissionsMethods dtoClassToPermissionToMethods = permissionEvaluator.dtoClassToPermissionToMethods(dtoClassToMethods);
        assertEquals(1, dtoClassToPermissionToMethods.size());
        assertNotNull(dtoClassToPermissionToMethods.get(String.class));
        assertEquals(1, dtoClassToPermissionToMethods.get(String.class).size());
        assertNotNull(dtoClassToPermissionToMethods.get(String.class).get("Read"));
        assertEquals(1, dtoClassToPermissionToMethods.get(String.class).get("Read").size());
    }

    @Test
    public void test_complexDtoClassToPermissionToMethods() {
        List<Pair<Object, Method>> rules = permissionEvaluator.findRules(singletonList(rulesBeans2));
        PermissionedObjectClassToPermissionsMethods dtoClassToMethods = permissionEvaluator.dtoClassToMethods(rules);

        // Method under test
        PermissionedObjectClassToPermissionsToPermissionsMethods dtoClassToPermissionToMethods = permissionEvaluator.dtoClassToPermissionToMethods(dtoClassToMethods);

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

    @Test
    public void test_generateRules() {

        permissionEvaluator.generateRules();

        Map<Class<?>, Map<String, List<Pair<Object, Method>>>> rulesMap = getRulesMap();

        // test that we have picked up and set the values for Strings, Integers and Longs
        {
            assertNotNull(rulesMap);
            assertEquals(3, rulesMap.size());
            assertThat(rulesMap.keySet(), containsInAnyOrder(Integer.class, String.class, Long.class));
        }

        // test the rules against Strings
        {
            Map<String, List<Pair<Object, Method>>> rulesByAction = rulesMap.get(String.class);
            assertEquals(2, rulesByAction.size());
            assertThat(rulesByAction.keySet(), containsInAnyOrder("Read", "Write"));

            List<Pair<Object, Method>> readStringRules = rulesByAction.get("Read");
            List<Pair<Object, Method>> writeStringRules = rulesByAction.get("Write");
            assertEquals(3, readStringRules.size());
            assertEquals(1, writeStringRules.size());
        }

        // test the rules against Integers
        {
            Map<String, List<Pair<Object, Method>>> rulesByAction = rulesMap.get(Integer.class);
            assertEquals(1, rulesByAction.size());
            assertThat(rulesByAction.keySet(), containsInAnyOrder("Read"));

            List<Pair<Object, Method>> readStringRules = rulesByAction.get("Read");
            assertEquals(1, readStringRules.size());
        }
    }

    @Test
    public void test_hasPermission() {

        permissionEvaluator.generateRules();

        // assert that the permissions are being applied correctly
        {
            assertFalse(permissionEvaluator.hasPermission(new UserAuthentication(noRightsUser), "A string instance", "Read"));
            assertTrue(permissionEvaluator.hasPermission(new UserAuthentication(readOnlyUser), "A string instance", "Read"));
            assertTrue(permissionEvaluator.hasPermission(new UserAuthentication(readWriteUser), "A string instance", "Read"));

            assertFalse(permissionEvaluator.hasPermission(new UserAuthentication(noRightsUser), "A string instance", "Write"));
            assertFalse(permissionEvaluator.hasPermission(new UserAuthentication(readOnlyUser), "A string instance", "Write"));
            assertTrue(permissionEvaluator.hasPermission(new UserAuthentication(readWriteUser), "A string instance", "Write"));
        }
    }

    @Test
    public void test_hasPermission_withUserAuthenticationParameter() {

        List<Object> allPermissionBeans = singletonList(userAuthenticationParameterRulesBeans);
        Map<String, Object> allPermissionBeansToMap = allPermissionBeans.stream().collect(Collectors.toMap(bean -> bean.hashCode() + "", identity()));
        when(applicationContextMock.getBeansWithAnnotation(PermissionRules.class)).thenReturn(allPermissionBeansToMap);

        permissionEvaluator.generateRules();

        // assert that the permissions are being applied correctly
        {
            assertFalse(permissionEvaluator.hasPermission(new UserAuthentication(noRightsUser), 123L, "Read"));
            assertTrue(permissionEvaluator.hasPermission(new UserAuthentication(noRightsUser), 123L, "Write"));
        }
    }

    @Test
    public void test_hasPermission_invalidSecondParameter() {

        List<Object> allPermissionBeans = singletonList(invalidParameterRulesBeans);
        Map<String, Object> allPermissionBeansToMap = allPermissionBeans.stream().collect(Collectors.toMap(bean -> bean.hashCode() + "", identity()));
        when(applicationContextMock.getBeansWithAnnotation(PermissionRules.class)).thenReturn(allPermissionBeansToMap);

        try {
            permissionEvaluator.generateRules();
            fail("Should've thrown an exception as the 2nd parameter was not a User or an Authentication subclass");
        } catch (IllegalStateException e) {
            // expected bahaviour
        }
    }

    @Test
    public void test_simpleFindLookupStrategies() {
        // Method under test
        List<Pair<Object, Method>> lookupStrategies = permissionEvaluator.findLookupStrategies(singletonList(permissionEntityLookupBeans1));
        assertEquals(1, lookupStrategies.size());
    }

    @Test
    public void test_complexFindLookupStrategies() {
        // Method under test
        List<Pair<Object, Method>> lookupStrategies = permissionEvaluator.findLookupStrategies(asList(permissionEntityLookupBeans1, permissionEntityLookupBeans2));
        assertEquals(3, lookupStrategies.size());
    }

    @Test
    public void test_generateLookupStrategies() throws InvocationTargetException, IllegalAccessException {

        permissionEvaluator.generateLookupStrategies();

        Map<Class<?>, ListOfOwnerAndMethod> permissionLookupStrategyMap = getPermissionLookupStrategyMap();

        // test that we have picked up and set the values for Strings, Integers and Longs
        {
            assertNotNull(permissionLookupStrategyMap);
            assertEquals(3, permissionLookupStrategyMap.size());
            assertThat(permissionLookupStrategyMap.keySet(), containsInAnyOrder(Integer.class, String.class, Long.class));
        }

        // test the lookup strategies against Strings
        {
            Pair<Object, Method> lookupStrategy = permissionLookupStrategyMap.get(String.class).get(0);
            assertEquals("A string 123", lookupStrategy.getRight().invoke(lookupStrategy.getLeft(), 123L));
        }

        // test the lookup strategies against Longs
        {
            Pair<Object, Method> lookupStrategy = permissionLookupStrategyMap.get(Long.class).get(0);
            assertEquals(105L, lookupStrategy.getRight().invoke(lookupStrategy.getLeft(), 5L));
        }

        // test the lookup strategies against Integers
        {
            Pair<Object, Method> lookupStrategy = permissionLookupStrategyMap.get(Integer.class).get(0);
            assertEquals(206, lookupStrategy.getRight().invoke(lookupStrategy.getLeft(), 6L));
        }
    }

    @Test
    public void test_hasPermission_withPermissionEntityLookup() {

        permissionEvaluator.generateRules();
        permissionEvaluator.generateLookupStrategies();

        // assert that the permissions and lookups are being applied correctly
        {
            assertFalse(permissionEvaluator.hasPermission(new UserAuthentication(noRightsUser), 123L, "java.lang.String", "Read"));
            assertTrue(permissionEvaluator.hasPermission(new UserAuthentication(readOnlyUser), 123L, "java.lang.String", "Read"));
            assertTrue(permissionEvaluator.hasPermission(new UserAuthentication(readWriteUser), 123L, "java.lang.String", "Read"));

            assertFalse(permissionEvaluator.hasPermission(new UserAuthentication(noRightsUser), 123L, "java.lang.String", "Write"));
            assertFalse(permissionEvaluator.hasPermission(new UserAuthentication(readOnlyUser), 123L, "java.lang.String", "Write"));
            assertTrue(permissionEvaluator.hasPermission(new UserAuthentication(readWriteUser), 123L, "java.lang.String", "Write"));
        }
    }

    @Test
    public void test_hasPermission_withPermissionEntityLookup_noLookupStrategyFound() {

        permissionEvaluator.generateRules();
        permissionEvaluator.generateLookupStrategies();

        try {
            permissionEvaluator.hasPermission(new UserAuthentication(readWriteUser), 123L, "java.lang.Double", "Read");
            fail("Should've failed as there are no lookup mechanisms for Doubles");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_hasPermission_withPermissionEntityLookup_moreThanOneLookupStrategyFound() {

        List<Object> allPermissionLookupBeans = asList(permissionEntityLookupBeans1, permissionEntityLookupBeans2, duplicatePermissionEntityLookupBeans3);
        Map<String, Object> allPermissionLookupBeansToMap = allPermissionLookupBeans.stream().collect(Collectors.toMap(bean -> bean.hashCode() + "", identity()));
        when(applicationContextMock.getBeansWithAnnotation(PermissionEntityLookupStrategies.class)).thenReturn(allPermissionLookupBeansToMap);

        try {
            permissionEvaluator.generateLookupStrategies();
            fail("Should've failed as there are more than one lookup mechanism for Longs");

        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_hasPermission_withPermissionEntityLookup_noEntityFoundWhenLookedUp() {

        permissionEvaluator.generateRules();
        permissionEvaluator.generateLookupStrategies();

        try {
            permissionEvaluator.hasPermission(new UserAuthentication(readWriteUser), Long.MAX_VALUE, "java.lang.Long", "Read");
            fail("Should've failed as no entity could be looked up");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_hasPermission_nonExistentClass() {

        permissionEvaluator.generateRules();
        permissionEvaluator.generateLookupStrategies();

        try {
            permissionEvaluator.hasPermission(new UserAuthentication(readWriteUser), 123L, "does.not.Exist", "Read");
            fail("Should've failed as there is no such class as does.not.Exist");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }


    @Test
    public void test_getPermissions() {
        permissionEvaluator.generateRules();
        permissionEvaluator.generateLookupStrategies();
        assertEquals(
                "There should only be a Read permission on Integers",
            singletonList("Read"),
                permissionEvaluator.getPermissions(new UserAuthentication(readWriteUser), 123));
        assertEquals(
                "There should only be a Read and Write permission on String",
                Arrays.asList(new String[]{"Read", "Write"}),
                permissionEvaluator.getPermissions(new UserAuthentication(readWriteUser), "A String"));
        assertEquals(
                "There should only be a Read and Write permission on String",
                Arrays.asList(new String[]{"Read"}),
                permissionEvaluator.getPermissions(new UserAuthentication(readOnlyUser), "A String"));
        assertEquals(
                "A user can have no permissions",
                new ArrayList<>(),
                permissionEvaluator.getPermissions(new UserAuthentication(new UserResource()), "A String"));
        assertEquals(
                "A domain object can have no permissions",
                new ArrayList<>(),
                permissionEvaluator.getPermissions(new UserAuthentication(new UserResource()), 1.0f));

    }


    private Map<Class<?>, Map<String, List<Pair<Object, Method>>>> getRulesMap() {
        return (Map<Class<?>, Map<String, List<Pair<Object, Method>>>>) getField(permissionEvaluator, "rulesMap");
    }

    private Map<Class<?>, ListOfOwnerAndMethod> getPermissionLookupStrategyMap() {
        return (Map<Class<?>, ListOfOwnerAndMethod>) getField(permissionEvaluator, "lookupStrategyMap");
    }

}


