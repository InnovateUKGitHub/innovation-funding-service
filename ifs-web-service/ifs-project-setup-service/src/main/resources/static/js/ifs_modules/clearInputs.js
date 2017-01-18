IFS.projectSetup.clearInputs = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      clearInputsEl: 'data-clear-inputs'
    },
    init: function () {
      s = this.settings
      jQuery(document).on('change', '[' + s.clearInputsEl + ']', function () {
        var inputs = jQuery(this).attr('data-clear-inputs')
        inputs = jQuery(inputs)
        jQuery.each(inputs, function () {
          jQuery(this).val('')
        })
      })
    }
  }
})()
