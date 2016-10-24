//  Progressive collapsibles original code by @Heydonworks altered by Worth Systems
//-----------------------------------------------------------------------------
IFS.core.collapsible = (function(){
  "use strict";
  var s; // private alias to settings
  var index = 0;

  return {
      settings: {
        collapsibleEl : '.collapsible > h2, .collapsible > h3'
      },
      init : function() {
        s = this.settings;
        //if this has to be more dynamicly updated in the future we can add a custom event
        jQuery(s.collapsibleEl).each(function(){
          IFS.core.collapsible.initCollapsibleHTML(this);
        });
        jQuery('body').on('click', '.collapsible > h2 >  [aria-controls], .collapsible > h3 >  [aria-controls]' , function(){
          IFS.core.collapsible.toggleCollapsible(this);
        });
      },
      initCollapsibleHTML  : function(el) {
        var inst = jQuery(el);
        var id = 'collapsible-' + index;   // create unique id for a11y relationship
        var loadstate = IFS.core.collapsible.getLoadstateFromCookie(id);
        // wrap the content and make it focusable
        inst.nextUntil('h2,h3').wrapAll('<div id="'+id+'" aria-hidden="'+!loadstate+'">');
        // Add the button inside the <h2> so both the heading and button semanics are read
        inst.wrapInner('<button aria-expanded="'+loadstate+'" aria-controls="'+ id +'" type="button">');
        index++;
      },
      toggleCollapsible : function(el){
        var inst = jQuery(el);
        var panel = jQuery('#'+inst.attr('aria-controls'));
        var state = inst.attr('aria-expanded') === 'false' ? true : false;
        //toggle the current
        inst.attr('aria-expanded', state);
        panel.attr('aria-hidden', !state);
        IFS.core.collapsible.setLoadStateInCookie(panel.attr('id'), state);
      },
      getLoadstateFromCookie : function(index){
        if(typeof(Cookies.getJSON('collapsibleStates')) !== 'undefined'){
          var json = Cookies.getJSON('collapsibleStates');
          var pathname = window.location.pathname;
          if(typeof(json[pathname]) !== 'undefined'){
            if(typeof(json[pathname][index]) !== 'undefined'){
              return json[pathname][index];
            }
          }
        }
        return false;
      },
      setLoadStateInCookie : function(index, state){
        var json = {};
        if(typeof(Cookies.getJSON('collapsibleStates')) !== 'undefined'){
          json = Cookies.getJSON('collapsibleStates');
        }
        var pathname = window.location.pathname;
        if((typeof(json[pathname]) === 'undefined')) {
          json[pathname] = {};
        }
        if(state === true){
          json[pathname][index] = state;
        }
        else if(typeof(json[pathname][index]) !== 'undefined'){
          //removing of false and empty objects from the json object as we store this in a cookie,
          //only == true will be opened on pageload so those are the only ones we have to store
          delete json[pathname][index];

          //options other than looping over for getting the object count break in ie8
          var count = 0;
          jQuery.each(json[pathname], function(){
            count++;
          });
          if(count===0){
            delete(json[pathname]);
          }
        }
        Cookies.set('collapsibleStates', json, { expires: 0.05 }); //defined in days, 0.05 = little bit more than one hour
      }
    };
})();
