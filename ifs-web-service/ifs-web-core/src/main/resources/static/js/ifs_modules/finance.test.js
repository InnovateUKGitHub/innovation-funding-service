describe('test finance', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./finance")
    })

    test("test getElementValue", () => {
        // test element with data-calculation-rawvalue tag
        var taggedElement = jQuery('<div data-calculation-rawvalue="47"></div>');
        expect(IFS.core.finance.getElementValue(taggedElement)).toBe(47);

        // test element with value
        var untaggedElement = jQuery('<input type="text" value="46"/>');
        expect(IFS.core.finance.getElementValue(untaggedElement)).toBe(46);

        // test element with pound sign value
        var untaggedCurrencyElement = jQuery('<input type="text" value="£45"/>');
        expect(IFS.core.finance.getElementValue(untaggedCurrencyElement)).toBe(45);

        // test element with no value
        var emptyElement = jQuery('<div></div>');
        expect(IFS.core.finance.getElementValue(emptyElement)).toBe(0);
    })

    test("test formatCurrency", () => {
        // test small value
        expect(IFS.core.finance.formatCurrency(23)).toBe("£23");
        // test large value
        expect(IFS.core.finance.formatCurrency(12345678)).toBe("£12,345,678");
        // test round down
        expect(IFS.core.finance.formatCurrency(20.4)).toBe("£20");
        // test round up
        expect(IFS.core.finance.formatCurrency(20.5)).toBe("£21");
    })

    test("test formatPercentage", () => {
        // test whole value
        expect(IFS.core.finance.formatPercentage(23)).toBe("23%");
        // test round down
        expect(IFS.core.finance.formatPercentage(20.4)).toBe("20%");
        // test round up
        expect(IFS.core.finance.formatPercentage(20.5)).toBe("21%");
    })
})