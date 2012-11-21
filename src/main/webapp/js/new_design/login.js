$(document).ready(function () {
    $(".response-message-container").hide();

    $("#username").focus();

    $("#passwd, #login").bind("keypress", function(even) {
        if (event.which == 13) {
            checkUser();
            return false;
        }
    });

    $("#submit-button").click(function() {
        checkUser();
        return false;
    });

    function checkUser() {
        $.ajax({
            url : "/controller",
            type : "POST",
            dataType: 'JSON',
            data: {
                cmd: "checkUser",
                login: $("#login").val(),
                passwd: $("#passwd").val()
            },
            beforeSend: function() {
                return true;
            },
            success: function(data) {
                $(".response-message-container").show();
                $(".response-message").text(data.message);
                if (data.status) {
                    $(".response-message-container div").removeClass("alert-error").addClass("alert-info");
                    $("form").submit();
                } else {
                    $(".response-message-container div").removeClass("alert-info").addClass("alert-error");
                }
            }
        });
    }
});