package com.worth.ifs.commons.pojo;


import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.*;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import com.worth.ifs.application.finance.form.AcademicFinance;
import com.worth.ifs.application.finance.model.AcademicFinanceFormField;
import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.form.*;
import com.worth.ifs.application.finance.model.QuestionStatusModel;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.login.form.ResetPasswordForm;
import com.worth.ifs.login.form.ResetPasswordRequestForm;
import com.worth.ifs.profile.UserDetailsForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

public class POJOTest {
    // Configured for expectation, so we know when a class gets added or removed.
    private static final int EXPECTED_RESOURCES = 0;

    // The package to test
    private static final String POJO_PACKAGE = "com.worth.ifs";

    private List<PojoClass> classes;
    private Validator validator;
    private List<Class<?>> classesToTest = Arrays.asList(
            QuestionStatusModel.class,
            FinanceFormField.class,
            AcademicFinanceFormField.class,
            AcademicFinance.class,
            OrganisationInviteForm.class,
            Form.class,
            InviteeForm.class,
            ContributorsForm.class,
            AddressForm.class,
            ResetPasswordForm.class,
            ResetPasswordRequestForm.class,
            UserDetailsForm.class,
            ApplicationForm.class
    );

    @Before
    public void setup() {
        classes = simpleMap(classesToTest, PojoClassFactory::getPojoClass);

        validator = ValidatorBuilder.create()
                .with(
                        new GetterMustExistRule(),
                        new NoNestedClassRule(),
                        new NoStaticExceptFinalRule(),
                        new SerializableMustHaveSerialVersionUIDRule(),
                        new NoFieldShadowingRule(),
                        new NoPublicFieldsExceptStaticFinalRule(),
                        new TestClassMustBeProperlyNamedRule()
                )
                .with(
                        new SetterTester(),
                        new GetterTester()
                )
                .build();


    }

    @Test
    public void ensureExpectedPojoCount() {
        Assert.assertEquals(String.format("Classes added / removed? %s => %s ", classesToTest.size()+EXPECTED_RESOURCES, classes.size()), classesToTest.size()+EXPECTED_RESOURCES, classes.size());
    }

    @Test
    public void testPojoStructureAndBehavior() {
        validator.validate(classes);
    }

    private static class FilterPackages implements  PojoClassFilter {
        private final List<Class<?>> classes;

        FilterPackages(List<Class<?>> classes){
            this.classes = classes;
        }

        @Override
        public boolean include(PojoClass pojoClass) {
            System.out.println("Include?? "+ pojoClass.getName());
            try{
                Class<?> clazz = Class.forName(pojoClass.getName());
                if(clazz.isInterface()){
                    return false;
                }
            }catch(ClassNotFoundException e){
                System.out.println("exception 1: "+ e.getStackTrace());
                return false;
            }catch(NoClassDefFoundError e){
                System.out.println("exception 2: "+ e.getStackTrace());
                return false;
            }
            return (classes.stream().anyMatch(pojoClass.getClazz()::equals));
        }
    }
}
