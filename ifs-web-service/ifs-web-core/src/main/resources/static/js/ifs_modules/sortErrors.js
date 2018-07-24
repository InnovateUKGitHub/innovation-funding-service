// Sorts errors to match order of fields on page
// Error sorting order: global errors (no link) -> errors with dead links -> errors in link order
// within the same link errors are sorted alphabetically
// if there is no error list code exits immediately
IFS.core.sortingErrors = (function () {
  'use strict'

  return {
    init: function () {
      IFS.core.sortingErrors.sortList()
    },

    sortList: function () {
      var errorContainer = jQuery('.error-summary-list')[0]
      if (!errorContainer) {
        // if theres no error summary return
        return
      }
      var errors = errorContainer.getElementsByTagName('LI')

      // for each error grab the field (the hash of the link)
      var fieldIds = []
      for (var i = 0; i < errors.length; i++) {
        var hash = IFS.core.sortingErrors.getHashFromElement(errors[i])
        if (hash) {
          fieldIds.push('#' + window.CSS.escape(hash.substring(1)))
        }
      }
      // use jquery search on all hash arguments to return elements in the order they appear on the page
      var elementsInOrder = jQuery(fieldIds.join(','))

      // convert the elements back to IDs of the pattern #fieldID
      var idsInOrder = []
      for (var j = 0; j < elementsInOrder.length; j++) {
        idsInOrder.push('#' + elementsInOrder[j].id)
      }

      // use jQuery sort syntax to so sort the elements
      var jQueryErrors = jQuery('li', jQuery(errorContainer))
      jQueryErrors.sort(function (a, b) {
        return IFS.core.sortingErrors.compareHash(a, b, idsInOrder)
      })

      // reattach sorted elements
      jQuery.each(jQueryErrors, function (index, row) {
        jQuery(errorContainer).append(row)
      })
    },
    // compares to errors, if they have matching hash, returns alphabetical
    compareHash: function (a, b, idsInOrder) {
      var aIndex = IFS.core.sortingErrors.getSortOrder(IFS.core.sortingErrors.getHashFromElement(a), idsInOrder)
      var bIndex = IFS.core.sortingErrors.getSortOrder(IFS.core.sortingErrors.getHashFromElement(b), idsInOrder)

      if (aIndex !== bIndex) {
        return aIndex - bIndex
      }
      return a.textContent.localeCompare(b.textContent)
    },

    // returns hash of hyperlink of error or null if not found
    getHashFromElement: function (element) {
      if (jQuery(element).find('a').length) {
        return jQuery(element).find('a')[0].hash
      }
      return null
    },

    // returns order of hash on the page or -1 if not on page, or -2 if no hash provided.
    getSortOrder: function (hash, idsInOrder) {
      if (hash != null) {
        return idsInOrder.indexOf(hash)
      }
      return -2
    }
  }
})()
