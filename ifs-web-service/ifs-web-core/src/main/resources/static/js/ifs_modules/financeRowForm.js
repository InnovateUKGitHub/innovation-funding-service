// Set up the handlers for adding and removing Cost Category costs rows
IFS.core.financeRowForm = (function () {
  'use strict'

  return {
    init: function () {
      IFS.core.financeRowForm.backForwardCacheReload()
      jQuery('body').on('click', '[name="add_cost"]', function (e) {
        e.preventDefault()
        IFS.core.financeRowForm.backForwardCacheInvalidate()
        IFS.core.financeRowForm.addRow(this, e)
      })
      jQuery('body').on('click', '[name="remove_cost"]', function (e) {
        e.preventDefault()
        IFS.core.financeRowForm.backForwardCacheInvalidate()
        IFS.core.financeRowForm.removeRow(this, e)
      })
      jQuery('body').on('persistUnsavedRow', function (event, name, newFieldId) {
        IFS.core.financeRowForm.persistUnsavedRow(name, newFieldId)
      })
    },
    getUrl: function (el) {
      return jQuery(el).closest('form').data('row-operation-url')
    },
    addRow: function (el, event) {
      var addRowButton = jQuery(el)
      var rowValue = addRowButton.val()
      event.preventDefault()
      jQuery.ajaxProtected({
        url: IFS.core.financeRowForm.getUrl(el) + '/add-row/' + rowValue,
        method: 'POST',
        beforeSend: function () {
          addRowButton.before('<span class="govuk-hint">Adding a new row</span>')
        },
        cache: false
      }).done(function (data) {
        var target = jQuery(addRowButton.data('repeatable-rowcontainer'))
        target.append(data)
        addRowButton.prevAll('.govuk-hint').remove()
        jQuery('body').trigger('updateSerializedFormState')
      })
    },
    removeRow: function (el, event) {
      var removeButton = jQuery(el)
      var rowValue = removeButton.val()
      var id = rowValue.split(',')[0]
      event.preventDefault()
      jQuery.ajaxProtected({
        url: IFS.core.financeRowForm.getUrl(el) + '/remove-row/' + id,
        method: 'POST'
      }).done(function (data) {
        removeButton.closest('[data-repeatable-row]').remove()
        jQuery('body').trigger('recalculateAllFinances').trigger('updateSerializedFormState')
      })
    },
    backForwardCacheReload: function () {
      // INFUND-2965 ajax results don't show when using the back button on the page after
      var input = jQuery('#cacheTest')
      if (input.length && input.val() !== '') {
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
      if (name.indexOf('[empty]') !== -1) {
        var path = IFS.core.financeRowForm.getPathToEmptyRow(name)
        jQuery('[name^="' + path + '"]').each(function () {
          var input = jQuery(this)
          if (input.attr('name').endsWith('costId')) {
            input.val(newFieldId)
          }
          input.attr('name', input.attr('name').replace('[empty]', '[' + newFieldId + ']'))
        })
        // update remove button
        jQuery('[name="remove_cost"][value*="empty"]').val(newFieldId)
      }
    },
    getPathToEmptyRow: function (name) {
      return name.substring(0, name.indexOf('[empty]') + '[empty]'.length)
    }
  }
})()
