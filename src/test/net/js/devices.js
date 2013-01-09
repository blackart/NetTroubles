$(document).ready(function() {
    function paintDevicesTable() {
        $("#devices_list_table tr:even").each(function() {
            $(this).css("background", "#bed4ff");
            $(this).find("td").css("border-right-color", "#fff");
        })
    }

    paintDevicesTable();

    $("#devices_list_table .edit_button").click(function() {
        $.fn.device_edit($(this).parent().parent().attr("id"));
    });

    $("#devices_list_table .delete_button").click(function() {
        $.fn.device_delete($(this).parent().parent().attr("id").replace("_device", ""), $(this).parent().parent().find(".device_name").html());
    });

    $("#devices_add .add_device_button").click(function() {
        var $name = $("#devices_add .device_name").val();
        var $description = $("#devices_add .device_description").val();
        var $status = $("#devices_add .device_status").val();
        var $group = $("#devices_add .device_group").val();
        var $region = $("#devices_add .device_region").val();

        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "addDevice",
                name: $name,
                desc: $description,
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

    });
});