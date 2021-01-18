// see initEventHandlers in assignUser.js
IFS.manuallyEnter = (function () {
  'use strict'
  return {
    handleAddRowMan: function (e) {
      var type = jQuery(e).attr('data-add-row-man')
      switch (type) {
        case 'sicCode':
          IFS.manuallyEnter.addSicCode()
          break
        case 'execOfficer':
          IFS.manuallyEnter.addExecOfficer()
          break
      }
      jQuery('body').trigger('updateSerializedFormState')
      return false
    },
    handleRemoveRowMan: function (e) {
      var inst = jQuery(e)
      var type = jQuery(e).attr('data-remove-row-man')
      switch (type) {
        case 'sicCode':
          inst.closest('[id^="sic-code-row-"]').remove()
          IFS.manuallyEnter.reindexRows('[id^="sic-code-row-"]')
          break
        case 'execOfficer':
          inst.closest('[id^="exec-officer-row-"]').remove()
          IFS.manuallyEnter.reindexRows('[id^="exec-officer-row-"]')
          break
      }
      jQuery('body').trigger('updateSerializedFormState')
    },
    addSicCode: function () {
      var idCount = 0
      if (jQuery('.sic-code-row').length) {
        idCount = parseInt(jQuery('.sic-code-row[id^=sic-code-row-]').last().attr('id').split('sic-code-row-')[1], 10) + 1
        if (idCount >= 4) {
          return
        }
      }
      var html = '<div class="govuk-grid-row govuk-!-margin-top-6 sic-code-row" id="sic-code-row-' + idCount + '">' +
                         '<div class="govuk-grid-column-one-half">' +
                             '<input class="govuk-input govuk-!-width-one-half"' +
                             'id="sicCode"' +
                             'maxlength="5" ' +
                             'name = "sicCodes[' + idCount + '].sicCode"' +
                             'type="text"/>' +
                             '</div>' +
                             '<div>' +
                                 '<button class="button-clear alignright" data-remove-row-man="sicCode"' +
                                           'type="button" name="remove-sic-code"' +
                                               'th:value="' + idCount + ' "' +
                                             'th:id="remove-sic-code-row"' + idCount + ' ">Remove' +
                                  '</button>' +
                            '</div>' +
                          '</div>'
      jQuery('.sic-code-row').last().after(html)
      jQuery('.sic-code-' + idCount).val('')
    },
    reindexRows: function (rowSelector) {
      jQuery(rowSelector + ' [name]').each(function () {
        var inst = jQuery(this)
        if (inst.prop('name').indexOf('[') !== -1) {
          var thisIndex = inst.closest(rowSelector).index(rowSelector)
          var oldAttr = inst.attr('name')
          var oldAttrName = oldAttr.split('[')[0]
          var oldAttrElement = oldAttr.split(']')[1]
          inst.prop('name', oldAttrName + '[' + thisIndex + ']' + oldAttrElement)
        }
      })
      jQuery(rowSelector + ' [data-remove-row-man]').each(function () {
        var inst = jQuery(this)
        var thisIndex = inst.closest(rowSelector).index(rowSelector)
        inst.val(thisIndex)
      })
    },
    addExecOfficer: function () {
      var idCount = 0
      if (jQuery('.exec-officer-row').length) {
        idCount = parseInt(jQuery('.exec-officer-row[id^=exec-officer-row-]').last().attr('id').split('exec-officer-row-')[1], 10) + 1
      }
      var html = '<div class="govuk-grid-row govuk-!-margin-top-6 exec-officer-row" id="exec-officer-row-' + idCount + '">' +
                               '<div class="govuk-grid-column-one-half">' +
                                   '<input class="govuk-input govuk-!-width-one-half"' +
                                   'id="execOfficer"' +
                                    'type="text" ' +
                                    'maxlength="255" ' +
                                    'name = "executiveOfficers[' + idCount + '].name"/>' +
                                    '</div>' +
                                    '<div>' +
                                       '<button class="button-clear alignright" data-remove-row-man="execOfficer"' +
                                                 'type="button" name="remove-exec-officer"' +
                                                     'th:value="' + idCount + ' "' +
                                                   'th:id="remove-exec-officer-row"' + idCount + ' ">Remove' +
                                        '</button>' +
                                  '</div>' +
                                '</div>'
      jQuery('.exec-officer-row').last().after(html)
      jQuery('.exec-officer-' + idCount).val('')
    }
  }
})()
