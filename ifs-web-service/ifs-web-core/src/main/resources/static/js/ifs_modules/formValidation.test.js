describe('test form validation', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');

        createInputField = function (value)
        {
            return jQuery('<input type="text" value="' + value + '"/>')
        }

        require("./formValidation")
        // patch invalid and valid functions so we easily see the result of validation functions
        IFS.core.formValidation.setInvalid = function(field,errorMessage,display){
            field.attr('invalid', errorMessage);
        }
        IFS.core.formValidation.setValid = function(field,errorMessage,display){
            field.attr('valid', errorMessage);
        }
        IFS.core.formValidation.init();
    })

    // Validation functions
    test("check contains uppercase", () => {
        expect(IFS.core.formValidation.checkFieldContainsUppercase(createInputField("abcdefGh"))).toBeTruthy()
        expect(IFS.core.formValidation.checkFieldContainsUppercase(createInputField("abcdefgh"))).toBeFalsy()
    })

    test("check contains lowercase", () => {
        expect(IFS.core.formValidation.checkFieldContainsLowercase(createInputField("abcdefGh"))).toBeTruthy()
        expect(IFS.core.formValidation.checkFieldContainsLowercase(createInputField("ABCDEFGH"))).toBeFalsy()
    })

    test("check contains number", () => {
        expect(IFS.core.formValidation.checkFieldContainsNumber(createInputField("abcdef9h"))).toBeTruthy()
        expect(IFS.core.formValidation.checkFieldContainsNumber(createInputField("abcdefgh"))).toBeFalsy()
    })

    test("check email", () => {
        expect(IFS.core.formValidation.checkEmail(createInputField("test@wibble.com"))).toBeTruthy()
        expect(IFS.core.formValidation.checkEmail(createInputField("banana"))).toBeFalsy()
    })

    test("check number", () => {
        // this is using non html5 validation
        expect(IFS.core.formValidation.checkNumber(createInputField("3423"))).toBeTruthy()
        expect(IFS.core.formValidation.checkNumber(createInputField("abcdefgh"))).toBeFalsy()
    })

    test("check max", () => {
        // this is using non html5 validation
        var validMaxField = jQuery('<input type="text" max="20" value="12"/>')
        expect(IFS.core.formValidation.checkMax(validMaxField)).toBeTruthy()

        var invalidMaxField = jQuery('<input type="text" max="10" value="12"/>')
        expect(IFS.core.formValidation.checkMax(invalidMaxField)).toBeFalsy()
    })

    test("check min", () => {
        // this is using non html5 validation
        var validMinField = jQuery('<input type="text" min="20" value="30"/>')
        expect(IFS.core.formValidation.checkMin(validMinField)).toBeTruthy()

        var invalidMinField = jQuery('<input type="text" min="10" value="8"/>')
        expect(IFS.core.formValidation.checkMin(invalidMinField)).toBeFalsy()
    })

    test("check range", () => {
        var validRangeField = jQuery('<input type="text" data-range-min="10" data-range-max="20" value="15"/>')
        expect(IFS.core.formValidation.checkRange(validRangeField)).toBeTruthy()

        var lowRangeField = jQuery('<input type="text" data-range-min="10" data-range-max="20" value="9"/>')
        expect(IFS.core.formValidation.checkRange(lowRangeField)).toBeFalsy()

        var highRangeField = jQuery('<input type="text" data-range-min="10" data-range-max="20" value="21"/>')
        expect(IFS.core.formValidation.checkRange(highRangeField)).toBeFalsy()
    })

    test("check min length", () => {
        var validMinField = jQuery('<input type="text" minlength="5" value="abcdef"/>')
        expect(IFS.core.formValidation.checkMinLength(validMinField)).toBeTruthy()

        var invalidMinField = jQuery('<input type="text" minlength="5" value="abcd"/>')
        expect(IFS.core.formValidation.checkMinLength(invalidMinField)).toBeFalsy()
    })

    test("check max length", () => {
        var validMaxField = jQuery('<input type="text" maxlength="5" value="abcd"/>')
        expect(IFS.core.formValidation.checkMaxLength(validMaxField)).toBeTruthy()

        var invalidMaxField = jQuery('<input type="text" maxlength="5" value="abcdef"/>')
        expect(IFS.core.formValidation.checkMaxLength(invalidMaxField)).toBeFalsy()
    })

    test("check pattern (regex validation)", () => {
        var validPatternField = jQuery('<input type="text" pattern="^[A-E]*$" value="ABC"/>')
        expect(IFS.core.formValidation.checkPattern(validPatternField)).toBeTruthy()

        var invalidPatternField = jQuery('<input type="text" pattern="^[A-E]*$" value="F"/>')
        expect(IFS.core.formValidation.checkPattern(invalidPatternField)).toBeFalsy()
    })

    test("check phone validation", () => {
        expect(IFS.core.formValidation.checkTel(createInputField("0123676767"))).toBeTruthy()
        expect(IFS.core.formValidation.checkTel(createInputField("banana"))).toBeFalsy()
    })

    // Helper functions
    test("get error message from field", () => {
        // test getting standard error message
        var taggedElement = jQuery('<input type="text" data-testmessage-errormessage="wibble" value="46"/>');
        expect(IFS.core.formValidation.getErrorMessage(taggedElement, "testmessage")).toBe("wibble")

        // test message missing (use default)
        expect(IFS.core.formValidation.getErrorMessage(taggedElement, "number")).toBe('This field can only accept whole numbers.')

        // test message missing (with subtype)
        expect(IFS.core.formValidation.getErrorMessage(taggedElement, "email-invalid")).toBe('Please enter a valid email address.')
        expect(IFS.core.formValidation.getErrorMessage(taggedElement, "email-duplicate")).toBe('The email address is already registered with us. Please sign into your account.')

        // test %text% injection
        var injectElement = jQuery('<input type="text" data-inject-errormessage="wibble %inject% wibble" inject="something" value="46"/>');
        expect(IFS.core.formValidation.getErrorMessage(injectElement, "inject")).toBe("wibble something wibble")
    })
    
    test("hash removal function", () => {
        // make sure hash removal only removes hashes from front of text
        expect(IFS.core.formValidation.removeHash("#test")).toBe("test")
        expect(IFS.core.formValidation.removeHash("test2")).toBe("test2")
        expect(IFS.core.formValidation.removeHash("te#st")).toBe("te#st")
    })
})
