$(document).ready(function() {
    function paintGroupsTable() {
        $("#groups_list_table tr:even").each(function() {
            $(this).css("background", "#bed4ff");
            $(this).find("td").css("border-right-color", "#fff");
        })
    }

    paintGroupsTable();

    $(".group_edit_button").click(function() {
        $.fn.groups_edit($(this).parent().parent().attr("id"));
    });

    $(".group_delete_button").click(function() {
        $.fn.groups_delete($(this).parent().parent().attr("id").replace("_group", ""), $(this).parent().parent().find(".group_name").html());
    });

    $(".group_add_button").click(function() {
        var $name = $("#groups_add .group_name").val();
        var $menu = {"items":[]};
        $.each($("#groups_add").find("li.group"), function() {
            if ($(this).find("input").attr("checked")) {
                var item = {"id": $(this).attr("id").replace("group-add-","")};
                item.items = [];
                $.each($(this).next("ul").find("li.item"), function() {
                    if ($(this).find("input").attr("checked")) {
                        item.items.push({"id": $(this).attr("id").replace("group-add-","")});
                    }
                });
                $menu.items.push(item);
            }
        });

        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "addGroup",
                name: $name,
                menu_config: JSON.stringify($menu)
            },
            beforeSend: function() {
                if ($.trim($name) === '') {
                    alert("Введите название группы");
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

    });

    $(".menu_items .l1 li").click(function() {
        var a = $(this).attr("id").split("_");
        if (($(this).find("input").attr("checked")) && (a[2] == "main")) {
            $(this).next("ul").find("input").attr("checked", true);
        } else if (!($(this).find("input").attr("checked")) && (a[2] == "main")) {
            $(this).next("ul").find("input").attr("checked", false);
        } else if (($(this).find("input").attr("checked")) && (a[2] == "child")) {
            $(this).parent().prev().find("input").attr("checked", true)
        } else if (!($(this).find("input").attr("checked")) && (a[2] == "child")) {
            var checked_count = 0;
            $(this).parent().find("input").each(function() {
                if ($(this).attr("checked")) {
                    checked_count++;
                }
            });
            if (checked_count == 0) {
                $(this).parent().prev().find("input").attr("checked", false)
            }
        }
    })
});