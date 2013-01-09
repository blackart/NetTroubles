$(document).ready(function() {
    function paintUsersTable() {
        $("#users_list_table tr:even").each(function() {
            $(this).css("background", "#bed4ff");
            $(this).find("td").css("border-right-color", "#fff");
        })
    }

    paintUsersTable();

    $(".account_edit_button").click(function() {
        $.fn.users_edit($(this).parent().parent().attr("id"));
    });

    $(".account_delete_button").click(function() {
        $.fn.users_delete($(this).parent().parent().attr("id").replace("_user", ""), $(this).parent().parent().find(".account_login").html());
    });

    $(".account_add_button").click(function() {
        var $login = $("#users_add .account_login").val();
        var $passwd = $("#users_add .account_passwd").val();
        var $name = $("#users_add .account_name").val();
        var $group = $("#users_add .account_group").val();
        var $block = $("#users_add .account_block").attr("checked") ? true : false;

//        alert($login + " | " + $passwd + " | " + $name + " | " + $group + "|" + $block);

        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "addAccount",
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
                } else if ($.trim($passwd) === '') {
                    alert("Введите пароль");
                } else if ($.trim($name) === '') {
                    alert("Введите Ф.И.О. пользователя");
                }
            },
            dataType: "json",
            success: function(data) {
                if (data) {
                    if (data.message) alert(data.message);
                } else {
                    $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                }
            }
        });

    });
});