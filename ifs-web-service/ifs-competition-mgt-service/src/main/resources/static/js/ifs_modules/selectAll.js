IFS.competitionManagement.selectAll = (function () {
  'use strict'
  var s
  return {
    settings: {
      selectAllAttribute: 'data-select-all'
    },
    init: function () {
      s = this.settings
      jQuery('body').on('change', '[' + s.selectAllAttribute + ']', function () {
        IFS.competitionManagement.selectAll.changeState(this)
      })
    },
    changeState: function (el) {
      var selectAllcheckbox = jQuery(el)
      var selectAllAtribute = selectAllcheckbox.attr(s.selectAllAttribute)
      var selectCheckboxes = jQuery(selectAllAtribute)
      var status = selectAllcheckbox.prop('checked')

      if (selectCheckboxes.length) {
        if (status === true) {
          selectCheckboxes.prop('checked', 'checked').trigger('change').closest('label').addClass('selected')
          jQuery('[data-select-all]').prop('checked', 'checked').closest('label').addClass('selected')
        } else {
          selectCheckboxes.removeAttr('checked').trigger('change').closest('label').removeClass('selected')
          jQuery('[data-select-all]').removeAttr('checked').closest('label').removeClass('selected')
        }
      }
    }
  }
})()
