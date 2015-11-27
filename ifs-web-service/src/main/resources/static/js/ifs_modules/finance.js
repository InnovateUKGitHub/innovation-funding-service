/* jshint strict: true, undef: true, unused: true */
/* globals jQuery : false */

//Innovation Funding Services javascript for calculating the finance fields
var ifs_finance = (function(){
    "use strict";
    return {
         MathOperation : {
                '+': function (x, y) { return x + y; },
                '-': function (x, y) { return x - y; },
                '*': function (x, y) { return x * y; },
                '/': function (x, y) {
                    if(y === 0) {
                        return 0;
                    }
                    return x / y;
                }
            },
            init : function(){
                ifs_finance.bindCalculationActionToFields(); // Bind calculations
            },
            bindCalculationActionToFields : function(){

                var updateBasedOnDataCalculationFieldsIfNecessary = function(dependantField, input) {

                    var dependencySelectors = dependantField.attr("data-calculation-fields").split(',');

                    var matchingDependenciesInArrays = dependencySelectors.map(function(selector) {
                        return jQuery(selector);
                    });

                    var matchingDependencies = matchingDependenciesInArrays.reduce(function(combined, currentJqueryObject) {
                        return jQuery.merge(combined, currentJqueryObject);
                    });

                    var idFn = function(element) {
                      if (element instanceof jQuery) {
                          return element.attr('id');
                      }
                      return jQuery(element).attr('id');
                    };

                    if (matchingDependencies.toArray().map(idFn).indexOf(idFn(input)) !== -1) {
                        ifs_finance.doMath(dependantField, matchingDependencies);
                    }
                };

                jQuery('body').on('change', 'input', function() {

                    var input = jQuery(this);

                    var fieldsDependantOnOthers = jQuery('[data-calculation-fields]');

                    fieldsDependantOnOthers.each(function(i, element) {
                        var dependantField = jQuery(element);
                        updateBasedOnDataCalculationFieldsIfNecessary(dependantField, input);
                    });

                });
            },
            doMath : function(element,calcFields){
                var operation = element.attr('data-calculation-operations').split(',');
                var values = [];

                for (var i = 0; i < calcFields.length; i++) {
                    var input = jQuery(calcFields[i]);
                    var rawValue = input.attr("data-calculation-rawvalue");

                    // TODO DW - would be better to force all fields to have a raw value at the start rather than these fallback cases
                    if (typeof rawValue !== 'undefined') {
                        values.push(parseFloat(rawValue));
                    } else {
                        var displayValue = input.val();
                        if (typeof displayValue !== 'undefined' && displayValue.length > 0) {
                            var parsed = displayValue.indexOf('£ ') === 0 ? displayValue.substring(2) : displayValue;
                            values.push(parseFloat(parsed));
                        } else {
                            values.push(parseFloat(0));
                        }
                    }
                }

                var calculatedValue;
                if(values.length === 1) {
                    calculatedValue=values[0];
                }
                else {
                    calculatedValue = ifs_finance.MathOperation[operation[0]](values[0],values[1]);
                }
                //one operation and more values, all get the same operation

                if((operation.length == 1) && (values.length > 2)) {
                    for (i = 2; i < values.length; i++) {
                        //console.log('round:',i,typeof(operation[0]),operation[0],typeof(calculatedValue),calculatedValue,typeof(values[i]),values[i],values)
                        calculatedValue = ifs_finance.MathOperation[operation[0]](calculatedValue,values[i]);
                    }
                }
               //multiple operations and multiple values
                else if((operation.length > 1) && (values.length > 2)) {
                    for (i = 1; i < operation.length; i++) {
                        // console.log('round:',i,operation[i],calculatedValue,values[i+1])
                        calculatedValue = ifs_finance.MathOperation[operation[i]](calculatedValue,values[i+1]);
                    }
                }
                element.attr("data-calculation-rawvalue",calculatedValue);

                var formatted = ifs_finance.formatCurrency(Math.round(calculatedValue));
                if (element.is('span')) {
                    element.text(formatted);
                } else {
                    element.val(formatted);
                }
                element.trigger('change');
            },

            formatCurrency: function(total) {
                total = parseFloat(total, 10);
                total = total.toFixed();
                total = total.replace(/(\d)(?=(\d{3})+\b)/g, "$1,");
                return '£ ' + total.toString();
            },
            mirrorInputs : function(){
                // Bind input and output fields together to mirror values of the "input" fields in the "outputs"
                jQuery('body').on('change', '[data-binding-input]', function() {
                    var input = jQuery(this);
                    var bindingId = input.attr('data-binding-input');
                    var bindingOutputs = jQuery('[data-binding-output="' + bindingId + '"]');
                    bindingOutputs.each(function() {
                        var output = jQuery(this);
                        if (output.is('span')) {
                            output.text(input.val());
                        } else {
                            output.val(input.val());
                        }
                    });
                });
            },
            sectionInfoInHeader : function(){
                // Move section elements into the section header

                jQuery('.place-in-header').each(function() {
                    var elementToMove = jQuery(this);
                    var subsectionId = elementToMove.attr('data-subsection-id');
                    var header = jQuery('[data-subsection-header=' + subsectionId + ']');
                    elementToMove.detach();
                    header.append(elementToMove);
                    elementToMove.show();
             });
            }
    };   
})();
