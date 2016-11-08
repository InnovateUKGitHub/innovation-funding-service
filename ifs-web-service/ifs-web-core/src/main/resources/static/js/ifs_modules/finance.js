//Innovation Funding Services javascript for calculating the finance fields
IFS.core.finance = (function() {
  "use strict";
  return {
    MathOperation : {
      '+': function(x, y) { return x + y; },
      '-': function(x, y) { return x - y; },
      '*': function(x, y) { return x * y; },
      '/': function(x, y) {
        if(y === 0) {
          return 0;
        }
        return x / y;
      },
      'negativeRoundUp': function(x, y) { return x < 0 ? 0 : x;  } //jshint ignore:line
    },
    init : function() {
      IFS.core.finance.bindCalculationActionToFields(); // Bind calculations
      jQuery('body').trigger('recalculateAllFinances');
    },
    bindCalculationActionToFields : function() {
      //we watch for changes in inputs as a calculation always starts with an input
      jQuery('body').on('change', 'input', function() {
        //if the calculation field is not binded we bind this field to the selector that is defined in the field
        if(jQuery('[data-calculation-fields]:not([data-calculation-binded])').length){
          jQuery('[data-calculation-fields]:not([data-calculation-binded])').each(function() {
            var element = jQuery(this);
            var fields = element.attr('data-calculation-fields');

            jQuery(document).on('change updateFinances', fields, function() {
              IFS.core.finance.doMath(element, fields.split(','));
            });
            //we only want to bind a field once
            element.attr('data-calculation-binded', '');
          });
        }
      });
      //force recalculate, only used for removal of finances
      jQuery('body').on('recalculateAllFinances', function() {
        if(jQuery('[data-calculation-fields]').length){
          jQuery('[data-calculation-fields]').each(function() {
            var element = jQuery(this);
            var fields = element.attr('data-calculation-fields');
            IFS.core.finance.doMath(element, fields.split(','));
          });
        }
      });
    },
    getElementValue : function(element) {
      var rawValue = jQuery(element).attr("data-calculation-rawvalue");

      //would be better to force all fields to have a raw value at the start rather than these fallback cases
      if (typeof(rawValue) !== 'undefined') {
        return parseFloat(rawValue);
      } else {
        if ((typeof(jQuery(element).val()) !== 'undefined') && (jQuery(element).val().length)) {
          var displayValue = jQuery(element).val().replace(',', '');
          var parsed = displayValue.indexOf('£ ') === 0 ? displayValue.substring(2) : displayValue;
          return parseFloat(parsed);
        }
      }
      return parseFloat(0);
    },
    doMath : function(element, calcFields) {
      var operation = element.attr('data-calculation-operations').split(',');
      var values = [];
      jQuery.each(calcFields, function(index, field) {
        if(jQuery.isNumeric(field)){
          //we use a static number not a selector to another field
          values.push(parseFloat(field));
        }
        else if(jQuery(field).length > 1){
          //we use a selector with multiple inputs and get the value
          jQuery.each(jQuery(field), function(index, field2) {
            values.push(IFS.core.finance.getElementValue(field2));
          });
        }
        else {
          //we use a selector with one input
          values.push(IFS.core.finance.getElementValue(field));
        }
      });

      var calculatedValue;
      if(values.length === 1) {
        calculatedValue=values[0];
      }
      else {
        calculatedValue = IFS.core.finance.MathOperation[operation[0]](values[0], values[1]);
      }
      //one operation and more values, all get the same operation
      if((operation.length == 1) && (values.length > 2)) {
        for (var i = 2; i < values.length; i++) {
          //console.log('round:',i,typeof(operation[0]),operation[0],typeof(calculatedValue),calculatedValue,typeof(values[i]),values[i],values)
          calculatedValue = IFS.core.finance.MathOperation[operation[0]](calculatedValue, values[i]);
        }
      }
     //multiple operations and multiple values
      else if((operation.length > 1) && (values.length > 2)) {
        for (var j = 1; j < operation.length; j++) {
          //console.log('round:',i,operation[i],calculatedValue,values[i+1])
          calculatedValue = IFS.core.finance.MathOperation[operation[j]](calculatedValue, values[j+1]);
        }
      }
      element.attr("data-calculation-rawvalue", calculatedValue);

      var formatted = IFS.core.finance.formatCurrency(Math.round(calculatedValue));
      if (element.is('input')) {
        element.val(formatted);
      } else {
        element.text(formatted);
      }
      element.trigger('change');
    },
    formatCurrency: function(total) {
      total = parseFloat(total, 10);
      total = total.toFixed();
      total = total.replace(/(\d)(?=(\d{3})+\b)/g, "$1,");
      return '£ ' + total.toString();
    }
  };
})();
