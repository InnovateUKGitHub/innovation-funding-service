IFS.core.autoComplete = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      autoCompleteWrapper: '.autocomplete__wrapper',
      autoCompleteElement: '[data-auto-complete]',
      autoCompleteSubmitElement: '[data-auto-complete-submit]',
      menuLimit: 20,
      autoCompletePlugin: accessibleAutocomplete // eslint-disable-line
    },
    init: function () {
      s = this.settings
      var autoCompleteElement = jQuery(s.autoCompleteElement)
      if (autoCompleteElement.length > 0) {
        autoCompleteElement.each(function () {
          IFS.core.autoComplete.initAutoCompletePlugin(jQuery(this))
        })
      }
    },
    initAutoCompletePlugin: function (element) {
      var autoCompleteSubmitElement = jQuery(s.autoCompleteSubmitElement)
      if (autoCompleteSubmitElement.length > 0) {
        autoCompleteSubmitElement.prop('disabled', true)
        var wrapper = element.closest(s.autoCompleteWrapper)
        jQuery(document).on('keydown', wrapper, function (e) {
          if (e.which !== 13 && e.which !== 32) {
            autoCompleteSubmitElement.prop('disabled', true)
          }
        })
      }
      var showAllValues = element.children('option').length <= s.menuLimit
      s.autoCompletePlugin.enhanceSelectElement({
        selectElement: element[0],
        showAllValues: showAllValues,
        defaultValue: '',
        confirmOnBlur: false,
        displayMenu: 'overlay',
        onConfirm: function (confirmed) {
          var selectedUserId = element.children('option:contains(' + confirmed + ')').val()
          element.val(selectedUserId)
          autoCompleteSubmitElement.prop('disabled', false)
        }
      })
    }
  }
})()
