$(document).ready(function() {
    $("#login").focus();
    $("#login-form-id").ajaxForm({
        success: function(data) {
            $("#login-button").attr("disabled", true);
            var ar = data.split("|");
            var res = ar[0];
            var red = $.trim(ar[1]);

            $("#response").text(res);
            if (red == "/login") {
                $("#response").css("color", "#ff3333");
                $("#login-button").attr("disabled", false);
            } else {
                $("#response").css("color", "#666666");
                document.location = red;
            }
        }
    });
});