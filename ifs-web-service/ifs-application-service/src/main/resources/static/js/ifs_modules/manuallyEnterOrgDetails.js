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
      }
      jQuery('body').trigger('updateSerializedFormState')
      return false
    },
    handleRemoveRowMan: function (e) {
      var inst = jQuery(e)
      console.log(inst.closest('[id^="sic-code-row-"]'))
      inst.closest('[id^="sic-code-row-"]').remove()
      IFS.manuallyEnter.reindexRows('[id^="sic-code-row-"]')
    },
    addSicCode: function () {
      var idCount = 0
      if (jQuery('.sic-code-row').length) {
        // id and for attributes have to be unique, gaps in count don't matter however I rather don't reindex all attributes on every remove, so we just higher the highest.
        idCount = parseInt(jQuery('.sic-code-row[id^=sic-code-row-]').last().attr('id').split('sic-code-row-')[1], 10) + 1
      }
      var html = '<div class="govuk-grid-row sic-code-row" id="sic-code-row-' + idCount + '">' +
                         '<div class="govuk-grid-column-one-half">' +
                           '<div class="govuk-form-group">' +
                             '<input>ello</input>' +
                              '</div>' +
                              '<div>' +
                                 '<button class="button-clear alignright" data-remove-row-man="sicCode"' +
                                           'type="button" name="remove-sic-code"' +
                                               'th:value="'  + idCount +'"' +
                                             'th:id="remove-sic-code-row"'  + idCount +'">Remove' +
                                  '</button>' +
                                '</div>' +
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
    }
  }
})()
