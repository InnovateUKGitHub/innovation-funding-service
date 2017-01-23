// When the value of an element with a data-clear-inputs is changed,
// it will clear the value of the fields matching the selector put into data-clear-inputs.
// I.e. a radiobutton with [data-clear-inputs="#form2 inputs"] will clear all inputs within form2 when selected
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
        IFS.projectSetup.clearInputs.clearInputs(inputs)
      })
    },
    clearInput: function (inputs) {
      inputs = jQuery(inputs)
      jQuery.each(inputs, function () {
        jQuery(this).val('')
      })
    }
  }
})()
