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
      var selectAllSelector = selectAllcheckbox.attr(s.selectAllAttribute)
      var selectCheckboxes = jQuery(selectAllSelector)
      var status = selectAllcheckbox.prop('checked')

      if (selectCheckboxes.length) {
        if (status === true) {
          selectCheckboxes.prop('checked', 'checked')
        } else {
          selectCheckboxes.removeProp('checked')
        }
      }
    }
  }
})()
