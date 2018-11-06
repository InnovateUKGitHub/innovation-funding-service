describe('test editor manipulation', () => {
	beforeAll(() => {
		// import IFS module
		IFS = {"core" : {}}
		require("./editor")
	})
	test("check list formatter", () => {
		// check basic <div> addition to list
		expect(
			IFS.core.editor.formatContent("<ul>test</ul>")
		).toBe("<ul>test</ul><div></div>")

		// check pair of lists with gap
		expect(
			IFS.core.editor.formatContent("<ul>list1</ul>gap<ul>list2</ul>")
		).toBe("<ul>list1</ul><div>gap<ul>list2</ul></div>")

		// check ordered list with br tags, removes br and adds div
		expect(
			IFS.core.editor.formatContent("<ol>list1</ol><br>gap<ol>list2</ol><br>")
		).toBe("<ol>list1</ol><div><div></div>gap<ol>list2</ol><div></div></div>")

		// check we handle moz tags
		expect(
			IFS.core.editor.formatContent('<ol>list1</ol><br type="_moz">gap<ol>list2</ol><br type="_moz">')
		).toBe("<ol>list1</ol><div><div></div>gap<ol>list2</ol><div></div></div>")
	})
	
	test("check textToHtml", () => {
		// empty string
		expect(
			IFS.core.editor.textToHtml("")
		).toBe("<p>&nbsp;</p>")

		// basic string
		expect(
			IFS.core.editor.textToHtml("this is basic")
		).toBe("<p>this is basic</p>")

		// string with line breaks
		expect(
			IFS.core.editor.textToHtml("this is \n a break \n see")
		).toBe("<p>this is <br /> a break <br /> see</p>")
	}
})