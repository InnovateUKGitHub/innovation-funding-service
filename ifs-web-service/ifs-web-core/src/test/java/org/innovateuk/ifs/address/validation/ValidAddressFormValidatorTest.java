package org.innovateuk.ifs.address.validation;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class ValidAddressFormValidatorTest extends BaseUnitTest {

    ValidAddressFormValidator validAddressFormValidator;
    AddressForm addressForm;

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Before
    public void setUp() {
        validAddressFormValidator = new ValidAddressFormValidator();
        addressForm = new AddressForm();
    }

    private void constraintValidatorContextMockSetup() {
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(any(String.class))).thenReturn(builder);

        ConstraintViolationBuilder.NodeBuilderCustomizableContext node = mock(NodeBuilderCustomizableContext.class);
        when(builder.addPropertyNode(any())).thenReturn(node);
    }

    @Test
    public void isValid_ActionIsSaveIsManualAddressEntryValid() {
        addressForm.setAction(AddressForm.Action.SAVE);
        addressForm.setAddressType(AddressForm.AddressType.MANUAL_ENTRY);
        AddressResource address = new AddressResource("addressLine1", null, null, "town", null, "AD87 4OP");
        addressForm.setManualAddress(address);

        assertTrue(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));
    }

    @Test
    public void isValid_ActionIsSaveIsManualAddressEntryInvalidNullPostcode() {
        addressForm.setAction(AddressForm.Action.SAVE);
        addressForm.setAddressType(AddressForm.AddressType.MANUAL_ENTRY);
        AddressResource address = new AddressResource(null, null, null, null, null, null);
        addressForm.setManualAddress(address);
        constraintValidatorContextMockSetup();

        assertFalse(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));
    }

    @Test
    public void isValid_ActionIsSaveIsManualAddressEntryInvalidLongPostcode() {
        addressForm.setAction(AddressForm.Action.SAVE);
        addressForm.setAddressType(AddressForm.AddressType.MANUAL_ENTRY);
        AddressResource address = new AddressResource(null, null, null, null, null, "123456789");
        addressForm.setManualAddress(address);
        constraintValidatorContextMockSetup();

        assertFalse(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));
    }

    @Test
    public void isValid_ActionIsSaveIsNotManualAddressEntryIsPostcodeAddressEntrySelectedPostCodeIndexIsNullOrLessThanZero() {
        addressForm.setAction(AddressForm.Action.SAVE);
        addressForm.setAddressType(AddressForm.AddressType.POSTCODE_LOOKUP);
        addressForm.setSelectedPostcodeIndex(null);
        constraintValidatorContextMockSetup();

        assertFalse(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));
    }

    @Test
    public void isValid_ActionIsSaveIsNotManualAddressEntryIsPostcodeAddressEntrySelectedPostCodeIndexIsNotNullOrLessThanZero() {
        addressForm.setAction(AddressForm.Action.SAVE);
        addressForm.setAddressType(AddressForm.AddressType.POSTCODE_LOOKUP);
        addressForm.setSelectedPostcodeIndex(2);
        constraintValidatorContextMockSetup();

        assertTrue(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));
    }

    @Test
    public void isValid_ActionIsSaveIsNotManualAddressEntryIsNotPostcodeAddressEntry() {
        addressForm.setAction(AddressForm.Action.SAVE);
        addressForm.setAddressType(null);
        constraintValidatorContextMockSetup();

        assertFalse(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));

    }

    @Test
    public void isValid_ActionIsSearchPostcodeAndPostCodeInputIsNullOrEmpty() {
        addressForm.setAction(AddressForm.Action.SEARCH_POSTCODE);
        addressForm.setPostcodeInput(null);
        constraintValidatorContextMockSetup();

        assertFalse(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));
    }

    @Test
    public void isValid_ActionIsNotSaveIsNotSearchPostcode() {
        addressForm.setAction(AddressForm.Action.CHANGE_POSTCODE);

        assertTrue(validAddressFormValidator.isValid(addressForm, constraintValidatorContext));
    }
}
