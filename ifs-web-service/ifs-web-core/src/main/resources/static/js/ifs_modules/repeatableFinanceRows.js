// Set up the handlers for adding and removing Cost Category costs rows
IFS.core.repeatableFinanceRows = (function () {
  'use strict'

  return {
    init: function () {
      IFS.core.repeatableFinanceRows.backForwardCacheReload()
      jQuery('body').on('click', '[data-repeatable-rowcontainer]', function (e) {
        e.preventDefault()
        IFS.core.repeatableFinanceRows.backForwardCacheInvalidate()
        IFS.core.repeatableFinanceRows.addRow(this, e)
      })
      jQuery('body').on('click', '.js-remove-row', function (e) {
        IFS.core.repeatableFinanceRows.backForwardCacheInvalidate()
        IFS.core.repeatableFinanceRows.removeRow(this, e)
      })

      jQuery('body').on('persistUnsavedRow', function (event, name, newFieldId) {
        IFS.core.repeatableFinanceRows.persistUnsavedRow(name, newFieldId)
      })
      var buttonHtml = '<button type="submit" name="remove_cost" class="buttonlink js-remove-row" value="">Remove</button>'
      jQuery('[data-repeatable-row] .buttonplaceholder').replaceWith(buttonHtml)
      jQuery('body').trigger('updateSerializedFormState')
    },
    getAjaxUrl: function (el) {
      var inst = jQuery(el)
      var elValue = inst.val()
      var elName = inst.prop('name')
      var hasValue = typeof (elValue) !== 'undefined'
      var hasName = typeof (elName) !== 'undefined'

      if (hasName && hasValue) {
        var host = window.location.protocol + '//' + window.location.host
        var url = ''
        if (jQuery('#application_id').length === 1) {
          url = host + '/application/' + jQuery('#application_id').val() + '/form/' + elName + '/' + elValue
        }
        if (jQuery('#projectId').length === 1) {
          url = host + '/project-setup-management/project/' + jQuery('#projectId').val() + '/finance-check/organisation/' + jQuery('#organisationId').val() + '/eligibility/' + elName + '/' + elValue
        }
      }

      return url
    },
    addRow: function (el, event) {
      var addRowButton = jQuery(el)
      var url = IFS.core.repeatableFinanceRows.getAjaxUrl(el)
      if (url.length) {
        event.preventDefault()
        jQuery.ajaxProtected({
          url: url,
          beforeSend: function () {
            addRowButton.before('<span class="form-hint">Adding a new row</span>')
          },
          cache: false
        }).done(function (data) {
          var target = jQuery(addRowButton.attr('data-repeatable-rowcontainer'))
          var buttonHtml = '<button type="submit" name="remove_cost" class="buttonlink js-remove-row" value="">Remove</button>'
          // find the .buttonplaceholder span and add empty valued remove_cost as it hasn't been persisted yet but can be removed client side
          // (we don't do this on the server because of non-js situation)
          addRowButton.prevAll('.form-hint').remove() // remove hint text
          data = jQuery(data)
          data.find('.buttonplaceholder').replaceWith(buttonHtml)
          target.append(data)

          jQuery('body').trigger('updateSerializedFormState')
          var appendRow = data.find('[type="number"][name],[type="text"][name],[type="email"][name],[type="radio"][name],textarea[name]').first().attr('name')
          if (typeof (appendRow) !== 'undefined') {
            jQuery('[name=' + appendRow + ']').focus()
          }
        })
      }
    },
    removeRow: function (el, event) {
      var removeButton = jQuery(el)
      var rowValue = removeButton.val()
      if (rowValue !== '') {
        var url = IFS.core.repeatableFinanceRows.getAjaxUrl(el)
        if (url.length) {
          event.preventDefault()
          jQuery.ajaxProtected({
            url: url
          }).done(function (data) {
            data = jQuery.parseJSON(data)
            if (data.status === 'OK') {
              jQuery('[data-repeatable-row=' + rowValue + ']').remove()
              jQuery('body').trigger('recalculateAllFinances').trigger('updateSerializedFormState')
            }
          })
        }
      } else {
        var closestRow = removeButton.closest('[data-repeatable-row]')
        if (closestRow.length) {
          jQuery('[data-repeatable-row=' + closestRow.attr('data-repeatable-row') + ']').remove()
          jQuery('body').trigger('updateSerializedFormState')
        }
      }
    },
    backForwardCacheReload: function () {
      // INFUND-2965 ajax results don't show when using the back button on the page after
      var input = jQuery('#cacheTest')
      if (input.val() !== '') {
        // the page has been loaded from the cache as the #cachetest has a value
        // equivalent of persisted == true
        jQuery('#cacheTest').val('')
        window.location.reload()
      }
    },
    backForwardCacheInvalidate: function () {
      // change the input value so that we can detect
      // if the page is reloaded from cache later
      jQuery('#cacheTest').val('cached')
    },
    persistUnsavedRow: function (name, newFieldId) {
      // transforms unpersisted rows to persisted rows by updating the name attribute
      var nameDashSplit = name.split('-')
      var unsavedCostId = nameDashSplit.length > 2 ? nameDashSplit[3] : null
      var fieldsForUnsavedCost = jQuery('[data-repeatable-row] [name*="' + unsavedCostId + '"]')

      fieldsForUnsavedCost.each(function () {
        var oldName = jQuery(this).prop('name')
        var thisFieldNameSplit = oldName.split('-')
        var newName = thisFieldNameSplit[0] + '-' + thisFieldNameSplit[1] + '-' + thisFieldNameSplit[2] + '-' + newFieldId
        jQuery(this).prop('name', newName)
        jQuery('[data-errorfield="' + oldName + '"]').attr('data-errorfield', newName)
      })
      // add the button
      var row = jQuery('[data-repeatable-row="' + unsavedCostId + '"]')
      var button = row.find('.js-remove-row')
      if (button.length) {
        button.val(newFieldId)
        // set the repeatable row id for referencing the removal, leave the original id as that is used for the promise
        row.attr('data-repeatable-row', newFieldId)
      }
    }
  }
})()
