// Set up the handlers for adding and removing Cost Category costs rows
IFS.core.financeRowForm = (function () {
  'use strict'

  return {
    init: function () {
      IFS.core.financeRowForm.backForwardCacheReload()
      jQuery('body').on('click', '[name="add_row"]', function (e) {
        e.preventDefault()
        IFS.core.financeRowForm.backForwardCacheInvalidate()
        IFS.core.financeRowForm.addRow(this, e)
      })
      jQuery('body').on('click', '[name="remove_row"]', function (e) {
        e.preventDefault()
        IFS.core.financeRowForm.backForwardCacheInvalidate()
        IFS.core.financeRowForm.removeRow(this, e)
      })
      jQuery('body').on('persistUnsavedRow', function (event, name, newFieldId) {
        IFS.core.financeRowForm.persistUnsavedRow(name, newFieldId)
      })
      jQuery('body').on('click', 'form button', function (e) {
        var button = jQuery(this)
        if (button.closest('form').attr('data-save-status') === 'progress') {
          e.preventDefault()
          setTimeout(function () {
            button.click()
          }, 500)
        }
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
        IFS.core.financeRowForm.hideOrShowAddButton(addRowButton)
        if (target.is('.govuk-accordion')) {
          IFS.core.accordion.get(target.get(0)).sectionChange()
        }
        IFS.core.mirrorElements.init()
      })
    },
    removeRow: function (el, event) {
      var removeButton = jQuery(el)
      var id = removeButton.val()
      var addRowButton = removeButton.closest('.govuk-accordion__section-content').find('[name="add_row"]')
      var removeRow = function () {
        removeButton.closest('[data-repeatable-row]').remove()
        jQuery('body').trigger('recalculateAllFinances').trigger('updateSerializedFormState')
        IFS.core.financeRowForm.hideOrShowAddButton(addRowButton)
        var target = jQuery(removeButton.data('repeatable-rowcontainer'))
        if (target.is('.govuk-accordion')) {
          IFS.core.accordion.get(target.get(0)).sectionChange()
        }
        IFS.core.mirrorElements.init()
      }
      event.preventDefault()
      if (id === '' || id.indexOf('unsaved') !== -1) {
        removeRow()
        return
      }
      jQuery.ajaxProtected({
        url: IFS.core.financeRowForm.getUrl(el) + '/remove-row/' + id,
        method: 'POST'
      }).done(function (data) {
        removeRow()
      })
    },
    hideOrShowAddButton: function (addRowButton) {
      if (addRowButton) {
        var target = jQuery(addRowButton.attr('data-repeatable-rowcontainer'))
        var maximum = addRowButton.data('repeatable-row-maximum')
        if (maximum && target.length) {
          var size = target.find('[data-repeatable-row]').length
          addRowButton.toggleClass('govuk-visually-hidden', size >= maximum)
        }
      }
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
      var start = name.indexOf('[unsaved-')
      if (start !== -1) {
        var unsavedId = name.substring(start + 1, name.indexOf(']', start))
        var path = name.substring(0, name.indexOf(']', start) + 1)
        var row = false
        jQuery('[name^="' + path + '"]').each(function () {
          var input = jQuery(this)
          if (input.attr('name').endsWith('.costId') || input.attr('name').endsWith('.id')) {
            input.val(newFieldId)
          }
          input.attr('name', input.attr('name').replace(unsavedId, newFieldId))
          if (!row) {
            row = input.closest('[data-repeatable-row]')
          }
        })
        // update remove button
        row.find('[name="remove_row"]').val(newFieldId)
      }
    }
  }
})()
