/*  Progressive collapsibles written by @Heydonworks altered by Worth Systems
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

/* 
Lea verou's SVG pie
*/

 $('.pie').each(function(index,pie) {
  var p = parseFloat(pie.textContent);
  var NS = "http://www.w3.org/2000/svg";
  var svg = document.createElementNS(NS, "svg");
  var circle = document.createElementNS(NS, "circle");
  var title = document.createElementNS(NS, "title");
  
  circle.setAttribute("r", 16);
  circle.setAttribute("cx", 16);
  circle.setAttribute("cy", 16);
  circle.setAttribute("stroke-dasharray", p + " 100");
  
  svg.setAttribute("viewBox", "0 0 32 32");
  title.textContent = pie.textContent;
  pie.textContent = '';
  svg.appendChild(title);
  svg.appendChild(circle);
  pie.appendChild(svg);
});
