//Innovation Funding Services javascript by Worth
var worthIFSFinance = {
    MathOperation : {
        '+': function (x, y) { return x + y },
        '-': function (x, y) { return x - y },
        '*': function (x, y) { return x * y },
        '/': function (x, y) { return x / y }
    },
    domReady : function(){
        if(worthIFS.isApplicationForm()){
            worthIFSFinance.preProcessRepeatedFieldsForTotalElements();
            worthIFSFinance.bindCalculationActionToFields();
        }
    },
    preProcessRepeatedFieldsForTotalElements : function(){
        if(jQuery('[data-calculation-repeating-total]').length){
            $('[data-calculation-repeating-total]').each(function(index,value){
                worthIFSFinance.preProcessRepeatedFieldsForTotalElement($(this));
            });
        }
    },
    preProcessRepeatedFieldsForTotalElement : function(totalElement) {
        var fieldSelector = worthIFSFinance.getFieldSelector(totalElement);
        var repeatedFieldElements = worthIFSFinance.getFieldsBySelector(fieldSelector);
        worthIFSFinance.addRepeatedFieldIdsToTotalElement(totalElement, repeatedFieldElements);
    },

    getFieldsBySelector : function(fieldSelector) {
        var fields = [];

        $(fieldSelector).each(function(){
            fields.push('#'+$(this).attr('id'));
        });

        return fields;
    },
    getFieldSelector : function(totalElement) {
        return totalElement.attr("data-calculation-repeating-total");
    },
    addRepeatedFieldIdsToTotalElement : function(totalElement, repeatedFieldElements) {
        totalElement.attr('data-calculation-fields',repeatedFieldElements.join());
    },

    bindCalculationActionToFields : function(){
        $('[data-calculation-fields]').each(function(index,value){
                var inst = $(this);
                var fields = inst.attr("data-calculation-fields").split(',');

                _.each(fields, function(el){
                    var field = $(el);
                    console.log('field',field);
                    field.on('change',function(){
                        worthIFSFinance.doMath(inst,fields);
                    });
                });
        });
    },
    doMath : function(element,calcFields){
            var operation = element.attr('data-calculation-operations').split(',');
            var values = [];
            _.each(calcFields, function(field){
                values.push(parseFloat($(field).attr("data-calculation-rawvalue") || $(field).val() || 0));
            });

            if(values.length === 1) {
                var calculatedValue=values[0];
            }
            else {
                var calculatedValue = worthIFSFinance.MathOperation[operation[0]](values[0],values[1]);
            }
            //one operation and more values, all get the same operation

            if((operation.length == 1) && (values.length > 2)) {
                for (i = 2; i < values.length; i++) { 
                    console.log('round:',i,typeof(operation[0]),operation[0],typeof(calculatedValue),calculatedValue,typeof(values[i]),values[i],values)
                    calculatedValue = worthIFSFinance.MathOperation[operation[0]](calculatedValue,values[i]);
                };
            }
           //multiple operations and multiple values
            else if((operation.length > 1) && (values.length > 2)) {
                for (i = 1; i < operation.length; i++) { 
                    // console.log('round:',i,operation[i],calculatedValue,values[i+1])
                    calculatedValue = worthIFSFinance.MathOperation[operation[i]](calculatedValue,values[i+1]);
                }
            }
            element.attr("data-calculation-rawvalue",calculatedValue);
            element.val(worthIFSFinance.formatCurrency(Math.round(calculatedValue)));
            element.trigger('change');
    },

    formatCurrency: function(total) {
        total = Math.abs(total);
        parsedFloat = parseFloat(total, 10);
        tofixed = parsedFloat.toFixed();
        replaced = tofixed.replace(/(\d)(?=(\d{3})+\b)/g, "$1,");
        tostring= replaced.toString();

        return tostring;
    }
} 

jQuery(document).ready(function(){
  worthIFSFinance.domReady();
});



