IFS.competitionManagement.select = (function () {
  'use strict'
  var s
  return {
    settings: {
      checkboxEl: '[data-select] [type="checkbox"]',
      selectAllEl: '[data-select] [data-select-all]',
      countEl: '[data-select] [data-count-selected]',
      submitEl: '[data-select] [data-submit-el]'
    },
    init: function () {
      s = this.settings
      jQuery(s.submitEl).prop('disabled', 'disabled')
      jQuery('body').on('change', s.checkboxEl, function () {
        var checkbox = jQuery(this)
        var isSelectAll = checkbox.is(s.selectAllEl)
        var checked = checkbox.is(':checked')
        var allCheckboxes = jQuery(s.checkboxEl).not(s.selectAllEl)
        if (isSelectAll) {
          IFS.competitionManagement.select.changeAllCheckboxStates(checked)
        }
        var selectedCount = jQuery(s.checkboxEl + ':checked').not(s.selectAllEl).length
        var allSelected = allCheckboxes.length === selectedCount
        if (!isSelectAll) {
          IFS.competitionManagement.select.changeSelectAllCheckboxState(allSelected)
        }
        IFS.competitionManagement.select.updateCount(selectedCount)
        IFS.competitionManagement.select.updateSubmitButton(selectedCount)
      })
    },
    updateSubmitButton: function (count) {
      var button = jQuery(s.submitEl)
      if (button.length) {
        if (count > 0) {
          button.removeProp('disabled')
        } else {
          button.prop('disabled', 'disabled')
        }
      }
    },
    updateCount: function (count) {
      var countEl = jQuery(s.countEl)
      if (countEl.length) {
        countEl.text(count)
      }
    },
    changeSelectAllCheckboxState: function (allSelected) {
      // if all checkboxes are checked we also check the selectAll
      var selectAll = jQuery(s.selectAllEl)
      if (allSelected) {
        selectAll.prop('checked', 'checked')
      } else {
        selectAll.removeProp('checked')
      }
    },
    changeAllCheckboxStates: function (selectAllChecked) {
      var allCheckboxes = jQuery(s.checkboxEl)
      if (selectAllChecked) {
        allCheckboxes.prop('checked', 'checked')
      } else {
        allCheckboxes.removeProp('checked')
      }
    }
  }
})()
