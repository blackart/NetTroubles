$(document).ready(function() {
    var $settings_main_filter_delete_id;
    var $account_delete_id;
    var $group_delete_id;
    var $device_delete_id;

    var $interval_start = 1;
    var $interval = "";
    var interval_val = 600000;

    $.fn.update_trouble_counters = function() {
        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "getTroubleCounters"
            },
            success: function(data) {
                var $current = $(data).find('current').text();
                var $waiting_close = $(data).find('waiting_close').text();
                var $close = $(data).find('close').text();
                var $trash = $(data).find('trash').text();
                var $need_actual_problem = $(data).find('need_actual_problem').text();

                $(".count_need_actual_problem_troubles").html("(" + $need_actual_problem);
                $(".count_current_troubles").html($current + ")");
                $(".count_waiting_close_troubles").html($waiting_close);
                $(".count_complete_troubles").html("(" + $close + ")");
                $(".count_trash_troubles").html("(" + $trash + ")");
            }
        });
    };

    $.fn.get_interval_val = function() {
        return interval_val;
    };

    $.fn.set_interval_val = function(interval) {
        interval_val = interval;
    };


    $.fn.get_interval_status = function() {
        return $interval_start;
    };

    $.fn.set_interval_status = function(interval) {
        $interval_start = interval;
    };


    $("#logout").click(function () {
        $.ajax({
            url: "/controller",
            type: "POST",
            data: {
                cmd: "logout"
            }
        });
        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
    });

    $("#main_menu").accordion({header: "h3", autoHeight: false, alwaysOpen: false, active: 0, navigation: true, collapsible: false, icons: false
        /*,change: function(event, ui) {
         $("#v_tabs").tabs('select', 0);
         }*/
    }).removeClass('ui-accordion').addClass('ui-accordion-isem');

    $("#v_tabs").tabs({fxSpeed: 'fast', cache: false, selected: 0, ajaxOptions: { async: true }
        /*select: function(event, ui) {

         }*/
    }).removeClass('ui-tabs').addClass('ui-tabs-vertical');

    $("body").ajaxStart(function() {
        $("body").append("<div id='zanaves'><div class='zanaves'></div><img src='../img/ajax-loader_2.gif' alt='load' class='preloader'/></div>");
        $("#zanaves").show();
    });
    $("body").ajaxStop(function() {
        $("#zanaves").hide();
        $("#zanaves").remove();
    });

    $.fn.checkDeviceInfo = function(devs) {
        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "checkStatusDevice",
                devs: devs
            },
            success: function(data) {
                $("#device_change_dialog #devices_change_list_table").html("");
                $("#device_change_dialog #devices_change_list_table").append("<tr class='header'><td>name</td><td>description</td><td>status</td><td>group</td><td>region</td></tr>");

                var $device_count = 0;
                $.each($(data).find("device"), function() {
                    $device_count++;
                    var $id = $(this).find("id").text();
                    var $name = $(this).find("name").text();
                    var $desc = $(this).find("desc").text();
                    var $group_id = $(this).find("group_id").text();
                    var $region_id = $(this).find("region_id").text();

                    var $name_str = "<td class='device_name'>" + $name + "</td>";
                    var $desc_str = "<td class='device_desc'><input type='text' id='devices_edit_desc' value='" + $desc + "'/></td>";
                    var $status_str = "<td class='device_host_status'><select id='host_status_device_change'>" + $("#host_status_replace").html() + "</select></td>";
                    var $group_str = "<td class='device_host_group'><select id='host_group_device_change'>" + $("#host_group_replace").html() + "</select></td>";
                    var $region_str = "<td class='device_host_region'><select id='host_region_device_change'>" + $("#host_region_replace").html() + "</select></td>";

                    $("#device_change_dialog #devices_change_list_table").append("<tr id='" + $id + "_device_change'>" + $name_str + $desc_str + $status_str + $group_str + $region_str + "</tr>");
                    $("#" + $id + "_device_change .device_host_group select [value='" + $group_id + "']").attr("selected", "selected");
                    $("#" + $id + "_device_change .device_host_region select [value='" + $region_id + "']").attr("selected", "selected");
                });
                if ($device_count != 0) $("#device_change_dialog").dialog("open");
            }
        });
    };

    /*-------------------------for current and close trouble tab-------------------------*/
    /*---------------------------------------dialogs-------------------------------------*/
    $("#title_merge, #actual_problem_merge").combobox();
    $("#status_merge").combobox({readonly: true});
    $("#merge_troubles_dialog").dialog({ autoOpen: false, title: "Merge troubles", position: "center", modal: true, resizable: false, draggable: false, height: 400, width: 600, maxHeight: 400,maxWidth: 600,
        buttons: {
            "Ok": function() {

                $("#title_merge").combobox("value");
                var $id = $("#ids_merge").val();
                var $title = $("#title_merge").combobox("value");

                var $service = "";
                $("select[id=service_merge] option:selected").each(function() {
                    $service += this.id.replace("_service_merge", "") + ";";
                });

                var $actual_problem = $("#actual_problem_merge").combobox("value");

                var $dev_id = "";
                $("select[id=dev_list_merge] option").each(function() {
                    $dev_id += this.id.replace("_dev_merge", "") + "|";
                });

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "mergeTroubles",
                        id: $id,
                        title: $title,
                        service: $service,
                        actual_problem: $actual_problem,
                        id_dev: $dev_id
                    },
                    beforeSend: function() {
                        $.fn.checkDeviceInfo($dev_id);
                        return true;
                    },
                    success: function(data) {
                        if ($(data).find("return_").text() === "2") {
                            $("#settings_trouble_close_list").submit();
                        } else {
                            $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                        }
                        $.fn.update_trouble_counters();

                        var message = $(data).find("message");
                        if (message.length == 1) alert($(message).text());
                    }
                });

                $(this).dialog("close");
            }
        }
    });

    $("#device_change_dialog").dialog({ autoOpen: false, title: "Device change", position: "center", width: 800, height: 'auto',  modal: true, resizable: false, draggable: false,
        buttons: {
            "save": function() {
                var id = "";
                var desc = "";
                var status = "";
                var group = "";
                var region = "";

                $.each($("#devices_change_list_table").find("tr[id$='_device_change']"), function() {
                    id += $(this).attr("id").replace("_device_change", "") + "|";
                    desc += $(this).find("#devices_edit_desc").val() + "|";
                    status += $(this).find("#host_status_device_change").val() + "|";
                    group += $(this).find("#host_group_device_change").val() + "|";
                    region += $(this).find("#host_region_device_change").val() + "|";
//                    alert($(this).find(".device_name").text() + " , " + $(this).find("#devices_edit_desc").val() + " , " + $(this).find("#host_status_device_change").val() + " , " + $(this).find("#host_group_device_change").val());
                });

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "editSomeDevices",
                        ids: id,
                        desc: desc,
                        status_id: status,
                        group_id: group,
                        region_id: region
                    },
                    success: function(data) {
                        $("#device_change_dialog").dialog("close");
                    }
                });
            }
        }
    });

    $("#settings_edit_dialog").dialog({ autoOpen: false, title: "Edit filter", position: "center", modal: true, resizable: false, draggable: true, height: 200, width: 600, maxHeight: 200,maxWidth: 600,
        buttons: {
            "Cancel" : function() {
                $(this).dialog("close");
            },
            "Ok": function() {
                var $id = $("#settings_main_filter_edit_id").val();
                var $name = $("#settings_main_filter_edit_name").val();
//                var $value = $("#settings_main_filter_edit_value").val();
                var $type = $("#settings_main_filter_edit_type").val();
                var $policy = $("#settings_main_filter_edit_policy").val();
                var $enable = $("#settings_main_filter_edit_enable").val();

                if ($("#settings_main_filter_edit_type :selected").text() == 'group') {
                    var $value = $("#filter_edit_value").find("select").val();
                } else {
                    $value = $("#filter_edit_value").find("input").val();
                }

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "editMainFilter",
                        id: $id,
                        name: $name,
                        value: $value,
                        type: $type,
                        policy: $policy,
                        enable: $enable
                    },
                    beforeSend: function() {
                        if ($.trim($name) === '') {
                            alert("Введите название фильтра");
                            return false;
                        } else if ($.trim($value) === '') {
                            alert("Введите значение фильтра");
                        }
                    },
                    success: function(data) {
                        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                    }
                });
                $(this).dialog("close");
            }
        }
    });
    $("#settings_delete_dialog").dialog({ autoOpen: false, title: "Edit filter", position: "center", modal: true, resizable: false, draggable: true,
        buttons: {
            "No" : function() {
                $(this).dialog("close");
            },
            "Yes": function() {

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "deleteMainFilter",
                        id: $settings_main_filter_delete_id
                    },
                    success: function(data) {
                        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                    }
                });

                $(this).dialog("close");
            }
        }
    });

    $("#users_edit_dialog").dialog({ autoOpen: false, title: "Edit user account", position: "center", modal: true, resizable: false, draggable: true, height: 200, width: 700, maxHeight: 200,maxWidth: 700,
        buttons: {
            "Cancel" : function() {
                $(this).dialog("close");
            },
            "Ok": function() {
                var $id = $("#users_edit_id").val();
                var $login = $("#users_edit_login").val();
                var $passwd = $("#users_edit_passwd").val();
                var $passwd_confirm = $("#users_edit_confirm_passwd").val();
                var $name = $("#users_edit_name").val();
                var $group = $("#users_edit_group").val();
                var $block = $("#users_edit_block").attr("checked") ? true : false;

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "editAccount",
                        id: $id,
                        login: $login,
                        passwd: $passwd,
                        name: $name,
                        group: $group,
                        block: $block
                    },
                    beforeSend: function() {
                        if ($.trim($login) === '') {
                            alert("Введите логин");
                            return false;
                        } else if ($.trim($name) === '') {
                            alert("Введите имя");
                            return false;
                        } else if ($passwd !== $passwd_confirm) {
                            alert("Пароли не совпадают");
                            return false;
                        }
                    },
                    success: function(data) {
                        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                    }
                });

                $(this).dialog("close");
            }
        }
    });
    $("#users_delete_dialog").dialog({ autoOpen: false, title: "Delete user account", position: "center", modal: true, resizable: false, draggable: true,
        buttons: {
            "No" : function() {
                $(this).dialog("close");
            },
            "Yes": function() {

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "deleteAccount",
                        id: $account_delete_id
                    },
                    success: function(data) {
                        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                    }
                });

                $(this).dialog("close");
            }
        }
    });

    $("#groups_edit_dialog").dialog({ autoOpen: false, title: "Edit filter", position: "center", modal: true, resizable: false, draggable: true, height: 450, width: 600, maxHeight: 450,maxWidth: 600,
        buttons: {
            "Cancel" : function() {
                $(this).dialog("close");
            },
            "Ok": function() {
                var $id = $("#groups_edit_id").val();
                var $name = $("#groups_edit_name").val();

                var $menu = {"items":[]};
                $.each($("#groups_edit_dialog").find("li.group"), function() {
                    if ($(this).find("input").attr("checked")) {
                        var item = {"id": $(this).attr("id").replace("diag-group-edit-","")};
                        item.items = [];
                        $.each($(this).next("ul").find("li.item"), function() {
                            if ($(this).find("input").attr("checked")) {
                                item.items.push({"id": $(this).attr("id").replace("diag-group-edit-","")});
                            }
                        });
                        $menu.items.push(item);
                    }
                });

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "editGroup",
                        id: $id,
                        name: $name,
                        menu_config: JSON.stringify($menu)
                    },
                    beforeSend: function() {
                        if ($.trim($name) === '') {
                            alert("Введите название шруппы");
                            return false;
                        }
                    },
                    success: function(data) {
                        if (data) {
                            var json_data = JSON.parse(data);
                            if (json_data.message) alert(json_data.message);
                        } else {
                            $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                        }
                    }
                });
                $(this).dialog("close");
            }
        }
    });
    $("#groups_delete_dialog").dialog({ autoOpen: false, title: "Delete group", position: "center", modal: true, resizable: false, draggable: true,
        buttons: {
            "No" : function() {
                $(this).dialog("close");
            },
            "Yes": function() {

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "deleteGroup",
                        id: $group_delete_id
                    },
                    success: function(data) {
                        if (data) {
                            var json_data = JSON.parse(data);
                            if (json_data.message) alert(json_data.message);
                        } else {
                            $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                        }
                    }
                });

                $(this).dialog("close");
            }
        }
    });

    $("#devices_edit_dialog").dialog({ autoOpen: false, title: "Edit device", position: "center", modal: true, resizable: false, draggable: true, height: 200, width: 800, maxHeight: 200,maxWidth: 800,
        buttons: {
            "Cancel" : function() {
                $(this).dialog("close");
            },
            "Ok": function() {
                var $id = $("#devices_edit_id").val();
                var $name = $("#device_edit_name").val();
                var $desc = $("#device_edit_description").val();
                var $status = $("#device_edit_status").val();
                var $group = $("#device_edit_group").val();
                var $region = $("#device_edit_region").val();

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "editDevice",
                        id: $id,
                        name: $name,
                        desc: $desc,
                        status: $status,
                        group: $group,
                        region: $region
                    },
                    beforeSend: function() {
                        if ($.trim($name) === '') {
                            alert("Введите название устройства!");
                            return false;
                        }
                    },
                    success: function(data) {
                        alert($(data).find("message").text());
                        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                    }
                });

                $(this).dialog("close");
            }
        }
    });
    $("#devices_delete_dialog").dialog({ autoOpen: false, title: "Delete group", position: "center", modal: true, resizable: false, draggable: true,
        buttons: {
            "No" : function() {
                $(this).dialog("close");
            },
            "Yes": function() {

                $.ajax({
                    url : "/controller",
                    type : "POST",
                    data : {
                        cmd: "deleteDevice",
                        id: $device_delete_id
                    },
                    success: function(data) {
                        alert($(data).find("message").text());
                        $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                    }
                });

                $(this).dialog("close");
            }
        }
    });

    /*-----------------------------functions--------------------------------------------*/

    $.fn.settings_main_filter_edit = function(id) {
        $("#settings_main_filter_edit_id").val(id.replace("_mainFilter", ""));

        $("#settings_main_filter_edit_name").val($("#" + id + " .filter_name").html());


        $("#settings_main_filter_edit_type :contains('" + $("#" + id + " .filter_type").html() + "')").attr("selected", "selected");
        $("#settings_main_filter_edit_policy").val($("#" + id + " .filter_policy").html());
        $("#settings_main_filter_edit_enable").val($("#" + id + " .filter_enable").html());

        if ($("#settings_main_filter_edit_type :selected").text() == 'group') {
            $("#filter_edit_value").html("");
            $("#filter_edit_value").append($('#append_hostgroups').html());
            $("#filter_edit_value").find("select :contains('" + $("#" + id + " .filter_value").html() + "')").attr("selected", "selected");
        } else {
            $("#filter_edit_value").html("");
            $("#filter_edit_value").append("<input type=\"text\" class=\"filter_value\"/>");
            $("#filter_edit_value").find("input").val($("#" + id + " .filter_value").html());
        }

        $("#settings_edit_dialog").dialog('open');
    };
    $.fn.settings_main_filter_delete = function(id, name) {
        $settings_main_filter_delete_id = id;
        $("#delete_main_filter").html(name);
        $("#settings_delete_dialog").dialog('open');
    };

    $.fn.users_edit = function(id) {
        $("#users_edit_id").val(id.replace("_user", ""));

        $("#users_edit_login").val($("#" + id + " .account_login").html());
        $("#users_edit_name").val($("#" + id + " .account_name").html());

        $("#users_edit_group :contains('" + $("#" + id + " .account_group").html() + "')").attr("selected", "selected");
        if ($("#" + id + " .account_block").html() == "true") {
            $("#users_edit_block").attr("checked", "checked");
        } else {
            $("#users_edit_block").removeAttr("checked");
        }

        $.each($(".settings_dialog .label_container input"), function() {
            if ($(this).val() != '') $(this).prev().css({"display": "none"});
        });

        $("#users_edit_dialog").dialog('open');
    };

    $(".settings_dialog .label_container input").focus(function(e){
        var clicked = $(e.target);
        if (clicked.val() == '') clicked.prev().css({"display": "none"});
    });

    $(".settings_dialog .label_container input").blur(function(e){
        var clicked = $(e.target);
        if (clicked.val() == '') clicked.prev().css({"display": "block"});
    });

    $.fn.users_delete = function(id, name) {
        $account_delete_id = id;
        $("#delete_user").html(name);
        $("#users_delete_dialog").dialog('open');
    };

    $.fn.groups_edit = function(id) {
        var real_id = id.replace("_group", "");
        $("#groups_edit_id").val(real_id);
        $("#groups_edit_name").val($("#" + id + " .group_name").html());

        $("#" + id + " .l1 li").each(function() {
            $("#groups_edit_dialog .l1").find("li[id=" + $(this).attr("id").replace("group-" + real_id, "diag-group") + "] input").attr("checked", $(this).find("input").attr("checked"));
        });

        $("#groups_edit_dialog").dialog('open');
    };
    $.fn.groups_delete = function(id, name) {
        $group_delete_id = id;
        $("#delete_group").html(name);
        $("#groups_delete_dialog").dialog('open');
    };

    $.fn.device_edit = function(id) {
        var real_id = id.replace("_device", "");
        $("#devices_edit_id").val(real_id);

        $("#device_edit_status").html("");
        $("#device_edit_status").append($("#host_status_replace").html());
        $("#device_edit_group").html("");
        $("#device_edit_group").append($("#host_group_replace").html());
        $("#device_edit_region").html("");
        $("#device_edit_region").append($("#host_region_replace").html());

        $("#device_edit_status option").each(function() {
            $(this).attr("value", $(this).attr("value"));
        });
        $("#device_edit_group option").each(function() {
            $(this).attr("value", $(this).attr("value"));
        });
        $("#device_edit_region option").each(function() {
            $(this).attr("value", $(this).attr("value"));
        });

        $("#device_edit_name").val($("#" + id + " .device_name").html());
        $("#device_edit_description").val($("#" + id + " .device_desc").html());

        $("#device_edit_status :contains('" + $("#" + id + " .device_host_status").html() + "')").attr("selected", "selected");
        $("#device_edit_group :contains('" + $("#" + id + " .device_host_group").html() + "')").attr("selected", "selected");
        $("#device_edit_region :contains('" + $("#" + id + " .device_host_region").html() + "')").attr("selected", "selected");

        $("#devices_edit_dialog").dialog('open');
    };
    $.fn.device_delete = function(id, name) {
        $device_delete_id = id;
        $("#delete_device").html(name);
        $("#devices_delete_dialog").dialog('open');
    };

    /*---------------------------------------------------------------------------------*/

    $.fn.checkSelectFilterType = function() {
        if ($("#settings_main_filter_edit_type :selected").text() == 'group') {
            $("#filter_edit_value").html("");
            $("#filter_edit_value").append($('#append_hostgroups').html());
        } else {
            $("#filter_edit_value").html("");
            $("#filter_edit_value").append("<input type=\"text\" class=\"filter_value\"/>");
        }
    };
    $("#settings_main_filter_edit_type").change(function() {
        $.fn.checkSelectFilterType();
    });

    var rewidth = function() {
        var $container_width = $("#v_tabs").css("width").replace("px", "");
        if ($container_width <= 800) {
            $container_width = 800;
        }
        $(".ui-tabs-vertical .ui-tabs-panel").css("width", ($container_width - 245) + "px");
    };

    $(window).bind('resize', function() {
        rewidth();
    });

    rewidth();

    function r() {
        var sel = $("#v_tabs").tabs('option', 'selected');
        if (sel == 0) {
            $("#v_tabs").tabs('load', sel);
        }
    }

    $.fn.reload_page = function(num, start) {
        if (start == 1) {
            $interval = setInterval(r, num);
            $.fn.set_interval_status(start);
        } else if (start == 0) {
//            interval = setInterval(r, 0);
            clearInterval($interval);
            $interval = "";
            $.fn.set_interval_status(start);
        }
        $.fn.update_trouble_counters();
    };

    if ($.fn.get_interval_status() == 1) {
        $.fn.reload_page($.fn.get_interval_val(), 1);
    }
    $.fn.update_trouble_counters();

});