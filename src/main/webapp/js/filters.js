$(document).ready(function() {
    function paintFiltersTable() {
        $("#filters_list_table tr:even").each(function() {
            $(this).css("background", "#bed4ff");
            $(this).find("td").css("border-right-color", "#fff");
        })
    }

    paintFiltersTable();


    $("#filter_add_type").change(function() {
        if ($("#filter_add_type :selected").text() == 'group') {
            $("#filter_add_value").html("");
            $("#filter_add_value").append($('#append_hostgroups').html());
        } else {
            $("#filter_add_value").html("");
            $("#filter_add_value").append("<input type=\"text\" class=\"filter_value\"/>");
        }
    });

    $(".edit_button").click(function() {
        $.fn.settings_main_filter_edit($(this).parent().parent().attr("id"));
    });

    $(".delete_button").click(function() {
        $.fn.settings_main_filter_delete($(this).parent().parent().attr("id").replace("_mainFilter", ""), $(this).parent().parent().find(".filter_name").html());
    });

    $(".add_filter_button").click(function() {
        var $name = $("#filters_add .filter_name").val();
//        var $value = $("#filters_add .filter_value").val();
        var $type = $("#filters_add .filter_type").val();
        var $policy = $("#filters_add .filter_policy").val();
        var $enable = $("#filters_add .filter_enable").val();

        if ($("#filter_add_type :selected").text() == 'group') {
            var $value = $("#filter_add_value").find("select").val();
        } else {
            $value = $("#filter_add_value").find("input").val();
        }

        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "addMainFilter",
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

    });
});