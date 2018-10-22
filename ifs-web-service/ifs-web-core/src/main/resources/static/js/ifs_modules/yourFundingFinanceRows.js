// Set up the handlers for adding and removing Cost Category costs rows
IFS.core.yourFunding = (function () {
  'use strict'

  return {
    init: function () {
      IFS.core.yourFunding.backForwardCacheReload()
      jQuery('body').on('click', '[name="add_other_funding"]', function (e) {
        e.preventDefault()
        IFS.core.yourFunding.backForwardCacheInvalidate()
        IFS.core.yourFunding.addRow(this, e)
      })
      jQuery('body').on('click', '[name="remove_other_funding"]', function (e) {
        e.preventDefault()
        IFS.core.yourFunding.backForwardCacheInvalidate()
        IFS.core.yourFunding.removeRow(this, e)
      })
      jQuery('body').on('persistUnsavedRow', function (event, name, newFieldId) {
        IFS.core.yourFunding.persistUnsavedRow(name, newFieldId)
      })
    },
    getBaseUrl: function (el) {
      var inst = jQuery(el)
      var form = inst.closest('form')
      return '/application/' + form.data('application-id') + '/form/your-funding/' + form.data('section-id')
    },
    addRow: function (el, event) {
      var addRowButton = jQuery(el)
      var url = IFS.core.yourFunding.getBaseUrl(el)
      if (url.length) {
        event.preventDefault()
        jQuery.ajaxProtected({
          url: url + '/add-row',
          method: 'POST',
          beforeSend: function () {
            addRowButton.before('<span class="govuk-hint">Adding a new row</span>')
          },
          cache: false
        }).done(function (data) {
          var target = jQuery(addRowButton.data('repeatable-rowcontainer'))
          var emptyRow = target.find('[name*="otherFundingRows[empty]"]')
          if (emptyRow.length) {
            emptyRow.closest('tr').before(data)
          } else {
            target.append(data)
          }
          addRowButton.prevAll('.govuk-hint').remove()
          jQuery('body').trigger('updateSerializedFormState')
        })
      }
    },
    removeRow: function (el, event) {
      var removeButton = jQuery(el)
      var rowValue = removeButton.val()
      var url = IFS.core.yourFunding.getBaseUrl(el)
      if (url.length) {
        event.preventDefault()
        jQuery.ajaxProtected({
          url: url + '/remove-row/' + rowValue,
          method: 'POST'
        }).done(function (data) {
          removeButton.closest('tr').remove()
          jQuery('body').trigger('recalculateAllFinances').trigger('updateSerializedFormState')
        })
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
      if (name.indexOf('otherFundingRows[empty]') !== -1) {
        jQuery('[name^="otherFundingRows[empty]"]').each(function () {
          var input = jQuery(this)
          if (input.attr('name') === 'otherFundingRows[empty].costId') {
            input.val(newFieldId)
          }
          input.attr('name', input.attr('name').replace('otherFundingRows[empty]', 'otherFundingRows[' + newFieldId + ']'))
        })
        jQuery('[name="remove_other_funding"][value="empty"]').val(newFieldId)
      }
    }
  }
})()
