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
            worthIFSFinance.repeatingTotal();
            worthIFSFinance.initFinanceCalculations();

        }
    },
    repeatingTotal : function(){
        if('[data-calculation-repeating-total]'){
            $('[data-calculation-repeating-total]').each(function(index,value){
                var inst = $(this);
                var fields = inst.attr("data-calculation-repeating-total");
                var attributes = [];
                $(fields).each(function(){
                    attributes.push('#'+$(this).attr('id'));
                });

                inst.attr('data-calculation-fields',attributes.join());
            });
        }
    },
    initFinanceCalculations : function(){
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
                values.push(parseFloat($(field).val() || 0));
            });

            var calculatedValue = worthIFSFinance.MathOperation[operation[0]](values[0],values[1]);
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
            element.val(Math.round(calculatedValue));
            element.trigger('change');
    }
} 

jQuery(document).ready(function(){
  worthIFSFinance.domReady();
});



