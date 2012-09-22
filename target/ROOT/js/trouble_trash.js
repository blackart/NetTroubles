$(document).ready(function() {
    $("#trash_trouble_list").accordion({collapsible: true, header: "h3", autoHeight: false, alwaysOpen: false, active: false, navigation: true, icons: false}).addClass('ui-accordion-trouble');

    $("#trash_trouble_list").clickToForm({
        header: ".content",
        elements: {
            /*text: {
                '.cont_rubric': {
                    limit: "200",
                    showCounter: "off"
                },
                ".description": {
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
                }
            }*/
        },
        buttons: {
            "recovery": {
                show: 'always',
                onClick: function() {
                    var $this = $.fn.getActiveBlock();
                    var $id = $($this).attr('id');

                    $.ajax({
                        url : "/controller",
                        type : "POST",
                        data : {
                            cmd: "recoveryTrouble",
                            id: $id
                        },
                        beforeSend: function() {
                            $.fn.controll_button("off");
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

    $.each($(".trouble_comments"), function() {
        if ($(this).find(".comment_item").length > 1) {
            $(this).prepend("<div class='comment_view_all'>show all</div>");
            $(this).find(".comment_view_all").bind("mouseover", function() {
                $(this).css("color","#000");
                $(this).css("background","#dbe8ff");
                $(this).css("cursor","pointer");
            });
            $(this).find(".comment_view_all").bind("mouseout", function() {
                $(this).css("color","#999999");
                $(this).css("background","#fff");
            });
            $(this).find(".comment_view_all").bind("click", function() {
                if ($(this).parent().find('.comment_item').length == 1) {
                    $(this).text("hide");
                    $(this).nextAll(".comment_item_unvisible").attr("class","comment_item");
                } else {
                    $(this).text("show all");
                    $(this).parent().find(".comment_item").last().prevAll(".comment_item").attr("class","comment_item_unvisible");
                }
            });
        }
        $(this).find(".comment_item").last().prevAll(".comment_item").attr("class","comment_item_unvisible");
    });

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

    $('.comment_item, .comment_item_unvisible').bind("mouseover", function() {
        $(this).css("background", "#dbe8ff");
    });

    $(".comment_item, .comment_item_unvisible").bind("mouseout", function() {
        $(this).css("background", "#fff");
    });

    $("#trash_trouble_list").css("display", "block");

    $("#destroy_all").click(function() {
        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "clearTrashList"
            },
            success: function(data) {
                $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                $.fn.update_trouble_counters();
            }
        })
    });

    $(".control_panel").css("display", ($("#openControlPanel_trash_troubles").val() == "true" ? "block" : "none"));

    $("#troubles_trash_page_control_panel").click(function() {
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