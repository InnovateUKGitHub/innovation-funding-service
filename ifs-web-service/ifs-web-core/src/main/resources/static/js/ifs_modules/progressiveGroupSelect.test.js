describe('test progressiveGroupSelect', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./progressiveGroupSelect")
    })

    test("test update", () => {

        // create a select box with an optgroup but nothing selected
        var newSelectBox = jQuery(`<select data-progressive-group-select>
            <option value="">empty</option> 
            <optgroup label="test">
                <option value="1">one</option>
                <option value="2">two</option>
            </optgroup>
        </select>`)

        // run the update command without options
        var optgroup = newSelectBox.find('optgroup')
        IFS.core.progressiveGroupSelect.update(optgroup, newSelectBox, [])

        // expect the optgroup to be disabled
        expect(optgroup.attr('disabled')).toBe("disabled")


        // create a select box with a selected value, note only the first value of the optgroup persists
        var selectedSelectBox = jQuery(`<select data-progressive-group-select>
            <option value="list1" selected>list1</option> 
            <optgroup label="test">
                <option value="0">header</option>
                <option value="x">deleted</option>
            </optgroup>
        </select>`)

        // set of options, where options [0], [1], and [3], match the selected value
        var options = [
            jQuery(`<option value="1" data-optgroup-label="list1">one</option>`)[0],
            jQuery(`<option value="2" data-optgroup-label="list1">two</option>`)[0],
            jQuery(`<option value="bad" data-optgroup-label="bad">bad</option>`)[0],
            jQuery(`<option value="3" data-optgroup-label="list1">three</option>`)[0],
            jQuery(`<option value="bad" data-optgroup-label="bad">bad</option>`)[0],
        ]

        // run the update command with options
        var optgroup = selectedSelectBox.find('optgroup')
        IFS.core.progressiveGroupSelect.update(optgroup, selectedSelectBox, options)

        // check the optgroup isn't disabled
        expect(optgroup.attr('disabled')).not.toBe("disabled")

        // check that the optgroup has the header and the three options that match the value
        expect(jQuery(optgroup.find('option')[0]).text()).toBe("header")
        expect(optgroup.find('option')[1]).toBe(options[0])
        expect(optgroup.find('option')[2]).toBe(options[1])
        expect(optgroup.find('option')[3]).toBe(options[3])
    })
})