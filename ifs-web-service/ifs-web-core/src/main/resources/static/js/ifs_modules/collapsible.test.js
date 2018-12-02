describe('test collapsible elements', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./collapsible")
    })

    test("test initLoadstate", () => {
        // build a mock collapsible div
        var wrapper = jQuery(' <div class="collapsible"><h3 class="govuk-heading-s">Finances summary</h3></div>');
        var header = wrapper.find('h3')

        // run initLoadstate
        IFS.core.collapsible.initLoadstate(header, true, true,
            function () { return 'header_id' },
            function (id) { return false })

        // verify header now looks correct
        var collapsibleButton = header.find('button')
        expect(collapsibleButton.attr('aria-expanded')).toBe("true")
        expect(collapsibleButton.attr('aria-controls')).toBe("header_id")
    })
})