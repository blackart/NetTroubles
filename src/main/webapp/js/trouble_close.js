$(document).ready(function() {
    $("#date_trouble_close_list").datepicker({
        changeMonth: true,
        changeYear: true,
        closeText: 'close',
        showButtonPanel: true,
        dayNames: ['Воскресение', 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница', 'Суббота'],
        dayNamesShort: ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
        dayNamesMin: ['Вс','Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
        firstDay: 1,
        hideIfNoPrevNext: true,
        monthNames: ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'],
        monthNamesShort: ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'],
        showOtherMonths: true,
        altFormat: 'dd/mm/yy',
        dateFormat: 'dd/mm/yy',
        appendText: '',
        onChangeMonthYear: function(year, month, inst) {
            $(this).datepicker("setDate", "01/" + month + "/" + year);
        },
        defaultDate: new Date
    });

    function generateShortDevList() {
        $.each($(".short_dev_list"), function() {
            $(this).append("<div class='dev_view_all'>show</div>");
            $(this).find(".dev_view_all").bind("mouseover", function() {
                $(this).css("color", "#000");
                $(this).css("background", "#dbe8ff");
                $(this).css("cursor", "pointer");
            });
            $(this).find(".dev_view_all").bind("mouseout", function() {
                $(this).css("color", "#999999");
                $(this).css("background", "#fff");
            });
            $(this).find(".dev_view_all").bind("click", function() {
                var $devices = $(this).parent().next("div");

                if ($($devices).css("display") == "none") {
                    $(this).text("hide");
                    $($devices).css("display", "block");
                } else {
                    $(this).text("show");
                    $($devices).css("display", "none");
                }
            });
            $(this).next("div").css("display", "none");
        });
    }

    $("#settings_trouble_close_list").ajaxForm({
        beforeSubmit: function() {
            if ($.trim($("#date_trouble_close_list").val()) == "") {
                alert("Введите дату поиска!");
                return null;
            }
        },
        success: function(xml) {
            var count = 0;

            $("#admin_trouble_close_list").accordion('destroy');
            $.each($("#admin_trouble_close_list").find('*'), function() {
                $(this).remove();
            });
            $("#admin_trouble_close_list").html('');

            $(xml).find('entry').each(function() {
                count++;

                /*----------------------------------------------------*/
                var $id = $(this).find('id').text();
                var $title = $(this).find('title').text();
                var $place = $(this).find('place').text();
                /*----------------------------------------------------*/
                var $service = $(this).find('service').text();
                var $service_id = $(this).find('service_id').text();
                /*----------------------------------------------------*/
                var $actual_problem = $(this).find('actual_problem').text();
                /*----------------------------------------------------*/
                var $timeout = $(this).find('timeout').text();
                var $date_in = $(this).find('date_in').text();
                var $date_out = $(this).find('date_out').text();
                /*----------------------------------------------------*/
//                var $description = $(this).find('description').text();
                /*----------------------------------------------------*/
                var $hostId = $(this).find('hostId').text();
                var $devc_count = $(this).find("devc_count").text();
                /*----------------------------------------------------*/

                var $head = "<div class='title_p'><div id='" + $id + "_title_item_admin_trouble_list'>" + (($title === "" || $title === "&nbsp;") ? $hostId + ", " + $place : $title + ", ") + "</div><div class='title_ex'>Время аварии: [" + $date_in + "] | Время восстановления: [" + $date_out + "]</div></div>";
                var $h3 = "<h3><div class='panel_kit'>" + $head + "</div></h3>";

                var $block_0 = "<div class=\"text_bold title_rubric\">Заголовок:</div><div id=\"" + $id + "_title\" class=\"cont_rubric\">" + $title + "</div>";
                var $block_2 = "<div class=\"text_bold title_rubric\">Затронутые сервисы:</div><div id=\"" + $id + "_service\" class=\"service_sel\">" + $service + "</div><input type=\"hidden\" value=\"" + $service_id + "\"/>";
                var $block_5 = "<div class=\"text_bold title_rubric\">Дата и время аварии:</div><div id=\"" + $id + "_date_in\" class=\"content_block_inactive\">" + $date_in + "</div>";
                var $block_6 = "<div class=\"text_bold title_rubric\">Сроки устранения:</div><div id=\"" + $id + "_timeout\" class=\"content_block_inactive\">" + $timeout + "</div>";
                var $block_7 = "<div class=\"text_bold title_rubric\">Дата и время устранения аварии:</div><div id=\"" + $id + "_date_out\" class=\"content_block_inactive\">" + $date_out + "</div>";
                var $block_3 = "<div class=\"text_bold title_rubric\">Фактическая проблема:</div><div id=\"" + $id + "_actual_problem\" class=\"cont_rubric\">" + $actual_problem + "</div>";

                var $dev_list = "";
                $(this).find("devcapsul").each(function() {
                    var id_dev = $(this).find('id_dev').text();
                    var host_id_dev = $(this).find('hostId').text();
                    var description_dev = $(this).find('desc').text();
                    var timedown_dev = $(this).find('timedown').text();
                    var timeup_dev = $(this).find('timeup').text();

                    var $cont_dev = "<div class=\"func\"><div><input type=\"button\" id=\"" + id_dev + "_unmerge\" value=\"unmerge\"></div></div>";
                    var $head_dev = "<h1 class='up'><div class='panel_kit'><div class='title_p'><div id=\"" + id_dev + "_title_dev\">" + host_id_dev + ", " + description_dev + "</div><div class='title_ex'>down: [" + timedown_dev + "] | up: [" + timeup_dev + "]</div></div>" + (parseInt($devc_count) > 1 ? $cont_dev : "") + "</div></h1>";
                    $dev_list += "<div class=\"dev_ent\" id=\"" + id_dev + "\">" + $head_dev + "</div>";
                });
                var $block_1 = "<div class=\"text_bold title_rubric\">Список узлов:</div><div><div class=\"short_dev_list\"></div><div id=\"" + $id + "_dev_list\" class=\"dev_list_down\"><div>" + $dev_list + "</div></div></div>";

                var $descriptions = "";
                $(this).find("descriptions").each(function() {
                    var id_desc = $(this).find('id_desc').text();
                    var text_desc = $(this).find('text_desc').text();
                    var time_desc = $(this).find('time_desc').text();
                    var author_desc = $(this).find('author_desc').text();
                    var id_author_desc = $(this).find('id_author_desc').text();

                    $descriptions += "<div class=\"comment_item\"><div class=\"comment_title\"><div class=\"comment_author\">" + author_desc + "</div><div class=\"comment_date\">" + time_desc + "</div></div><div class=\"comment_text\">" + text_desc + "</div></div>";
                });
                var $block_8 = "<div class=\"text_bold title_rubric\">Комментарии:</div><div class=\"trouble_comments\">" + $descriptions + "</div>";


                var $content = "<div class=\"content\" id=\"" + $id + "\">" + $block_0 + $block_1 + $block_2 + $block_5 + $block_6 + $block_7 + $block_3 + $block_8 + "</div>";
                var $check_dop = "<div class=\"trouble_item_check\"><input type=\"checkbox\"/></div>";

                $("<div class=\"trouble_item\">" + $check_dop + "<div id='" + $id + "_block_item_admin_trouble_lists' class=\"trouble_item_accord\">" + $h3 + $content + "</div></div>").appendTo("#admin_trouble_close_list");
            });

            generateShortDevList();

            $("#admin_trouble_close_list").accordion({collapsible: true, header: "h3", autoHeight: false, alwaysOpen: false, active: false, navigation: true, icons: false}).addClass('ui-accordion-trouble');
            $("#admin_trouble_close_list").clickToForm({
                header: ".content",
                elements: {
                    text: {
                        '.cont_rubric': {
                            limit: "200",
                            showCounter: "off"
                        },
                        '.description': {
                            limit: "1500",
                            showCounter: "off"
                        }
                    },
                    select: {
                        '.status_sel': {
                            valuesPlace: "#status_sel",
                            separatorTwin: ";",
                            separatorVal: "|",
                            multiple: ""
                        },
                        '.service_sel': {
                            valuesPlace: "#service_sel",
                            separatorTwin: ";",
                            separatorVal: "|",
                            multiple: "multiple"
                        }
                    },
                    datetimepicker: {
                        '.timeout' : {
                            dateFormat: 'dd/mm/yy',
                            duration: '',
                            showTime: true,
                            constrainInput: false,
                            time24h: true
                        },
                        '.dateout' : {
                            dateFormat: 'dd/mm/yy',
                            duration: '',
                            showTime: true,
                            constrainInput: false,
                            time24h: true
                        },
                        '.datein' : {
                            dateFormat: 'dd/mm/yy',
                            duration: '',
                            showTime: true,
                            constrainInput: false,
                            time24h: true
                        }
                    }
                },
                buttons: {
                    "сохранить": {
                        show: 'afterEdit',
                        onClick: function() {
                            var $this = $.fn.getActiveBlock();

                            var $id = $($this).attr('id');
                            var $title = $("#" + $id + "_title").html();
                            var $service = $("#" + $id + "_service").next(":hidden").val();
//                            var $legend = $("#" + $id + "_legend").html();
//                            var $status = $("#" + $id + "_status").next(":hidden").val();
                            /*var $timeout = $("#" + $id + "_timeout").html();
                            var $date_in = $("#" + $id + "_date_in").html();
                            var $date_out = $("#" + $id + "_date_out").html();*/
                            var $actual_problem = $("#" + $id + "_actual_problem").html();

                            $.ajax({
                                url : "/controller",
                                type : "POST",
                                data : {
                                    cmd: "editTroubleOfCompleteTroubleList",
                                    id: $id,
                                    title: $title,
                                    service: $service,
                                    /*legend: $legend,
                                    status: $status,
                                    timeout: $timeout,
                                    date_in: $date_in,
                                    date_out: $date_out,
                                    description: $description,*/
                                    actual_problem: $actual_problem,
                                    list: "complete"
                                },
                                beforeSend: function() {
                                    $.fn.controll_button("off");
                                    return true;
                                },
                                success: function(data) {
                                    $("#settings_trouble_close_list").submit();
//                                    $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                                }
                            });
                        }
                    },
                    "удалить": {
                        show: 'always',
                        onClick: function() {
                            var $this = $.fn.getActiveBlock();
                            var $id = $($this).attr('id');

                            var $title = $("#" + $id + "_title").html();
                            var $service = $("#" + $id + "_service").next(":hidden").val();
                            /*var $legend = $("#" + $id + "_legend").html();
                            var $status = $("#" + $id + "_status").next(":hidden").val();
                            var $timeout = $("#" + $id + "_timeout").html();
                            var $date_in = $("#" + $id + "_date_in").html();
                            var $description = $("#" + $id + "_description").html();*/
                            var $actual_problem = $("#" + $id + "_actual_problem").html();

                            $.ajax({
                                url : "/controller",
                                type : "POST",
                                data : {
                                    cmd: "deleteTrouble",
                                    id: $id,
                                    title: $title,
                                    service: $service,
                                    /*legend: $legend,
                                    status: $status,
                                    timeout: $timeout,
                                    date_in: $date_in,
                                    description: $description*/
                                    actual_problem: $actual_problem
                                },
                                beforeSend: function() {
                                    $.fn.controll_button("off");

                                    var devs = "";
                                    $.each($($this).find(".dev_ent"), function() {
                                        devs += $(this).attr("id").replace("_dev", "") + "|";
                                    });
                                    $.fn.checkDeviceInfo(devs);

                                    return true;
                                },
                                success: function(data) {
                                    $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                                    $.fn.update_trouble_counters();
                                }
                            });
                        }
                    }
                }
            });
            $("input[id$=_unmerge]").bind("click", function() {
                var $id = $(this).attr("id").replace("_unmerge", "");

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "unmergeTrouble",
                        id_devc: $id
                    },
                    beforeSend: function() {

                    },
                    success: function(data) {
                        $("#settings_trouble_close_list").submit();
                        $.fn.update_trouble_counters();
                    }
                });
            });
            if (count == 0) {
                $('#admin_trouble_close_list').append("<br><div style='text-align: center;'>По вашему запросу ничего не найдено.</div>");
            }
        }
    });

    $("#refresh_trouble_close_list").click(function() {
        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
    });

    $("#checkall_close_list").click(function() {
        $("#admin_trouble_close_list").find(".trouble_item .trouble_item_check :input[type=checkbox]").each(function () {
            if ($("#checkall_close_list").attr("checked")) {
                $(this).attr('checked', 'checked');
            } else {
                $(this).attr('checked', false);
            }
        });
    });

    $("#merge_close_list").click(function() {
        $("#dev_list_merge").empty();
        $("#title_merge, #legend_merge, #description_merge").combobox("clear");
        $("select[id='service_merge'] option").removeAttr('selected');

        var ids = "";
        $("#admin_trouble_close_list").find(".trouble_item").each(function() {
            if ($(this).find(".trouble_item_check :input[type=checkbox]").attr("checked")) {
                ids += $(this).find(".content").attr("id") + ";";
                $("#ids_merge").val(ids);

                var $title = $.trim($(this).find(".content").find("div[id$='_title']").text().replace("&nbsp;", ""));
                if ($title !== "") $("#title_merge").append("<option>" + $title + "</option>");

                var $legend = $.trim($(this).find(".content").find("div[id$='_legend']").text().replace("&nbsp;", ""));
                if ($legend !== "") $("#legend_merge").append("<option>" + $legend + "</option>");

                var $description = $.trim($(this).find(".content").find("div[id$='_description']").text().replace("&nbsp;", ""));
                if ($description !== "") $("#description_merge").append("<option>" + $description + "</option>");

                $.each($(this).find(".content").find(".dev_ent"), function() {
                    var $id_dev = $(this).find("div[id$=_title_dev]");
                    var $dev_id = $($id_dev).attr("id").replace("_title_dev", "");
                    var $dev_desc = $($id_dev).text();
                    $("#dev_list_merge").append("<option id='" + $dev_id + "_dev_merge'>" + $dev_desc + "</option>");
                });

                var $service = $.trim($(this).find(".content").find("div[id$='_service']").text().replace("&nbsp;", ""));
                $.each($service.split(";"), function() {
                    if ($.trim(this.replace(";", "")) !== "") $("select[@id=service_merge] option[value='" + $.trim(this.replace(";", "")) + "']").attr('selected', 'selected');
                });
            }
        });

        if (ids != "") {
            if ($("select[id=title_merge] option").size() > 0) {
                $("#title_merge").next("input").val($("#title_merge :first").text());
            }
            if ($("select[id=legend_merge] option").size() > 0) {
                $("#legend_merge").next("input").val($("#legend_merge :first").text());
            }
            if ($("select[id=description_merge] option").size() > 0) {
                $("#description_merge").next("input").val($("#description_merge :first").text());
            }
            if ($("select[id=status_merge] option").size() > 0) {
                $("#status_merge").next("input").val($("#status_merge :first").text());
            }

            $("#merge_troubles_dialog").dialog('open');
        } else {
            alert("Отметьте хотя бы одну проблему");
        }
        $.fn.update_trouble_counters();
    });

    $("#service_merge").addClass("ui-autocomplete-input ui-widget ui-widget-content ui-corner-left");

    $(".control_panel").css("display", ($("#openControlPanel_close_troubles").val() == "true" ? "block" : "none"));

    $("#troubles_close_page_control_panel").click(function() {
        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "openControlPanel",
                value: $(".control_panel").css("display") == "none"
            }
        });

        if ($(".control_panel").css("display") == "none") {
            $(".control_panel").show("blind", { direction: "vertical" }, 300);
        } else {
            $(".control_panel").hide("blind", { direction: "vertical" }, 300);
        }
    });
});