IFS.core.tableSorter = (function () {
  'use strict'
  var s
  return {
    settings: {
      tableSorterElement: '[data-table-sorter]'
    },
    init: function () {
      s = this.settings
      jQuery(s.tableSorterElement).tablesorter()
    }
  }
})()
