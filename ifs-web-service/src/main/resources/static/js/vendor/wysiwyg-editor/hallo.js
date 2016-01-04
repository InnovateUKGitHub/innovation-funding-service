/* Hallo 1.1.1 - rich text editor for jQuery UI
* by Henri Bergius and contributors. Available under the MIT license.
* See http://hallojs.org for more information
*/(function() {
  (function(jQuery) {
    return jQuery.widget('IKS.hallo', {
      toolbar: null,
      bound: false,
      originalContent: '',
      previousContent: '',
      uuid: '',
      selection: null,
      _keepActivated: false,
      originalHref: null,
      options: {
        editable: true,
        plugins: {},
        toolbar: 'halloToolbarContextual',
        parentElement: 'body',
        buttonCssClass: null,
        toolbarCssClass: null,
        toolbarPositionAbove: false,
        toolbarOptions: {},
        placeholder: '',
        forceStructured: true,
        checkTouch: true,
        touchScreen: null
      },
      _create: function() {
        var options, plugin, _ref,
          _this = this;
        this.id = this._generateUUID();
        if (this.options.checkTouch && this.options.touchScreen === null) {
          this.checkTouch();
        }
        _ref = this.options.plugins;
        for (plugin in _ref) {
          options = _ref[plugin];
          if (!jQuery.isPlainObject(options)) {
            options = {};
          }
          jQuery.extend(options, {
            editable: this,
            uuid: this.id,
            buttonCssClass: this.options.buttonCssClass
          });
          jQuery(this.element)[plugin](options);
        }
        this.element.one('halloactivated', function() {
          return _this._prepareToolbar();
        });
        return this.originalContent = this.getContents();
      },
      _init: function() {
        if (this.options.editable) {
          return this.enable();
        } else {
          return this.disable();
        }
      },
      destroy: function() {
        var options, plugin, _ref;
        this.disable();
        if (this.toolbar) {
          this.toolbar.remove();
          this.element[this.options.toolbar]('destroy');
        }
        _ref = this.options.plugins;
        for (plugin in _ref) {
          options = _ref[plugin];
          jQuery(this.element)[plugin]('destroy');
        }
        return jQuery.Widget.prototype.destroy.call(this);
      },
      disable: function() {
        var _this = this;
        this.element.attr("contentEditable", false);
        this.element.off("focus", this._activated);
        this.element.off("blur", this._deactivated);
        this.element.off("keyup paste change", this._checkModified);
        this.element.off("keyup", this._keys);
        this.element.off("keyup mouseup", this._checkSelection);
        this.bound = false;
        jQuery(this.element).removeClass('isModified');
        jQuery(this.element).removeClass('inEditMode');
        this.element.parents('a').addBack().each(function(idx, elem) {
          var element;
          element = jQuery(elem);
          if (!element.is('a')) {
            return;
          }
          if (!_this.originalHref) {
            return;
          }
          return element.attr('href', _this.originalHref);
        });
        return this._trigger("disabled", null);
      },
      enable: function() {
        var _this = this;
        this.element.parents('a[href]').addBack().each(function(idx, elem) {
          var element;
          element = jQuery(elem);
          if (!element.is('a[href]')) {
            return;
          }
          _this.originalHref = element.attr('href');
          return element.removeAttr('href');
        });
        this.element.attr("contentEditable", true);
        if (!jQuery.parseHTML(this.element.html())) {
          this.element.html(this.options.placeholder);
          jQuery(this.element).addClass('inPlaceholderMode');
          this.element.css({
            'min-width': this.element.innerWidth(),
            'min-height': this.element.innerHeight()
          });
        }
        if (!this.bound) {
          this.element.on("focus", this, this._activated);
          this.element.on("blur", this, this._deactivated);
          this.element.on("keyup paste change", this, this._checkModified);
          this.element.on("keyup", this, this._keys);
          this.element.on("keyup mouseup", this, this._checkSelection);
          this.bound = true;
        }
        if (this.options.forceStructured) {
          this._forceStructured();
        }
        return this._trigger("enabled", null);
      },
      activate: function() {
        return this.element.focus();
      },
      containsSelection: function() {
        var range;
        range = this.getSelection();
        return this.element.has(range.startContainer).length > 0;
      },
      getSelection: function() {
        var range, sel;
        sel = rangy.getSelection();
        range = null;
        if (sel.rangeCount > 0) {
          range = sel.getRangeAt(0);
        } else {
          range = rangy.createRange();
        }
        return range;
      },
      restoreSelection: function(range) {
        var sel;
        sel = rangy.getSelection();
        return sel.setSingleRange(range);
      },
      replaceSelection: function(cb) {
        var newTextNode, r, range, sel, t;
        if (navigator.appName === 'Microsoft Internet Explorer') {
          t = document.selection.createRange().text;
          r = document.selection.createRange();
          return r.pasteHTML(cb(t));
        } else {
          sel = window.getSelection();
          range = sel.getRangeAt(0);
          newTextNode = document.createTextNode(cb(range.extractContents()));
          range.insertNode(newTextNode);
          range.setStartAfter(newTextNode);
          sel.removeAllRanges();
          return sel.addRange(range);
        }
      },
      removeAllSelections: function() {
        if (navigator.appName === 'Microsoft Internet Explorer') {
          return range.empty();
        } else {
          return window.getSelection().removeAllRanges();
        }
      },
      getPluginInstance: function(plugin) {
        var instance;
        instance = jQuery(this.element).data("IKS-" + plugin);
        if (instance) {
          return instance;
        }
        instance = jQuery(this.element).data(plugin);
        if (instance) {
          return instance;
        }
        throw new Error("Plugin " + plugin + " not found");
      },
      getContents: function() {
        var cleanup, instance, plugin;
        for (plugin in this.options.plugins) {
          instance = this.getPluginInstance(plugin);
          if (!instance) {
            continue;
          }
          cleanup = instance.cleanupContentClone;
          if (!jQuery.isFunction(cleanup)) {
            continue;
          }
          jQuery(this.element)[plugin]('cleanupContentClone', this.element);
        }
        return this.element.html();
      },
      setContents: function(contents) {
        return this.element.html(contents);
      },
      isModified: function() {
        var changed;
        if (typeof this.previousContent === 'undefined') {
          this.previousContent = false;
          return true;
        }
        changed = this.previousContent !== this.getContents();
        this.previousContent = this.getContents();
        return changed;
      },
      setUnmodified: function() {
        jQuery(this.element).removeClass('isModified');
        return this.previousContent = this.getContents();
      },
      setModified: function() {
        jQuery(this.element).addClass('isModified');
        return this._trigger('modified', null, {
          editable: this,
          content: this.getContents()
        });
      },
      restoreOriginalContent: function() {
        return this.element.html(this.originalContent);
      },
      execute: function(command, value) {
        if (document.execCommand(command, false, value)) {
          return this.element.trigger("change");
        }
      },
      protectFocusFrom: function(el) {
        var _this = this;
        return el.on("mousedown", function(event) {
          event.preventDefault();
          _this._protectToolbarFocus = true;
          return setTimeout(function() {
            return _this._protectToolbarFocus = false;
          }, 300);
        });
      },
      keepActivated: function(_keepActivated) {
        this._keepActivated = _keepActivated;
      },
      _generateUUID: function() {
        var S4;
        S4 = function() {
          return ((1 + Math.random()) * 0x10000 | 0).toString(16).substring(1);
        };
        return "" + (S4()) + (S4()) + "-" + (S4()) + "-" + (S4()) + "-" + (S4()) + "-" + (S4()) + (S4()) + (S4());
      },
      _prepareToolbar: function() {
        var defaults, instance, plugin, populate, toolbarOptions;
        this.toolbar = jQuery('<div class="hallotoolbar"></div>').hide();
        if (this.options.toolbarCssClass) {
          this.toolbar.addClass(this.options.toolbarCssClass);
        }
        defaults = {
          editable: this,
          parentElement: this.options.parentElement,
          toolbar: this.toolbar,
          positionAbove: this.options.toolbarPositionAbove
        };
        toolbarOptions = jQuery.extend({}, defaults, this.options.toolbarOptions);
        this.element[this.options.toolbar](toolbarOptions);
        for (plugin in this.options.plugins) {
          instance = this.getPluginInstance(plugin);
          if (!instance) {
            continue;
          }
          populate = instance.populateToolbar;
          if (!jQuery.isFunction(populate)) {
            continue;
          }
          this.element[plugin]('populateToolbar', this.toolbar);
        }
        this.element[this.options.toolbar]('setPosition');
        return this.protectFocusFrom(this.toolbar);
      },
      changeToolbar: function(element, toolbar, hide) {
        var originalToolbar;
        if (hide == null) {
          hide = false;
        }
        originalToolbar = this.options.toolbar;
        this.options.parentElement = element;
        if (toolbar) {
          this.options.toolbar = toolbar;
        }
        if (!this.toolbar) {
          return;
        }
        this.element[originalToolbar]('destroy');
        this.toolbar.remove();
        this._prepareToolbar();
        if (hide) {
          return this.toolbar.hide();
        }
      },
      _checkModified: function(event) {
        var widget;
        widget = event.data;
        if (widget.isModified()) {
          return widget.setModified();
        }
      },
      _keys: function(event) {
        var old, widget;
        widget = event.data;
        if (event.keyCode === 27) {
          old = widget.getContents();
          widget.restoreOriginalContent(event);
          widget._trigger("restored", null, {
            editable: widget,
            content: widget.getContents(),
            thrown: old
          });
          return widget.turnOff();
        }
      },
      _rangesEqual: function(r1, r2) {
        if (r1.startContainer !== r2.startContainer) {
          return false;
        }
        if (r1.startOffset !== r2.startOffset) {
          return false;
        }
        if (r1.endContainer !== r2.endContainer) {
          return false;
        }
        if (r1.endOffset !== r2.endOffset) {
          return false;
        }
        return true;
      },
      _checkSelection: function(event) {
        var widget;
        if (event.keyCode === 27) {
          return;
        }
        widget = event.data;
        return setTimeout(function() {
          var sel;
          sel = widget.getSelection();
          if (widget._isEmptySelection(sel) || widget._isEmptyRange(sel)) {
            if (widget.selection) {
              widget.selection = null;
              widget._trigger("unselected", null, {
                editable: widget,
                originalEvent: event
              });
            }
            return;
          }
          if (!widget.selection || !widget._rangesEqual(sel, widget.selection)) {
            widget.selection = sel.cloneRange();
            return widget._trigger("selected", null, {
              editable: widget,
              selection: widget.selection,
              ranges: [widget.selection],
              originalEvent: event
            });
          }
        }, 0);
      },
      _isEmptySelection: function(selection) {
        if (selection.type === "Caret") {
          return true;
        }
        return false;
      },
      _isEmptyRange: function(range) {
        if (range.collapsed) {
          return true;
        }
        if (range.isCollapsed) {
          if (typeof range.isCollapsed === 'function') {
            return range.isCollapsed();
          }
          return range.isCollapsed;
        }
        return false;
      },
      turnOn: function() {
        if (this.getContents() === this.options.placeholder) {
          this.setContents('');
        }
        jQuery(this.element).removeClass('inPlaceholderMode');
        jQuery(this.element).addClass('inEditMode');
        return this._trigger("activated", null, this);
      },
      turnOff: function() {
        jQuery(this.element).removeClass('inEditMode');
        this._trigger("deactivated", null, this);
        if (!this.getContents()) {
          jQuery(this.element).addClass('inPlaceholderMode');
          return this.setContents(this.options.placeholder);
        }
      },
      _activated: function(event) {
        return event.data.turnOn();
      },
      _deactivated: function(event) {
        if (event.data._keepActivated) {
          return;
        }
        if (event.data._protectToolbarFocus !== true) {
          return event.data.turnOff();
        } else {
          return setTimeout(function() {
            return jQuery(event.data.element).focus();
          }, 300);
        }
      },
      _forceStructured: function(event) {
        var e;
        try {
          return document.execCommand('styleWithCSS', 0, false);
        } catch (_error) {
          e = _error;
          try {
            return document.execCommand('useCSS', 0, true);
          } catch (_error) {
            e = _error;
            try {
              return document.execCommand('styleWithCSS', false, false);
            } catch (_error) {
              e = _error;
            }
          }
        }
      },
      checkTouch: function() {
        return this.options.touchScreen = !!('createTouch' in document);
      }
    });
  })(jQuery);

}).call(this);

(function() {
  (function(jQuery) {
    var rangyMessage;
    rangyMessage = 'The hallocleanhtml plugin requires the selection save and\
    restore module from Rangy';
    return jQuery.widget('IKS.hallocleanhtml', {
      _create: function() {
        var editor,
          _this = this;
        if (jQuery.htmlClean === void 0) {
          throw new Error('The hallocleanhtml plugin requires jQuery.htmlClean');
          return;
        }
        editor = this.element;
        return editor.bind('paste', this, function(event) {
          var lastContent, lastRange, widget;
          if (rangy.saveSelection === void 0) {
            throw new Error(rangyMessage);
            return;
          }
          widget = event.data;
          widget.options.editable.getSelection().deleteContents();
          lastRange = rangy.saveSelection();
          lastContent = editor.html();
          editor.html('');
          return setTimeout(function() {
            var cleanPasted, error, pasted, range;
            pasted = editor.html();
            cleanPasted = jQuery.htmlClean(pasted, _this.options);
            editor.html(lastContent);
            rangy.restoreSelection(lastRange);
            if (cleanPasted !== '') {
              try {
                return document.execCommand('insertHTML', false, cleanPasted);
              } catch (_error) {
                error = _error;
                range = widget.options.editable.getSelection();
                return range.insertNode(range.createContextualFragment(cleanPasted));
              }
            }
          }, 4);
        });
      }
    });
  })(jQuery);

}).call(this);

(function() {
  (function(jQuery) {
    return jQuery.widget("IKS.halloformat", {
      options: {
        editable: null,
        uuid: '',
        formattings: {
          bold: true,
          italic: true,
          strikeThrough: false,
          underline: false
        },
        buttonCssClass: null
      },
      populateToolbar: function(toolbar) {
        var buttonize, buttonset, enabled, format, widget, _ref,
          _this = this;
        widget = this;
        buttonset = jQuery("<span class=\"" + widget.widgetName + "\"></span>");
        buttonize = function(format) {
          var buttonHolder;
          buttonHolder = jQuery('<span></span>');
          buttonHolder.hallobutton({
            label: format,
            editable: _this.options.editable,
            command: format,
            uuid: _this.options.uuid,
            cssClass: _this.options.buttonCssClass
          });
          return buttonset.append(buttonHolder);
        };
        _ref = this.options.formattings;
        for (format in _ref) {
          enabled = _ref[format];
          if (!enabled) {
            continue;
          }
          buttonize(format);
        }
        buttonset.hallobuttonset();
        return toolbar.append(buttonset);
      }
    });
  })(jQuery);

}).call(this);

(function() {
  (function(jQuery) {
    return jQuery.widget("IKS.hallolists", {
      options: {
        editable: null,
        toolbar: null,
        uuid: '',
        lists: {
          ordered: true,
          unordered: true
        },
        buttonCssClass: null
      },
      populateToolbar: function(toolbar) {
        var buttonize, buttonset,
          _this = this;
        buttonset = jQuery("<span class=\"" + this.widgetName + "\"></span>");
        buttonize = function(type, label) {
          var buttonElement;
          buttonElement = jQuery('<span></span>');
          buttonElement.hallobutton({
            uuid: _this.options.uuid,
            editable: _this.options.editable,
            label: label,
            command: "insert" + type + "List",
            icon: "fa-list-" + (label.toLowerCase()),
            cssClass: _this.options.buttonCssClass
          });
          return buttonset.append(buttonElement);
        };
        if (this.options.lists.ordered) {
          buttonize("Ordered", "OL");
        }
        if (this.options.lists.unordered) {
          buttonize("Unordered", "UL");
        }
        buttonset.hallobuttonset();
        return toolbar.append(buttonset);
      }
    });
  })(jQuery);

}).call(this);

(function() {
  (function(jQuery) {
    return jQuery.widget('IKS.halloToolbarFixed', {
      toolbar: null,
      options: {
        parentElement: 'body',
        editable: null,
        toolbar: null,
        affix: true,
        affixTopOffset: -35
      },
      _create: function() {
        var el, widthToAdd,
          _this = this;
        this.toolbar = this.options.toolbar;
        this.toolbar.show();
        jQuery(this.options.parentElement).append(this.toolbar);
        this._bindEvents();
        jQuery(window).resize(function(event) {
          return _this.setPosition();
        });
        jQuery(window).scroll(function(event) {
          return _this.setPosition();
        });
        if (this.options.parentElement === 'body') {
          el = jQuery(this.element);
          widthToAdd = parseFloat(el.css('padding-left'));
          widthToAdd += parseFloat(el.css('padding-right'));
          widthToAdd += parseFloat(el.css('border-left-width'));
          widthToAdd += parseFloat(el.css('border-right-width'));
          widthToAdd += (parseFloat(el.css('outline-width'))) * 2;
          widthToAdd += (parseFloat(el.css('outline-offset'))) * 2;
          return jQuery(this.toolbar).css("width", el.width() + widthToAdd);
        }
      },
      _getPosition: function(event, selection) {
        var offset, position, width;
        if (!event) {
          return;
        }
        width = parseFloat(this.element.css('outline-width'));
        offset = width + parseFloat(this.element.css('outline-offset'));
        return position = {
          top: this.element.offset().top - this.toolbar.outerHeight() - offset,
          left: this.element.offset().left - offset
        };
      },
      _getCaretPosition: function(range) {
        var newRange, position, tmpSpan;
        tmpSpan = jQuery("<span/>");
        newRange = rangy.createRange();
        newRange.setStart(range.endContainer, range.endOffset);
        newRange.insertNode(tmpSpan.get(0));
        position = {
          top: tmpSpan.offset().top,
          left: tmpSpan.offset().left
        };
        tmpSpan.remove();
        return position;
      },
      setPosition: function() {
        var elementBottom, elementTop, height, offset, scrollTop, topOffset;
        if (this.options.parentElement !== 'body') {
          return;
        }
        this.toolbar.css('position', 'absolute');
        this.toolbar.css('top', this.element.offset().top - this.toolbar.outerHeight());
        if (this.options.affix) {
          scrollTop = jQuery(window).scrollTop();
          offset = this.element.offset();
          height = this.element.height();
          topOffset = this.options.affixTopOffset;
          elementTop = offset.top - (this.toolbar.height() + this.options.affixTopOffset);
          elementBottom = (height - topOffset) + (offset.top - this.toolbar.height());
          if (scrollTop > elementTop && scrollTop < elementBottom) {
            this.toolbar.css('position', 'fixed');
            this.toolbar.css('top', this.options.affixTopOffset);
          }
        } else {

        }
        return this.toolbar.css('left', this.element.offset().left - 2);
      },
      _updatePosition: function(position) {},
      _bindEvents: function() {
        var _this = this;
        this.element.on('halloactivated', function(event, data) {
          _this.setPosition();
          return _this.toolbar.show();
        });
        return this.element.on('hallodeactivated', function(event, data) {
          return _this.toolbar.hide();
        });
      }
    });
  })(jQuery);

}).call(this);

(function() {
  (function(jQuery) {
    jQuery.widget('IKS.hallobutton', {
      button: null,
      isChecked: false,
      options: {
        uuid: '',
        label: null,
        icon: null,
        editable: null,
        command: null,
        commandValue: null,
        queryState: true,
        cssClass: null
      },
      _create: function() {
        var hoverclass, id, opts, _base,
          _this = this;
        if ((_base = this.options).icon == null) {
          _base.icon = "fa-" + (this.options.label.toLowerCase());
        }
        id = "" + this.options.uuid + "-" + this.options.label;
        opts = this.options;
        this.button = this._createButton(id, opts.command, opts.label, opts.icon);
        this.element.append(this.button);
        if (this.options.cssClass) {
          this.button.addClass(this.options.cssClass);
        }
        if (this.options.editable.options.touchScreen) {
          this.button.addClass('btn-large');
        }
        this.button.data('hallo-command', this.options.command);
        if (this.options.commandValue) {
          this.button.data('hallo-command-value', this.options.commandValue);
        }
        hoverclass = 'ui-state-hover';
        this.button.on('mouseenter', function(event) {
          if (_this.isEnabled()) {
            return _this.button.addClass(hoverclass);
          }
        });
        return this.button.on('mouseleave', function(event) {
          return _this.button.removeClass(hoverclass);
        });
      },
      _init: function() {
        var editableElement, events, queryState,
          _this = this;
        if (!this.button) {
          this.button = this._prepareButton();
        }
        this.element.append(this.button);
        if (this.options.queryState === true) {
          queryState = function(event) {
            var compared, e, value;
            if (!_this.options.command) {
              return;
            }
            try {
              if (_this.options.commandValue) {
                value = document.queryCommandValue(_this.options.command);
                compared = value.match(new RegExp(_this.options.commandValue, "i"));
                return _this.checked(compared ? true : false);
              } else {
                return _this.checked(document.queryCommandState(_this.options.command));
              }
            } catch (_error) {
              e = _error;
            }
          };
        } else {
          queryState = this.options.queryState;
        }
        if (this.options.command) {
          this.button.on('click', function(event) {
            if (_this.options.commandValue) {
              _this.options.editable.execute(_this.options.command, _this.options.commandValue);
            } else {
              _this.options.editable.execute(_this.options.command);
            }
            if (typeof queryState === 'function') {
              queryState();
            }
            return false;
          });
        }
        if (!this.options.queryState) {
          return;
        }
        editableElement = this.options.editable.element;
        events = 'keyup paste change mouseup hallomodified';
        editableElement.on(events, queryState);
        editableElement.on('halloenabled', function() {
          return editableElement.on(events, queryState);
        });
        return editableElement.on('hallodisabled', function() {
          return editableElement.off(events, queryState);
        });
      },
      enable: function() {
        return this.button.removeAttr('disabled');
      },
      disable: function() {
        return this.button.attr('disabled', 'true');
      },
      isEnabled: function() {
        return this.button.attr('disabled') !== 'true';
      },
      refresh: function() {
        if (this.isChecked) {
          return this.button.addClass('ui-state-active');
        } else {
          return this.button.removeClass('ui-state-active');
        }
      },
      checked: function(checked) {
        this.isChecked = checked;
        return this.refresh();
      },
      _createButton: function(id, command, label, icon) {
        var classes;
        classes = ['ui-button', 'ui-widget', 'ui-state-default', 'ui-corner-all', 'ui-button-text-only', "" + command + "_button"];
        return jQuery("<button id=\"" + id + "\"        class=\"" + (classes.join(' ')) + "\" title=\"" + label + "\">          <span class=\"ui-button-text\">            <i class=\"fa " + icon + "\"></i>          </span>        </button>");
      }
    });
    return jQuery.widget('IKS.hallobuttonset', {
      buttons: null,
      _create: function() {
        return this.element.addClass('ui-buttonset');
      },
      _init: function() {
        return this.refresh();
      },
      refresh: function() {
        var rtl;
        rtl = this.element.css('direction') === 'rtl';
        this.buttons = this.element.find('.ui-button');
        this.buttons.removeClass('ui-corner-all ui-corner-left ui-corner-right');
        if (rtl) {
          this.buttons.filter(':first').addClass('ui-corner-right');
          return this.buttons.filter(':last').addClass('ui-corner-left');
        } else {
          this.buttons.filter(':first').addClass('ui-corner-left');
          return this.buttons.filter(':last').addClass('ui-corner-right');
        }
      }
    });
  })(jQuery);

}).call(this);
