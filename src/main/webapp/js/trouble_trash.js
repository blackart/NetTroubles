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

    $.fn.transformCommentBlock($(".trouble_comments"));
    $.fn.transformDeviceListBlock($(".short_dev_list"));

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