/*
 *
 *
 *
 *
 *
 *
 * */

(function($) {
    var $element;
    var $content;
    var $edit = false;
    var $markitup = false;
    var $temp_content;
    var $options;
    var $now_block = null;
    var $nowElem;
    var $nowType;

    $.fn.clickToForm = function(options) {
        /*
         $options - совокупность параметров по умолчанию и пользовательских параметров.
         */
        $options = $.extend({
            header: "div:first",
            mouseover_backgroundcolor: "#dbe8ff",
            mouseout_backgroundcolor: "#FFF",
            elements: {
                /*select: {
                 ".controlThreadCheckLogFile" : {
                 valuesPlace: "",
                 separatorTwin: ";",
                 separatorVal: "|"
                 },
                 ".2" : {
                 valuesPlace: "",
                 separatorTwin: ";",
                 separatorVal: "|"
                 }
                 },
                 text: {
                 ".3" : {
                 limit: "100",
                 showCounter: "on"

                 },
                 ".4" : {
                 limit: "100",
                 showCounter: "on"
                 }
                 },
                 markitup: {
                 ".5" : {
                 limit: "100",
                 showCounter: "on"
                 },
                 ".6" : {
                 limit: "100",
                 showCounter: "on"
                 }
                 },
                 file_mod: {
                 ".file": {
                 progressbar: "",
                 dialog: "",
                 url: "",
                 cmdStat: "",
                 cmdInfo: ""
                 }
                 }*/
            },
            buttons: {
                "применить": {show: '', onClick: function() {
                    alert('Please bind event to this button');
                }},
                "отменить": {show: '', onClick: function() {
                    alert('Please bind event to this button');
                }}
            }
        }, options);

        $(this).find($options.header).append("<div class=\"button_ok_cancel\"></div>");

        $.each($(this).find('.button_ok_cancel'), function() {
            for (var $button in $options.buttons) {
                $(this).append("<input type=\"button\" value=\"" + $button + "\"/>");
            }
            $.each($(this).find(':button'), function() {
                switch ($options.buttons[$(this).attr('value')].show) {
                    case 'always':
                        $(this).css({"display": "inline-block"});
                        break;
                    case 'afterEdit':
                        $(this).css({"display": "none"});
                        break;

                }
            });
        });

        $(this).find(".button_ok_cancel").find(':button').bind('click', function() {
            $.fn.edit_ok_();
            $now_block = $(this).parent().parent();
            $($options.buttons[$(this).attr('value')].onClick);
        });

        return this.each(function() {
            for (var e in $options.elements) {
                if (e === 'select') {
                    for (var sel in $options.elements.select) {
                        $(this).find(sel).css({"cursor": "pointer"}).addClass("content_block").click(
                                function() {
                                    $.fn.click_(this, 'select');
                                }).mouseover(
                                function() {
                                    $(this).css({"background": $options.mouseover_backgroundcolor});
                                }).mouseout(function() {
                            $(this).css({"background": $options.mouseout_backgroundcolor});
                        });
                    }
                } else if (e === 'file_mod') {
                    for (var file in $options.elements.file_mod) {
                        $(this).find(file).css({"cursor": "pointer"}).addClass("content_block").click(
                                function() {
                                    $.fn.click_(this, 'file_mod');
                                }).mouseover(
                                function() {
                                    $(this).css({"background": $options.mouseover_backgroundcolor});
                                }).mouseout(function() {
                            $(this).css({"background": $options.mouseout_backgroundcolor});
                        });
                    }
                } else if (e === 'text') {
                    for (var text in $options.elements.text) {
                        $(this).find(text).css({"cursor": "pointer"}).addClass("content_block").click(
                                function() {
                                    $.fn.click_(this, 'text');
                                }).mouseover(
                                function() {
                                    $(this).css({"background": $options.mouseover_backgroundcolor});
                                }).mouseout(function() {
                            $(this).css({"background": $options.mouseout_backgroundcolor});
                        });
                    }
                } else if (e === 'markitup') {
                    for (var markitup in $options.elements.markitup) {
                        $(this).find(markitup).css({"cursor": "pointer"}).addClass("content_block").click(
                                function() {
                                    $.fn.click_(this, 'markitup');
                                }).mouseover(
                                function() {
                                    $(this).css({"background": $options.mouseover_backgroundcolor});
                                }).mouseout(function() {
                            $(this).css({"background": $options.mouseout_backgroundcolor});
                        });
                    }
                } else if (e === 'datetimepicker') {
                    for (var datetimepicker in $options.elements.datetimepicker) {
                        $(this).find(datetimepicker).css({"cursor": "pointer"}).addClass("content_block").click(
                                function() {
                                    $.fn.click_(this, 'datetimepicker');
                                }).mouseover(
                                function() {
                                    $(this).css({"background": $options.mouseover_backgroundcolor});
                                }).mouseout(function() {
                            $(this).css({"background": $options.mouseout_backgroundcolor});
                        });
                    }
                }
            }
        });
    };

    $.fn.getActiveBlock = function() {
        return $now_block;
    };

    $.fn.controll_button = function (display) {
        if (typeof(display) === 'string') {
            var $dis;

            if (display === 'on') {
                $dis = 'inline-block';
            } else {
                $dis = 'none';
            }

            $.each($($.fn.getActiveBlock()).find('.button_ok_cancel:first').find(':button'), function() {
                if ($options.buttons[$(this).attr('value')].show === 'afterEdit') {
                    $(this).css('display', $dis);
                }
            });
        }
    };

    $.fn.edit_ok_ = function (lastElem, lastType) {
        var $cElem;
        var $cType;
        if (typeof(lastElem) == 'undefined') {
            $cElem = $nowElem;
            $cType = $nowType;
        } else {
            $cElem = lastElem;
            $cType = lastType;
        }

        if ($edit) {
            switch ($cType) {
                case 'select':
                    $content = "";
                    $values = "";
                    $.each($($cElem).find('select:first option:selected'), function() {
                        $content += $(this).text() + " ; ";
                        $values += $(this).val() + ";";
                    });

                    $($cElem).next().next().val($values);
                    break;
                case 'file_mod':
                    var $id_file = $($cElem).find('form').next('div').find(':hidden').val();
                    var $linkpath = $($cElem).find('span').find('a').attr('href');
                    var $linkname = $($cElem).find('textarea').val();
                    if (($.trim($linkpath) == "undefined") || (typeof($linkpath) == "undefined")) {
                        $linkpath = "";
                    } else {
                        $linkpath = "href=\"" + $linkpath + "\"";
                    }
                    $content = "<input type='hidden' value='" + $id_file + "'><a " + $linkpath + "linkname>" + $linkname + "</a>";
                    break;
                case 'markitup':
                    $($cElem).find('textarea:first').markItUpRemove();
                    $temp_content = $($cElem).find('textarea:first').val();
                    if ($.trim($temp_content) == "") $temp_content = '&nbsp;';
                    $content = $temp_content;
                    break;
                case 'datetimepicker':
                    $($cElem).find(':text:first').datepicker("destroy");
                    $temp_content = $($cElem).find(':text:first').val();
                    if ($.trim($temp_content) == "") $temp_content = '&nbsp;';
                    $content = $temp_content;
                    break;
                default:
                    $temp_content = $($cElem).contents().val();
                    if ($.trim($temp_content) == "") $temp_content = '&nbsp;';
                    $content = $temp_content;
                    break;
            }
            $temp_content = null;

            $($cElem).html($content);
            $($cElem).next().remove();
            $.fn.controll_button("on");
            $edit = false;
        }
    };

    $.fn.numberOfSymbol = function(elem, limit, type, classCounter) {
        var $find_elem = ':text:first';
        switch (type) {
            case 'text':
                $find_elem = ':text:first';
                $(elem).find($find_elem).css({"width": "93%", "margin": "0 5px 0 0"/*,"display": "block",*/ /*"border": "1px solid #000"*/});
                $(elem).find("[class*='" + classCounter.replace(".", "") + "']").css({"width": "2%", "margin": "0 0 0 0", /*"float": "right",*/ /*"border": "1px solid #000" ,*/"padding": "0 0 0 0", 'display':'inline'});
                break;
            case 'markitup':
                $find_elem = 'textarea';
                $(elem).find("[class*='" + classCounter.replace(".", "") + "']").css({"float": "right", "margin-right": "5px"});
                break;
        }

        function counter(el) {
            $(elem).find("[class*='" + classCounter.replace(".", "") + "']").html(limit - $(el).val().length);
            if ((limit - $(el).val().length) >= 0) {
                $(elem).find("[class*='" + classCounter.replace(".", "") + "']").css({"color": "green"});
            } else {
                $(elem).find("[class*='" + classCounter.replace(".", "") + "']").css({"color": "red"});
            }
        }

        $(elem).find($find_elem).bind('keyup keydoun keypress', function() {
            counter(this);
        });
        $(elem).find($find_elem).trigger('keyup');
    };

    $.fn.edit_cancel_ = function() {
        if ($edit) {
            if ($markitup) $($element).find('textarea:first').markItUpRemove();
            if ($.trim($content) == '') $content = '&nbsp;';
            $($element).html($content);
            $($element).next().remove();
            $edit = false;
        }
    };

    $.fn.edit_ = function (elem, type_elem) {
        var $new_content_element;            //то, чем будем заменять
        $element = elem;                     //текущий элемент
        $content = $(elem).html();           //содержимое текущего элемента
        $now_block = $(elem).parent();       //к какой "группе" принадлежил элемент

        if ($.trim($content) == '&nbsp;') $content = '';
        if ($.trim($content) == '') $content = $.trim($content);

        //в зависимости от типа генерируем заменяющий контент
        switch (type_elem) {
            case 'text':
                $new_content_element = "<input type='text' value='" + $content + "'/>";
                $new_content_element += "<div class='counter'></div>";
                $markitup = false;
                break;
            case 'datetimepicker':
                $new_content_element = "<input type='text' value='" + $content + "'/>";
                $markitup = false;
                break;
            case 'select':
                var $selector;

                for (var sel in $options.elements.select) {
                    if ($(elem).attr('class').indexOf(sel.replace(".", "")) != -1) {
                        $selector = sel;
                    } else if ($(elem).attr('id').indexOf(sel.replace(".", "")) != -1) {
                        $selector = sel;
                    }
                }

                $new_content_element = "<select class='select_type_news' " + ($options.elements.select[$selector].multiple === "multiple" ? "multiple" : "") + ">";
                var $types = $($options.elements.select[$selector].valuesPlace).val().split($options.elements.select[$selector].separatorTwin);
                for (var i = 0; i < $types.length - 1; i++) {
                    var $types_ = $types[i].split($options.elements.select[$selector].separatorVal);
                    $new_content_element += "<option value=" + $types_[1] + ">" + $types_[0] + "</option>";
                }
                $new_content_element += "</select>";
                $markitup = false;
                break;
            case 'markitup':
//            $new_content_element = "<div class='counter'></div>";
                $new_content_element = "<textarea>" + $content + "</textarea>";
                $markitup = true;
                break;
            case 'file':
                $new_content_element = "<input type=\"file\" name=\"file\" value=\"" + $content + "\"/>";
                $markitup = false;
                break;
            case 'file_mod':
                var $selector_file;

                for (var file in $options.elements.file_mod) {
                    if ($(elem).attr('class').indexOf(file.replace(".", "")) != -1) {
                        $selector_file = file;
                    } else if ($(elem).attr('id').indexOf(file.replace(".", "")) != -1) {
                        $selector_file = file;
                    }
                }
                var $id_file = $(elem).find(':hidden:first').val();
                var $link_path = $(elem).find('a').attr('href');
                var $url = $options.elements.file_mod[$selector_file].url;

                if (($.trim($link_path) == 'undefined') || (typeof($link_path) == "undefined")) {
                    $link_path = "";
                } else {
                    $link_path = "href=\"" + $link_path + "\"";
                }

                var $link_name = $(elem).find('a').html();

                if (($.trim($link_name) == 'undefined') || (typeof($link_name) == "undefined")) {
                    $link_name = "";
                }

                $new_content_element =
                        "<div class=\"content\">" +
                                "<input type=\"button\" value=\"Прикрепить файл\"/>" +

                                "<form method=\"POST\" enctype=\"multipart/form-data\" action=\"" + $url + "\">" +
                                "<div class=\"content\">" +
                                "Файл:" +
                                "<input type=\"file\" name=\"file\"/>" +
                                "</div>" +
                                "<div class=\"content\">" +
                                "Название ссылки:" +
                                "<textarea rows=\"2\" name=\"descr\" style=\"width:100%\">" + $link_name + "</textarea>" +
                                "</div>" +
                                "<input type=\"submit\" value=\"Прикрепить!\"/>" +
                                "</form>" +

                                "<div>" +
                                "<span><a " + $link_path + ">" + $link_name + "</a></span>&nbsp;&nbsp;" +
                                "<a>Удалить вложение</a>" +
                                "<input type='hidden' value=\"" + $id_file + "\"/>" +
                                "</div>" +
                                "</div>";

                $markitup = false;
                break;
        }
        //---------------------------------------------------------------------------------------------------------//


        $(elem).html("").append($new_content_element); //заменяем контент элемента на сгенерированный

        function setValue(id, linkpath, linkname) {
            $(elem).find("form").next('div').find(':hidden').val(id);
            $(elem).find("textarea").val(linkname);
            $(elem).find("span").html("<a href=\"  " + linkpath + "\">" + linkname + "</a>");
            $(elem).find(":button:first").hide().next('form').hide().next('div').show();

        }

        switch (type_elem) {
            case 'text':
                var $selector_text;

                for (var text in $options.elements.text) {
                    if ($(elem).attr('class').indexOf(text.replace(".", "")) != -1) {
                        $selector_text = text;
                    } else if ($(elem).attr('id').indexOf(text.replace(".", "")) != -1) {
                        $selector_text = text;
                    }
                }

                if ($options.elements.text[$selector_text].showCounter == 'on') {
                    if (String(parseInt($options.elements.text[$selector_text].limit)) == $options.elements.text[$selector_text].limit) {
                        $.fn.numberOfSymbol(elem, $options.elements.text[$selector_text].limit, type_elem, '.counter');
                    }
                }
                break;
            case 'datetimepicker':
                var $selector_datetimepicker;

                for (var datetimepicker in $options.elements.datetimepicker) {
                    if ($(elem).attr('class').indexOf(datetimepicker.replace(".", "")) != -1) {
                        $selector_datetimepicker = datetimepicker;
                    } else if ($(elem).attr('id').indexOf(datetimepicker.replace(".", "")) != -1) {
                        $selector_datetimepicker = datetimepicker;
                    }
                }

                var curElem = $(elem).find(":text:first");

                $(curElem).datepicker($options.elements.datetimepicker[$selector_datetimepicker]);
                $(curElem).datepicker("show");

                if ($(curElem).val() === "") {
                    var time = new Date();
                    $.timepicker._setTime('hour', time.getHours());
                    $.timepicker._setTime('minute', time.getMinutes());
                }
                break;
            case 'markitup':
                var $selector_markitup;

                for (var markitup in $options.elements.markitup) {
                    if ($(elem).attr('class').indexOf(markitup.replace(".", "")) != -1) {
                        $selector_markitup = markitup;
                    } else if ($(elem).attr('id').indexOf(markitup.replace(".", "")) != -1) {
                        $selector_markitup = markitup;
                    }
                }


                $(elem).find('textarea:first').markItUp(mySettings);

                if ($options.elements.markitup[$selector_markitup].showCounter == 'on') {
                    if (String(parseInt($options.elements.markitup[$selector_markitup].limit)) == $options.elements.markitup[$selector_markitup].limit) {
                        $.fn.numberOfSymbol(elem, $options.elements.markitup[$selector_markitup].limit, type_elem, '.counter');
                    }
                }
                break;
            case 'select':
                $.each($(elem).next(":hidden").val().split(";"), function() {
                    $(elem).find("select:first option[value='" + this.trim() + "']").attr('selected', 'selected');
                });
                $(elem).find("select:first").css({"width": "100%", "display": "block"});
                break;
            case 'file_mod':
                if ($.trim($id_file) == "") {
                    $(elem).find('form').hide().next('div').hide();
                } else {
                    setValue($id_file, $link_path, $link_name);
                }

                $(elem).find(":button:first").bind('click', function() {
                    $(this).hide();
                    $(elem).find('form').show();
                });
                $(elem).find('form').next('div').find("a").css({"cursor": "pointer", "text-decoration": "underline"}).bind('click', function() {
                    $(elem).find(":button:first").show().next('form').hide().next('div').hide();
                    $($options.elements.file_mod[$selector_file].progressbar).progressbar("option", "value", 0);
                    $(elem).find('form').next('div').find(":hidden:first").val('');
                    $(elem).find('form').find(':file').val("");
                    $(elem).find('span').find('a').removeAttr("href");
                    $(elem).find('span').find('a').html("");
                    $(elem).find('textarea').val("");
                });

                var $dialog;
                if ($options.elements.file_mod[$selector_file].dialog != 'undefined') {
                    $dialog = $options.elements.file_mod[$selector_file].dialog;
                } else {
                    $dialog = '';
                }

                var $timeinterval;
                $(elem).find('form').ajaxForm({
                    beforeSubmit : function(a, f, o) {
                        var ret = false;
                        if (a[0].value === "") {
                            alert("Файл не выбран. Вы можете загрузить файлы с расширением *.txt, *.doc, *.pdf, *.zip");
                            ret = false;
                        } else if ((a[0].value.indexOf("pdf") != -1) || (a[0].value.indexOf("doc") != -1) || (a[0].value.indexOf("txt") != -1) || (a[0].value.indexOf("zip") != -1)) {
                            ret = true;
                            if ($dialog != '') $($dialog).dialog('open');
                            $timeinterval = window.setInterval(clickToFormGetStat, 100);
                        } else {
                            alert("Неверное расширение файла. Вы можете загрузить файлы с расширением *.txt, *.doc, *.pdf, *.zip");
                            ret = false;
                        }
                        return ret;
                    },
                    success: function(data) {
                        window.clearInterval($timeinterval);
                        clickToFormGetFinfo();
                        if ($dialog != '') $($dialog).dialog('close');
                    }
                });

            function clickToFormGetStat() {
                $.ajax({
                    url : $options.elements.file_mod[$selector_file].url,
                    type : "POST",
                    data : "ajaxcmd=" + $options.elements.file_mod[$selector_file].cmdStat,
                    success : function(data) {
                        if (data == "done") {
                            $($options.elements.file_mod[$selector_file].progressbar).progressbar("option", "value", 100);
                        } else {
                            $($options.elements.file_mod[$selector_file].progressbar).progressbar("option", "value", data);
                        }
                    }
                });
            }

            function clickToFormGetFinfo() {
                $.ajax({
                    url : $options.elements.file_mod[$selector_file].url,
                    type : "POST",
                    data : "ajaxcmd=" + $options.elements.file_mod[$selector_file].cmdInfo,
                    success : function(data) {
                        var $ar = data.split("|");
                        var $id = $ar[0];
                        var $linkname = $ar[1];
                        var $linkpath = $ar[2];

                        setValue($id, $linkpath, $linkname);
                    }
                });
            }
                break;
        }

        $(elem)
                .after("<div class='local_button_ok_cancel'></div>")
                .next()
                .css({'display':'block', 'margin-left': 'auto', "text-align": "right"/*, "border": "1px solid #000"*/})
                .append("<input class='ok_edit' type='button'  value='применить'/><input class='cancel_edit' type='button' value='отмена'/>");

        $(elem)
                .next()
                .find(':button')
                .bind('click', function () {
            if ($(this).attr('class') === 'ok_edit') {
                $.fn.edit_ok_();
            } else if ($(this).attr('class') === 'cancel_edit') {
                $.fn.edit_cancel_();
            }
        });

//        if ($markitup)
        $edit = true;
    };

    $.fn.click_ = function(elem, type_elem) {
        var $lastElem;
        var $lastType;
        if (typeof($nowElem) === 'undefined') {
            $lastElem = elem;
            $lastType = type_elem;
        } else {
            $lastElem = $nowElem;
            $lastType = $nowType;
        }

        $nowElem = elem;
        $nowType = type_elem;

        if (!$edit) {
            $.fn.edit_(elem, type_elem);
        } else if (elem != $element) {
            $.fn.edit_ok_($lastElem, $lastType);
            $.fn.edit_(elem, type_elem);
        }
    };

})(jQuery);
;


