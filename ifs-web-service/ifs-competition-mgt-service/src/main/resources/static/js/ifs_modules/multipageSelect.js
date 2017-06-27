IFS.competitionManagement.multipageSelect = (function () {
  'use strict'
  var s
  return {
    settings: {
      multipageCheckboxEl: '[data-multipage-select] [type="checkbox"]',
      selectAllEl: '[data-multipage-select] [data-select-all]',
      countEl: '[data-multipage-select] [data-count-selected]',
      submitEl: '[data-multipage-select] [data-submit-el]',
      totalListSizeEl: '[data-multipage-select][data-total-checkboxes]',
      selectionLimitExceededElement: '#selection-limit-exceeded-block',
      totalListSize: 0
    },
    init: function () {
      s = this.settings
      // caching the total list size once so we can do the changeSelectAllCheckboxState all selected check
      s.totalListSize = parseInt(jQuery(s.totalListSizeEl).attr('data-total-checkboxes'))

      jQuery('body').on('change', s.multipageCheckboxEl, function () {
        IFS.competitionManagement.multipageSelect.processMultipageCheckbox(this)
      })
    },
    getData: function (checked, value, isSelectAll) {
      if (isSelectAll) {
        return { 'addAll': checked }
      } else {
        return { 'selectionId': value, 'isSelected': checked }
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
          IFS.competitionManagement.multipageSelect.changeAllCheckboxStates(checked)
        }
        if (typeof (result.selectionCount) !== 'undefined') {
          var selectedRows = parseInt(result.selectionCount)
          var allSelected = result.allSelected
          var limitExceeded = result.limitExceeded
          IFS.competitionManagement.multipageSelect.updateCount(selectedRows)
          IFS.competitionManagement.multipageSelect.updateSubmitButton(selectedRows)
          if (!isSelectAll) {
            IFS.competitionManagement.multipageSelect.changeSelectAllCheckboxState(allSelected)
          }
          IFS.competitionManagement.multipageSelect.updateLimitExceededMessage(limitExceeded, checkbox)
        }
      }).fail(function (data) {
        var errorMessage = IFS.core.autoSave.getErrorMessage(data)
        checkbox.closest('fieldset').find('legend').append('<span class="error-message">' + errorMessage + '</span>')
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
    changeSelectAllCheckboxState: function (allSelected) {
      // if all checkboxes are checked we also check the s'electAll
      var selectAll = jQuery(s.selectAllEl)
      if (allSelected) {
        selectAll.prop('checked', 'checked')
      } else {
        selectAll.removeProp('checked')
      }
    },
    changeAllCheckboxStates: function (selectAllChecked) {
      var allCheckboxes = jQuery(s.multipageCheckboxEl)
      if (selectAllChecked) {
        allCheckboxes.prop('checked', 'checked')
      } else {
        allCheckboxes.removeProp('checked')
      }
    },
    updateLimitExceededMessage: function (limitExceeded, checkbox) {
      var errorElement = jQuery(s.selectionLimitExceededElement)
      if (limitExceeded) {
        errorElement.html('<div class="warning-alert extra-margin-bottom">' +
                              '<p>Cannot select additional items, selection limit of 500 exceeded.</p>' +
                               '</div>')
        checkbox.removeProp('checked')
      } else {
        errorElement.html('')
      }
    }
  }
})()
