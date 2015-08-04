/* 3. Progressive collapsibles written by Heydon 
-----------------------------------------------------------------------------------------
*/
$(document).ready(function(){
  $('.collapsible h2').each(function() {
    var inst = $(this);
    var id = 'collapsible-' + inst.index();   // create unique id for a11y relationship
     var loadstate = inst.hasClass('open');

    // wrap the content and make it focusable
    inst.nextUntil('h2').wrapAll('<div id="'+ id +'" aria-hidden="'+!loadstate+'">');
    var panel = inst.next();

    // Add the button inside the <h2> so both the heading and button semanics are read  
    inst.wrapInner('<button aria-expanded="'+loadstate+'" aria-controls="'+ id +'">');
    var button = inst.children('button');

    // Toggle the state properties  
    button.on('click', function() {
      var state = $(this).attr('aria-expanded') === 'false' ? true : false;

      //close all others
      $('.collapsible [aria-expanded]').attr('aria-expanded','false');
      $('.collapsible [aria-hidden]').attr('aria-hidden','true');

      //toggle the current
      $(this).attr('aria-expanded', state);
      panel.attr('aria-hidden', !state);
    });
  });

});
