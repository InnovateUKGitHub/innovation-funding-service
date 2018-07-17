IFS.core.sortingErrors = (function () {
  'use strict'

  return {
    init: function () {
    	IFS.core.sortingErrors.sortList();
    },

	sortList: function () {
      var ul = jQuery('.error-summary-list')[0]
      if (!ul) {
        return
      }
      var collection = ul.getElementsByTagName('LI')

      var ids = []
      // forEach error grab the field
      for (var i = 0; i < collection.length; i++) {
        var hash = IFS.core.sortingErrors.getHashFromElement(collection[i])
        if (hash) {
          ids.push(hash)
        }
      }
      var elementsInOrder = jQuery(ids.join(','))
      // select returns fields in order.
      var idsInOrder = []
      for (var j = 0; j < elementsInOrder.length; j++) {
        idsInOrder.push('#' + elementsInOrder[j].id)
      }

      var listLi = jQuery('li', jQuery(ul))
      listLi.sort(function (a, b) {
        return IFS.core.sortingErrors.compareHash(a, b, idsInOrder)
      })
      jQuery.each(listLi, function (index, row) {
        jQuery(ul).append(row)
      })
    },
    compareHash: function (a, b, idsInOrder) {
      var aIndex = IFS.core.sortingErrors.getSortOrder(IFS.core.sortingErrors.getHashFromElement(a), idsInOrder)
      var bIndex = IFS.core.sortingErrors.getSortOrder(IFS.core.sortingErrors.getHashFromElement(b), idsInOrder)

      if (aIndex !== bIndex) {
        return aIndex - bIndex
      }
      return a.textContent.localeCompare(b.textContent)
    },

    getHashFromElement: function (element) {
      if (jQuery(element).find('a').length) {
        return jQuery(element).find('a')[0].hash
      }
      return null
    },

    getSortOrder: function (hash, idsInOrder) {
      if (hash != null) {
        return idsInOrder.indexOf(hash)
      }
      return -2
    }
  }
})()