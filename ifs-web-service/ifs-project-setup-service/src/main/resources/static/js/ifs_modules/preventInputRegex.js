// prevents user typing or pasting characters that are not within a defined regex
IFS.projectSetup.preventInputRegex = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      preventInputRegexEl: 'data-prevent-input-regex'
    },
    init: function () {
      s = this.settings

      jQuery(document).on('input', '[' + s.preventInputRegexEl + ']', function () {
        var el = jQuery(this)
        var regex = new RegExp(el.attr(s.preventInputRegexEl), 'g')
        var inputValue = el.val()
        var outputValue = inputValue.replace(regex, '')
        el.val(outputValue)
      })
    }
  }
})()
