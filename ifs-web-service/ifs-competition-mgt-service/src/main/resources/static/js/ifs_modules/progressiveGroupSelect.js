// creates additional <select> elements using the <optgroup> for progressive filters of long drop-downs
IFS.competitionManagement.progressiveGroupSelect = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      progressiveGroupSelect: '.progressive-group-select'
    },
    init: function () {
      s = this.settings

      console.log('init')
      console.log(jQuery(s.progressiveGroupSelect))

      jQuery('body').on('change', jQuery(s.progressiveGroupSelect), function (e) {
        console.log('changed')
        IFS.competitionManagement.progressiveGroupSelect.update(this, e)
      })
    },
    update: function (el) {
      console.log(el)
    }
  }
})()
