// IFS.core.autoComplete = (function () {
//   'use strict'
//   var s // private alias to settings
//   return {
//     settings: {
//       autoCompleteWrapper: '.autocomplete__wrapper',
//       autoCompleteElement: '[data-auto-complete]',
//       autoCompleteSubmitElement: '[data-auto-complete-submit]',
//       menuLimit: 20,
//       autoCompletePlugin: accessibleAutocomplete // eslint-disable-line
//     },
//     init: function () {
//       s = this.settings
//       var autoCompleteElement = jQuery(s.autoCompleteElement)
//       if (autoCompleteElement.length > 0) {
//         console.log('Element')
//         autoCompleteElement.each(function () {
//           IFS.core.autoComplete.initAutoCompletePlugin(jQuery(this))
//         })
//       }
//     },
//     initAutoCompletePlugin: function (element) {
//       console.log('init plugin')
//       var autoCompleteSubmitElement = jQuery(s.autoCompleteSubmitElement)
//       if (autoCompleteSubmitElement.length > 0) {
//         autoCompleteSubmitElement.prop('disabled', true)
//         var wrapper = element.closest(s.autoCompleteWrapper)
//         jQuery(document).on('keydown', wrapper, function (e) {
//           if (e.which !== 13 && e.which !== 32) {
//             console.log('Event')
//             autoCompleteSubmitElement.prop('disabled', true)
//           }
//         })
//       }
//       var showAllValues = element.children('option').length <= s.menuLimit
//       var required = element.data('required-errormessage')
//       s.autoCompletePlugin.enhanceSelectElement({
//         selectElement: element[0],
//         showAllValues: showAllValues,
//         defaultValue: '',
//         confirmOnBlur: false,
//         displayMenu: 'overlay',
//         required: required,
//         onConfirm: function (confirmed) {
//           console.log('confirm')
//           console.log(confirmed)
//           if (confirmed !== '') {
//             console.log('Full')
//             var selectedUserId = element.children('option:contains(' + confirmed + ')').val()
//             element.val(selectedUserId)
//             autoCompleteSubmitElement.prop('disabled', false)
//           } else {
//             console.log('Empty')
//             element.val('')
//           }
//         }
//       })
//       if (required) {
//         element.parent().find('.autocomplete__input').attr('data-required-errormessage', required)
//       }
//     }
//   }
// })()

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
        console.log('Element')
        autoCompleteElement.each(function () {
          IFS.core.autoComplete.initAutoCompletePlugin(jQuery(this))
        })
      }
    },
    initAutoCompletePlugin: function (element) {
      console.log('init plugin')
      var autoCompleteSubmitElement = jQuery(s.autoCompleteSubmitElement)
      if (element.length > 0) {
        autoCompleteSubmitElement.prop('disabled', true)
        jQuery(document).on('keydown', s.autoCompleteWrapper, function (e) {
          if (e.which !== 13 && e.which !== 32) {
            autoCompleteSubmitElement.prop('disabled', true)
          }
        })
        var showAllValues = element.children('option').length <= s.menuLimit
        // var required = element.data('required-errormessage')
        s.autoCompletePlugin.enhanceSelectElement({
          autoselect: false,
          selectElement: element[0],
          showAllValues: showAllValues,
          defaultValue: '',
          confirmOnBlur: false,
          displayMenu: 'overlay'
          // required: required
          // onConfirm: function (confirmed) {
          //   var selectedUserId = element.children('option:contains(' + confirmed + ')').val()
          //   element.val(selectedUserId)
          //   autoCompleteSubmitElement.prop('disabled', false)
          // }
        })
      }
    }
  }
})()
