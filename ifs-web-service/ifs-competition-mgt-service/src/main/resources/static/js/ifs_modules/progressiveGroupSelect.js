// creates additional <select> elements using the <optgroup> for progressive filters of long drop-downs
IFS.competitionManagement.progressiveGroupSelect = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      progressiveGroupSelect: '[data-progressive-group-select]'
    },
    init: function () {
      s = this.settings

      jQuery.each(jQuery(s.progressiveGroupSelect), function () {
        var el = jQuery(this)

        // disable the second <select> if an option is not already selected
        if (el.find('option:selected').val() === '') {
          el.prop('disabled', true)
        }

        // add a label to each option for identifying the optgroup parent
        jQuery('optgroup', el).each(function () {
          var optgroup = jQuery(this)
          var optgroupLabel = optgroup.attr('label')

          jQuery('option', optgroup).each(function () {
            jQuery(this).attr('data-optgroup-label', optgroupLabel)
          })
        })

        IFS.competitionManagement.progressiveGroupSelect.createParentSelect(el)

        // remove the <optgroup> wrappers
        jQuery('optgroup option', el).unwrap('optgroup')
      })
    },
    createParentSelect: function (el) {
      // create a <select> to pre-filter the original dropdown
      var parentSelectTitle = el.attr('data-progressive-group-select')
      var parentSelectInstruction = el.attr('data-progressive-group-select-instruction')
      var parentSelect = jQuery('<select class="form-control width-full js-progressive-group-select" aria-label="' + parentSelectTitle + '"><option value="">' + parentSelectInstruction + '</option></select>')
      var optgroupsArray = el.find('optgroup')
      var optionsArray = []

      jQuery(optgroupsArray).each(function () {
        // create new options for each optgroup element
        optionsArray.push('<option value="' + jQuery(this).attr('label') + '">' + jQuery(this).attr('label') + '</option>')
      })

      // insert the new options into the new <select>
      parentSelect.append(optionsArray)

      // select parent option if an option is already selected
      if (el.find('option:selected').val() !== '') {
        var selectedOption = jQuery('option:selected', el).attr('data-optgroup-label')

        jQuery('option[value="' + selectedOption + '"]', parentSelect).prop('selected', true)
      }

      // bind event handlers
      parentSelect.on('change', function () {
        IFS.competitionManagement.progressiveGroupSelect.update(el, parentSelect)
      })

      // update the DOM with the new <select>
      el.before(parentSelect)
    },
    update: function (el, parentSelect) {
      // event handler for changes to the <select> element
      var selectedOption = parentSelect.val()

      // reset any previously selected choice
      jQuery('option:first', el).prop('selected', true)

      // update the state of the <select> fields
      if (selectedOption === '') {
        jQuery(el).prop('disabled', true)
      } else {
        // hide all options
        jQuery('option', el).hide()

        // show applicable options in second <select>
        jQuery('option:first, option[data-optgroup-label="' + selectedOption + '"]', el).show()

        jQuery(el).prop('disabled', false)
      }
    }
  }
})()
