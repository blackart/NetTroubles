
$(document).ready(function() {
    $("#admin_trouble_list").accordion({collapsible: true, header: "h3", autoHeight: false, alwaysOpen: false, active: false, navigation: true, icons: false}).addClass('ui-accordion-trouble');
    $("#admin_waiting_close_trouble_list").accordion({collapsible: true, header: "h3", autoHeight: false, alwaysOpen: false, active: false, navigation: true, icons: false}).removeClass('ui-accordion').addClass('ui-accordion-trouble-wait');
    $("#admin_need_actual_problem_trouble_list").accordion({collapsible: true, header: "h3", autoHeight: false, alwaysOpen: false, active: false, navigation: true, icons: false}).removeClass('ui-accordion').addClass('ui-accordion-trouble-need-actual-problem');

    $.fn.transformCommentBlock = function(source) {
        $.each(source, function() {
            if ($(this).find(".comment_item").length > 1) {
                $(this).prepend("<div class='comment_view_all'>show all</div>");
                $(this).find(".comment_view_all").bind("mouseover", function() {
                    $(this).css("color", "#000");
                    $(this).css("background", "#dbe8ff");
                    $(this).css("cursor", "pointer");
                });
                $(this).find(".comment_view_all").bind("mouseout", function() {
                    $(this).css("color", "#999999");
                    $(this).css("background", "#fff");
                });
                $(this).find(".comment_view_all").bind("click", function() {
                    if ($(this).parent().find('.comment_item').length == 1) {
                        $(this).text("hide");
                        $(this).nextAll(".comment_item_unvisible").attr("class", "comment_item");
                    } else {
                        $(this).text("show all");
                        $(this).parent().find(".comment_item").last().prevAll(".comment_item").attr("class", "comment_item_unvisible");
                    }
                });
            }
            $(this).find(".comment_item").last().prevAll(".comment_item").attr("class", "comment_item_unvisible");
        });
    };
    $.fn.transformDeviceListBlock = function(source) {
        $.each(source, function() {
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
    };

    $.fn.send_comment = function($active_block, $text_comment) {
        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "addComment",
                id: $($active_block).attr('id'),
                text: $text_comment
            },
            success: function(data) {
                var $text = $(data).find('text').text();
                var $date = $(data).find('date').text();
                var $author = $(data).find('author').text();
                var $insert_comment = $('<div class="comment_item"><div class="comment_title"><div class="comment_author">' + $author + '</div><div class="comment_date">' + $date + '</div></div><div class="comment_text">' + $text + '</div></div>');

                $($active_block).find(".comment_send").find("textarea").val("");

                var $comments = $($active_block).find(".trouble_comments").find('div[class^="comment_item"]');

                if ($($comments).length == 0) {
                    $($insert_comment).insertBefore($($active_block).find(".comment_send"));
                } else if ($($comments).length == 1) {
                    $($insert_comment).insertBefore($($active_block).find(".comment_send"));
                    $.fn.transformCommentBlock($($active_block).find(".trouble_comments"));
                } else {
                    var $last_comment = $($comments).last();
                    var invisible = true;
                    $($last_comment).prevAll(".comment_item").each(function() {
                        if ($(this).attr("class") != "comment_item_unvisible") {
                            invisible = invisible && false;
                        }
                    });
                    if (invisible) $($last_comment).attr("class", "comment_item_unvisible");

                    $($insert_comment).insertAfter($last_comment);
                }


                $($insert_comment).bind("mouseover", function() {
                    $(this).css("background", "#dbe8ff");
                });

                $($insert_comment).bind("mouseout", function() {
                    $(this).css("background", "#fff");
                });
            },
            beforeSend: function() {
                if ($.trim($text_comment) == "") {
                    return false;
                }
                return true;
            }
        });
    };


    $(".troubles_lists").clickToForm({
        header: ".content",
        elements: {
            text: {
                '.cont_rubric': {
                    limit: "200",
                    showCounter: "off"
                },
                ".actual_problem": {
                    limit: "15000",
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
                }
            }
        },
        buttons: {
            "сохранить": {
                show: 'always',
                onClick: function() {
                    var $this = $.fn.getActiveBlock();

                    var $id = $($this).attr('id');
                    var $title = $("#" + $id + "_title").html();
                    var $service = $("#" + $id + "_service").next(":hidden").val();
                    var $timeout = $("#" + $id + "_timeout").html();
                    var $actual_problem = $("#" + $id + "_actual_problem").html();

                    $.ajax({
                        url : "/controller",
                        type : "POST",
                        data : {
                            cmd: "editTroubleOfCurrentTroubleList",
                            id: $id,
                            title: $title,
                            service: $service,
                            timeout: $timeout,
                            actual_problem: $actual_problem
                        },
                        beforeSend: function() {
                            $.fn.controll_button("off");

                            var $text = $($this).find(".comment_send").find("textarea").val();

                            if ($.trim($text) != "") {
                                $.ajax({
                                    url : "/controller",
                                    type : "POST",
                                    data : {
                                        cmd: "addComment",
                                        id: $($this).attr('id'),
                                        text: $.trim($text)
                                    }
                                });
                            }

                            var devs = "";
                            $.each($($this).find(".dev_ent"), function() {
                                devs += $(this).attr("id").replace("_dev", "") + "|";
                            });
                            $.fn.checkDeviceInfo(devs);

                            return true;
                        },
                        success: function(data) {
                            $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
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
                    var $timeout = $("#" + $id + "_timeout").html();
                    var $actual_problem = $("#" + $id + "_actual_problem").html();

                    $.ajax({
                        url : "/controller",
                        type : "POST",
                        data : {
                            cmd: "deleteTrouble",
                            id: $id,
                            title: $title,
                            service: $service,
                            timeout: $timeout,
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
                            var $status_crm = $(data).find("status").text();
                            if ($status_crm == "false") {
                                alert($(data).find("message").text());
                            }
                            $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                            $.fn.update_trouble_counters();
                        }
                    });
                }
            },
            "отправить в CRM": {
                show: 'always',
                onClick: function() {
                    var $this = $.fn.getActiveBlock();
                    var $id = $($this).attr('id');

                    var $title = $("#" + $id + "_title").html();
                    var $service = $("#" + $id + "_service").next(":hidden").val();
                    var $timeout = $("#" + $id + "_timeout").html();
                    var $actual_problem = $("#" + $id + "_actual_problem").html();

                    if ($.trim($timeout) != "") {
                        var find_date = $timeout.split(" ");

                        var date_split = find_date[0].split("/");
                        var time_split = find_date[1].split(":");

                        var date = new Date(date_split[2], date_split[1] - 1, date_split[0], time_split[0], time_split[1], time_split[2], 0);
                        var curr_date = new Date();
                    } else {
                        $timeout = '';
                    }

                    $.fn.send_trouble = function() {
                        $.ajax({
                            url : "/controller",
                            type : "POST",
                            data : {
                                cmd: "sendToCRM",
                                id: $id,
                                title: $title,
                                service: $service,
                                timeout: $timeout,
                                actual_problem: $actual_problem
                            },
                            beforeSend: function() {
                                var $comments = $($this).find('div[class^="comment_item"]');

                                if ($.trim($title) == "") {
                                    alert("Введите заголовок проблемы");
                                    return false;
                                } else if ($.trim($service) == "") {
                                    alert("Укажите затронутые сервисы");
                                    return false;
                                } else if (($.trim($timeout) == "") && ($($this).parent().parent().parent().attr("id") == "admin_trouble_list")) {
                                    alert("Укажите примерное время устранения аварии");
                                    return false;
                                } else if (date < curr_date) {
                                    alert("Срок устранения аварии истёк, актуализируйте сроки");
                                    return false;
                                } else if ($($comments).length == 0) {
                                    alert("Добавьте хотя бы один комментарий по проблеме");
                                    return false;
                                }

                                var devs = "";
                                $.each($($this).find(".dev_ent"), function() {
                                    devs += $(this).attr("id").replace("_dev", "") + "|";
                                });
                                $.fn.checkDeviceInfo(devs);

                                $.fn.controll_button("off");

                                return true;
                            },
                            success: function(data) {
                                var $status_crm = $(data).find("status").text();
                                if ($status_crm == "false") {
                                    alert($(data).find("message").text());
                                }
                                $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                                $.fn.update_trouble_counters();
                            }
                        });
                    };

                    $.fn.send_comment($this, $($this).find(".comment_send textarea").val());
                    $.fn.send_trouble();
                }
            }
        }
    });

    $.fn.transformCommentBlock($(".trouble_comments"));
    $.fn.transformDeviceListBlock($(".short_dev_list"));

    $('.comment_item, .comment_item_unvisible').bind("mouseover", function() {
        $(this).css("background", "#dbe8ff");
    });

    $(".comment_item, .comment_item_unvisible").bind("mouseout", function() {
        $(this).css("background", "#fff");
    });

    $("#refresh_trouble_list").click(function() {
        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
        $.fn.update_trouble_counters();
    });

    $("#checkall").click(function() {
        $(".troubles_lists").find(".trouble_item .trouble_item_check :input[type=checkbox]").each(function () {
            if ($("#checkall").attr("checked")) {
                $(this).attr('checked', 'checked');
            } else {
                $(this).attr('checked', false);
            }
        });
    });

    function checkSelectTroubles() {
        var ok_wait = false;
        var ok_curr = false;
        $("#admin_trouble_list").find(".trouble_item .trouble_item_check :input[type=checkbox]").each(function () {
            ok_wait = ok_wait || $(this).attr("checked");
        });

        $("#admin_waiting_close_trouble_list").find(".trouble_item .trouble_item_check :input[type=checkbox]").each(function () {
            ok_curr = ok_curr || $(this).attr("checked");
        });
        alert(ok_wait && ok_curr);

        return ok_wait && ok_curr;
    }

    $(".troubles_lists #admin_trouble_list").css("display", "block");
    $(".troubles_lists #admin_waiting_close_trouble_list").css("display", "block");

    $("#merge").click(function() {
        $("#dev_list_merge").empty();
        $("#title_merge, #legend_merge, #description_merge").combobox("clear");
        $("select[id='service_merge'] option").removeAttr('selected');

        var ids = "";
        $(".troubles_lists").find(".trouble_item").each(function() {
            if ($(this).find(".trouble_item_check :input[type=checkbox]").attr("checked")) {
                ids += $(this).find(".content").attr("id") + ";";
                $("#ids_merge").val(ids);

                var $title = $.trim($(this).find(".content").find("div[id$='_title']").text().replace("&nbsp;", ""));
                if ($title !== "") $("#title_merge").append("<option>" + $title + "</option>");

                var $actual_problem = $.trim($(this).find(".content").find("div[id$='_actual_problem']").text().replace("&nbsp;", ""));
                if ($actual_problem !== "") $("#actual_problem_merge").append("<option>" + $actual_problem + "</option>");

                $.each($(this).find(".content").find(".dev_ent"), function() {
                    var $id_dev = $(this).find("div[id$=_title_dev]");
                    var $dev_id = $($id_dev).attr("id").replace("_title_dev", "");
                    var $dev_desc = $($id_dev).text();
                    $("#dev_list_merge").append("<option id='" + $dev_id + "_dev_merge'>" + $dev_desc + "</option>");
                });

                var $service = $.trim($(this).find(".content").find("div[id$='_service']").text().replace("&nbsp;", ""));
                $.each($service.split(";"), function() {
                    debugger;
                    var service = $.trim(this.replace(";", ""));
                    if (service !== "") {
                        $("select[id='service_merge'] option").each(function() {
                            if (this.value == service) $(this).attr('selected', 'selected');
                        })
                    }
                });
            }
        });

        if (ids != "") {
            if ($("select[id=title_merge] option").size() > 0) {
                $("#title_merge").next("input").val($("#title_merge :first").text());
            }
            if ($("select[id=actual_problem_merge] option").size() > 0) {
                $("#actual_problem_merge").next("input").val($("#actual_problem_merge :first").text());
            }
            if ($("select[id=status_merge] option").size() > 0) {
                $("#status_merge").next("input").val($("#status_merge :first").text());
            }

            $("#merge_troubles_dialog").dialog('open');
        } else {
            alert("Отметьте хотя бы одну проблему");
        }
    });

    function check_timeout() {
        $.each($("#admin_trouble_list div[id$='_timeout']"), function() {
            if ($.trim($(this).text()) != "") {
                var find_date = $(this).text().split(" ");

                var date_split = find_date[0].split("/");
                var time_split = find_date[1].split(":");

                var date = new Date(date_split[2], date_split[1] - 1, date_split[0], time_split[0], time_split[1], time_split[2], 0);
                var curr_date = new Date();
                if (date < curr_date) {
                    debugger;
                    var elem = $(this).parent().parent();
                    $(elem).find("h3").css({background: "#000", color: "#FFF", border: "3px solid red"});
                    $(elem).find("h3 .title_timeout").text("Срок устранения аварии истёк");
                    $(elem).find("h3 .title_timeout").css({color: "red", "text-align": "right"});
                }
            }
        });
    }

    check_timeout();

    $("#reload_page").click(function() {
        if ($.fn.get_interval_status() == 0) {
            $.fn.reload_page($.fn.get_interval_val(), 1);         //900000 - 15 минут
            $(this).val("stop reload");
        } else if ($.fn.get_interval_status() == 1) {
            $.fn.reload_page($.fn.get_interval_val(), 0);
            $(this).val("start reload");
        }

        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "reloadPage",
                value: ($.fn.get_interval_status() == 1)
            }
        });

        $.fn.set_interval_val($("#interval_val").val());
    });

    $("#set_interval").click(function() {
        $.fn.set_interval_val($("#interval_val").val());

        if ($.fn.get_interval_status() == 1) {
            $.fn.reload_page($.fn.get_interval_val(), 0);
            $.fn.reload_page($.fn.get_interval_val(), 1);
        }

        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "timeOutReloadPage",
                value: $("#interval_val").val()
            }
        });

    });

    $.fn.set_interval_val($("#timeoutReloadPage").val());

    $("#interval_val").val($.fn.get_interval_val());

    $.fn.set_interval_status($("#pageReload").val() == "true" ? 1 : 0);

    if ($.fn.get_interval_status() == 1) {
        $("#reload_page").val("stop reload");
    } else if ($.fn.get_interval_status() == 0) {
        $("#reload_page").val("start reload");
    }

    $("input[id$=_unmerge]").click(function() {
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
                $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                $.fn.update_trouble_counters();
            }
        });
    });
    $("#service_merge").addClass("ui-autocomplete-input ui-widget ui-widget-content ui-corner-left");

    $(".control_panel").css("display", ($("#openControlPanel").val() == "true" ? "block" : "none"));

    $("#troubles_page_control_panel").click(function() {
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

    $(".comment_send_button").click(function() {
        var $active_block = $(this).parent().parent().parent();
        var $text_comment = $(this).prev("textarea").val();

        $.fn.send_comment($active_block, $text_comment);
    });
});