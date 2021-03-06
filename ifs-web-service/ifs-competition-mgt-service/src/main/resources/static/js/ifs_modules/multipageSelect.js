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
      selectionLimitExceededElement: '[data-selection-limit-exceeded-block]',
      limitExceededMessage: '<div class="warning-alert govuk-!-margin-bottom-6"><p class="govuk-body">Cannot select additional items, selection limit of 500 exceeded.</p></div>',
      totalListSize: 0
    },
    init: function () {
      s = this.settings
      // caching the total list size once so we can do the changeSelectAllCheckboxState all selected check
      s.totalListSize = parseInt(jQuery(s.totalListSizeEl).attr('data-total-checkboxes'))

      // synchronous ajax calls to the server
      var multipageAjaxCall = jQuery.when({})

      jQuery('body').on('change', s.multipageCheckboxEl, function () {
        jQuery(this).prop('disabled', 'disabled')
        jQuery(s.submitEl).prop('disabled', 'disabled')
        multipageAjaxCall = multipageAjaxCall.then(IFS.competitionManagement.multipageSelect.processMultipageCheckbox(this))
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
      return function () {
        var defer = jQuery.Deferred()
        checkbox = jQuery(checkbox)
        var isSelectAll = checkbox.is(s.selectAllEl)
        var checked = checkbox.is(':checked')
        var value = checkbox.val()
        var data = IFS.competitionManagement.multipageSelect.getData(checked, value, isSelectAll)
        var url = window.location.href

        checkbox.prop('disabled', 'disabled')
        jQuery(s.submitEl).prop('disabled', 'disabled')

        jQuery.ajaxProtected({
          type: 'POST',
          url: url,
          data: data,
          dataType: 'json',
          timeout: 15000
        }).done(function (result) {
          checkbox.removeProp('disabled')
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
        }).always(function () {
          defer.resolve()
        })
        return defer.promise()
      }
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
      if (errorElement.length) {
        if (limitExceeded) {
          errorElement.html(s.limitExceededMessage)
          checkbox.removeProp('checked')
        } else {
          errorElement.empty()
        }
      }
    }
  }
})()
