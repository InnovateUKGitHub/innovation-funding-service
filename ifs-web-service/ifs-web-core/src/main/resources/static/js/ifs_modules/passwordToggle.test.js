describe('test passwordToggle', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./passwordToggle")
    })

    test("test showPassword", () => {
        // create elements to be manipulated
        var button = jQuery('<button id="test"></button>');
        var passwordInput = jQuery('<input type="password" value="pass1234"/>');
        var submitButton = jQuery('<button type="submit" form="form1" value="Submit">Submit</button>');
        var form = jQuery('<form />')

        // set the password fieldname
        expectedFieldName = "TEST1234"
        IFS.core.passwordToggle.setFieldName(expectedFieldName)

        // invoke show password
        IFS.core.passwordToggle.showPassword(button, passwordInput, submitButton, form)

        // check we're in the show state
        expect(button.attr('aria-checked')).toBe("true")
        expect(button.text()).toBe("Hide")
        expect(passwordInput.attr('name')).not.toBe(expectedFieldName)
        expect(passwordInput.attr('type')).toBe("text")

        // check submitbutton returns passwordinput to submitable state
        submitButton.click()
        expect(passwordInput.attr('name')).toBe(expectedFieldName)
        expect(passwordInput.attr('type')).toBe("password")

    })

    test("test hidePassword", () => {
        // create elements to be manipulated
        var button = jQuery('<button id="test">Hide</button>');
        var shownPasswordInput = jQuery('<input type="text" value="pass1234"/>');

        // set the password fieldname
        expectedFieldName = "TEST1234"
        IFS.core.passwordToggle.setFieldName(expectedFieldName)

        // invoke hide password
        IFS.core.passwordToggle.hidePassword(button, shownPasswordInput)

        // check we're in the hide state
        expect(button.attr('aria-checked')).toBe("false")
        expect(button.text()).toBe("Show")
        expect(shownPasswordInput.attr('name')).toBe(expectedFieldName)
        expect(shownPasswordInput.attr('type')).toBe("password")

    })
})