// prevents user typing or pasting characters that are not within a defined regex
IFS.core.preventInputRegex = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      preventInputRegexEl: 'data-prevent-input-regex'
    },
    init: function () {
      s = this.settings

      /* we catch paste/keypress commands for number fields as the input event directly clashes with browsers
        own inadequate attempts to prevent bad input */
      jQuery('[data-prevent-input-non-number]').on('paste', function (event) {
        var regex = new RegExp('[^0-9]', 'g')
        var data = event.originalEvent.clipboardData.getData('text')
        if (data.replace(regex, '') !== data) {
          event.originalEvent.preventDefault()
        }
      })

      jQuery('[data-prevent-input-non-number]').on('keypress', function (event) {
        var regex = new RegExp('[^0-9]', 'g')
        if (event.originalEvent.key.replace(regex, '') === '') {
          event.originalEvent.preventDefault()
        }
      })

      // general form that can be used with any regex
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
