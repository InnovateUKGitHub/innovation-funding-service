describe('test conditional forms', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./conditionalForms")
    })

    test("test toggleVisibility", () => {
        // build check box and target div
        var uncheckedCheckbox = jQuery('<input type=checkbox value=a name=not_checked>');
        var checkedCheckbox = jQuery('<input type=checkbox value=b name=checked checked>');
        var target = jQuery('<div>I am target</div>');

        // run toggleVisibility with checked and verify hidden
        IFS.core.conditionalForms.toggleVisibility(uncheckedCheckbox, target[0], false, false, false);
        expect(target.attr('aria-hidden')).toBe("true")

        // run toggleVisibility with unchecked and verify shown
        IFS.core.conditionalForms.toggleVisibility(checkedCheckbox, target[0], false, false, false);
        expect(target.attr('aria-hidden')).toBe("false")

        // run toggleVisibility with checked, inverted, and verify shown
        IFS.core.conditionalForms.toggleVisibility(uncheckedCheckbox, target[0], false, false, true);
        expect(target.attr('aria-hidden')).toBe("false")

        // run toggleVisibility with unchecked, inverted and verify hidden
        IFS.core.conditionalForms.toggleVisibility(checkedCheckbox, target[0], false, false, true);
        expect(target.attr('aria-hidden')).toBe("true")
    })
})