// creates additional <select> elements using the <optgroup> for progressive filters of long drop-downs
IFS.core.progressiveGroupSelect = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      progressiveGroupSelect: '[data-progressive-group-select]'
    },
    init: function () {
      s = this.settings

      jQuery(s.progressiveGroupSelect).each(function () {
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

        IFS.core.progressiveGroupSelect.createParentSelect(el)
      })
    },
    createParentSelect: function (el) {
      // create a <select> to pre-filter the original dropdown
      var parentSelectTitle = el.attr('data-progressive-group-select')
      var parentSelectInstruction = el.attr('data-progressive-group-select-instruction')
      var parentSelect = jQuery('<select class="form-control width-full js-progressive-group-select" aria-label="' + parentSelectTitle + '"><option value="">' + parentSelectInstruction + '</option></select>')
      var optgroupsArray = el.find('optgroup')
      var optionsArray = []
      var parentOptionsArray = []
      var selectedOption = el.find('option:selected')

      optgroupsArray.each(function () {
        // create new options for each optgroup element
        parentOptionsArray.push('<option value="' + jQuery(this).attr('label') + '">' + jQuery(this).attr('label') + '</option>')

        jQuery('option', jQuery(this)).each(function (index, element) {
          // add all option to array for use later
          optionsArray.push(element)
        })
      })

      // insert the new options into the new <select>
      parentSelect.append(parentOptionsArray)

      // select parent option if an option is already selected
      if (selectedOption.val() !== '') {
        jQuery('option[value="' + selectedOption.attr('data-optgroup-label') + '"]', parentSelect).prop('selected', true)

        IFS.core.progressiveGroupSelect.update(el, parentSelect, optionsArray)
      }

      // bind event handlers
      parentSelect.on('change', function () {
        IFS.core.progressiveGroupSelect.update(el, parentSelect, optionsArray)
      })

      // remove the <optgroup> and <option>, replaced when inserting filtered options
      jQuery('optgroup', el).remove()

      // update the DOM with the new <select>
      el.before(parentSelect)
    },
    update: function (el, parentSelect, optionsArray) {
      // event handler for changes to the <select> element
      var selectedOption = parentSelect.val()

      // update the state of the <select> fields
      if (selectedOption === '') {
        // remove all options except the first
        jQuery('option', el).slice(1).remove()

        // reset to first option
        jQuery('option:first', el).attr('selected', 'selected')

        jQuery(el).prop('disabled', true)
      } else {
        // remove all options except the first
        jQuery('option', el).slice(1).remove()

        // insert applicable options in second <select> that match select parent
        for (var i = 0; i < optionsArray.length; i++) {
          if (jQuery(optionsArray[i]).data('optgroup-label') === selectedOption) {
            jQuery(el).append(optionsArray[i])
          }
        }

        jQuery(el).prop('disabled', false)
      }
    }
  }
})()
