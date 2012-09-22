$(document).ready(function() {
    $("#admin_trouble_list").accordion({clearStyle: true, collapsible: true, header: "h3", autoHeight: false, alwaysOpen: false, active: false, navigation: true, animated: false}).addClass('ui-accordion-trouble');

    $.each($(".trouble_comments"), function() {
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

    $('.comment_item, .comment_item_unvisible').bind("mouseover", function() {
        $(this).css("background", "#dbe8ff");
    });

    $(".comment_item, .comment_item_unvisible").bind("mouseout", function() {
        $(this).css("background", "#fff");
    });
});
