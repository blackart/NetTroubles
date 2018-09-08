(function($) {
    $.widget("ui.combobox", {
        options: {
            readonly: false
        },
        _create: function() {
            var self = this;
            var select = this.element.hide();
            var input = $("<input>")
                    .insertAfter(select)
                    .attr("readonly", self.options.readonly)
                    .autocomplete({
                                      source: function(request, response) {
                                          var matcher = new RegExp(request.term, "i");
                                          response(select.children("option").map(function() {
                                              var text = $(this).text();
                                              if (this.value && (!request.term || matcher.test(text)))
                                                  return {
                                                      id: this.value,
                                                      label: text.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + $.ui.autocomplete.escapeRegex(request.term) + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>"),
                                                      value: text
                                                  };
                                          }));
                                      },
                                      delay: 0,
                                      /*select: function( event, ui ) {

                                       },*/
                                      change: function(event, ui) {
                                          select.val(ui.item.id);
                                          self._trigger("selected", event, {
                                              item: select.find("[value='" + ui.item.id + "']")
                                          });
                                      },
                                      minLength: 0
                                  })
                    .addClass("ui-widget ui-widget-content ui-corner-left");
            $("<button>&nbsp;</button>")
                    .attr("tabIndex", -1)
                    .attr("title", "Show All Items")
                    .insertAfter(input)
                    .button({
                                icons: {
                                    primary: "ui-icon-triangle-1-s"
                                },
                                text: false
                            }).removeClass("ui-corner-all")
                    .addClass("ui-corner-right ui-button-icon")
                    .click(function() {
                // close if already visible
                if (input.autocomplete("widget").is(":visible")) {
                    input.autocomplete("close");
                    return;
                }
                // pass empty string as value to search for, displaying all results
                input.autocomplete("search", "");
                input.focus();
            });

            this.input = input;
            this.select = select;
        },
        clear: function() {
            this.input.val("");
            this.select.empty();
        },
        value: function() {
            return this.input.val();
        }
    });

})(jQuery);