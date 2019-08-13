// Handle replacing square brackets on radio button name so we can tab to it
IFS.assessment.scopePage = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      element: '[data-replace-name]'
    },
    init: function () {
      s = this.settings
      // Rename the field to remove square brackets
      IFS.assessment.scopePage.renameField(s.element, 'parenthesis')

      // Add square brackets back into name on submit
      var form = jQuery(s.element).closest('form')
      jQuery(form).on('submit', function () {
        IFS.assessment.scopePage.renameField(s.element, 'square')
      })
    },
    renameField: function (radioButtons, type) {
      jQuery(radioButtons).each(function () {
        var name
        if (type === 'square') {
          name = jQuery(this).attr('name').replace('(', '[').replace(')', ']')
        } else {
          name = jQuery(this).attr('name').replace('[', '(').replace(']', ')')
        }
        jQuery(this).attr('name', name)
      })
    }
  }
})()
