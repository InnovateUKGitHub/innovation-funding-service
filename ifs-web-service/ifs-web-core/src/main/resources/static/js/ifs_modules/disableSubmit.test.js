describe('test disable submit', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./disableSubmit")
    })

    test("test updateButton", () => {
        // build mock button
        var button = jQuery('<button id="test"></button>');

        // disable button, check attributes
        IFS.core.disableSubmitUntilChecked.updateButton(button, false);
        expect(button.attr('aria-disabled')).toBe("true")
        expect(button.hasClass('disabled')).toBe(true)
        expect(button.prop('disabled')).toBe(true)

        // enable button, check attributes
        IFS.core.disableSubmitUntilChecked.updateButton(button, true);
        expect(button.attr('aria-disabled')).toBe(undefined)
        expect(button.hasClass('disabled')).toBe(false)
        expect(button.prop('disabled')).toBe(false)
    })
})