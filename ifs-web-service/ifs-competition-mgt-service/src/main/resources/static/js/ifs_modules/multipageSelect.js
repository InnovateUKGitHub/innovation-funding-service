IFS.competitionManagement.multipageSelect = (function () {
  'use strict'
  var s
  return {
    settings: {
      multipageCheckboxEl: '[data-multipage-select] [type="checkbox"]', // <form data-multipage-select> to identify this is a multipage Select
      selectAllEl: '[data-select-all]',       // the select all element <input type="che"
      countEl: '[data-count-selected]',
      submitEl: '[data-submit-el]'
    },
    init: function () {
      s = this.settings

      jQuery('body').on('change', s.multipageCheckboxEl, function () {
        IFS.competitionManagement.multipageSelect.processMultipageCheckbox(this)
      })
    },
    getData: function (checked, value, isSelectAll) {
      if (isSelectAll) {
        return { 'addAll': checked }
      } else {
        return { 'assessor': value, 'isSelected': checked }
      }
    },
    processMultipageCheckbox: function (checkbox) {
      checkbox = jQuery(checkbox)
      var isSelectAll = checkbox.is(s.selectAllEl)
      var checked = checkbox.is(':checked')
      var value = checkbox.val()
      var data = IFS.competitionManagement.multipageSelect.getData(checked, value, isSelectAll)
      var url = window.location.href

      jQuery.ajaxProtected({
        type: 'POST',
        url: url,
        data: data,
        dataType: 'json',
        timeout: IFS.core.autoSave.settings.ajaxTimeOut
      }).done(function (result) {
        if (isSelectAll) {
          IFS.competitionManagement.multipageSelect.changeStateCheckboxes(checked)
        }
        if (typeof (result.selectionCount) !== 'undefined') {
          var selectedRows = parseInt(result.selectionCount)
          console.log(selectedRows)
          IFS.competitionManagement.multipageSelect.updateCount(selectedRows)
          IFS.competitionManagement.multipageSelect.updateSubmitButton(selectedRows)
        }
      }).fail(function (jqXHR, data) {
        // ignore incomplete requests, likely due to navigating away from the page
        if (jqXHR.readyState < 4) {
          return true
        } else {
          var errorMessage = 'Something went wrong'
          checkbox.closest('fieldset').find('legend').html('<span class="error-message">' + errorMessage + '</span>')
        }
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
      if (jQuery(s.countEl).length) {
        jQuery(s.countEl).text(count)
      }
    },
    changeStateCheckboxes: function (state) {
      var allCheckboxes = jQuery(s.multipageCheckboxEl)
      if (state === true) {
        allCheckboxes.prop('checked', 'checked')
      } else {
        allCheckboxes.removeProp('checked')
      }
    }
  }
})()
