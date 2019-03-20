IFS.core.autoComplete = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      autoCompleteWrapper: '.autocomplete__wrapper',
      autoCompleteElement: '[data-auto-complete]',
      autoCompleteSubmitElement: '[data-auto-complete-submit]',
      autoCompletePlugin: accessibleAutocomplete // eslint-disable-line
    },
    init: function () {
      s = this.settings
      var autoCompleteElement = jQuery(s.autoCompleteElement)
      var autoCompleteSubmitElement = jQuery(s.autoCompleteSubmitElement)
      if (autoCompleteElement.length > 0) {
        autoCompleteElement.prepend('<option value="">Select</option>').val('')
        autoCompleteSubmitElement.prop('disabled', true)
        jQuery(document).on('keydown', s.autoCompleteWrapper, function (e) {
          if (e.which !== 13 && e.which !== 32) {
            autoCompleteSubmitElement.prop('disabled', true)
          }
        })
        var showAllValues = autoCompleteElement.children('option').length <= 20
        s.autoCompletePlugin.enhanceSelectElement({
          selectElement: autoCompleteElement[0],
          showAllValues: showAllValues,
          defaultValue: '',
          confirmOnBlur: false,
          onConfirm: function (confirmed) {
            autoCompleteElement.children('option:contains(' + confirmed + ')').attr('selected', 'selected')
            autoCompleteSubmitElement.prop('disabled', false)
          }
        })
      }
    }
  }
})()
