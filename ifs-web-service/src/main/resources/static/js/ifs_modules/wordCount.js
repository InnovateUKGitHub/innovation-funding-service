/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false */

var ifs_wordCount = (function(){
    "use strict";
    var s; 
    return {
        settings : {
            element : jQuery(".word-count textarea")
        },
        init : function(){
            s = this.settings; 
            
            var options = {
                callback: function () { ifs_wordCount.updateWordCount(this);  },
                wait: 500,
                highlight: false,
                captureLength: 1
            };
            s.element.typeWatch( options );
            s.element.each(function(index, el){
                ifs_wordCount.updateWordCount(el);
            });
        },
        updateWordCount : function(textarea){
              var field = jQuery(textarea);
              var value = field.val();
              //regex = replace newlines with space \r\n, \n, \r 
              var words = jQuery.trim(value.replace(/(\r\n|\n|\r)/gm," ")).split(' ');
              var count = 0;
              //for becuase of ie7 performance. 
              for (var i = 0; i < words.length; i++) {
                if(words[i].length > 0){
                  count++;
                }
              }

              var delta = field.attr('data-max_words') - count;
              var countDownEl = field.parents(".word-count").find(".count-down");

              countDownEl.html(delta);
              if(delta < 0){
                  countDownEl.removeClass("positive").addClass("negative");
              }else{
                  countDownEl.removeClass("negative").addClass("positive");
              }
        }  
    };
})();
