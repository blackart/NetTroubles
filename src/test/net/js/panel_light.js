$(document).ready(function() {
    /*$.fn.get_troble_list = function() {
     $.ajax({
     url: "/info/controller",
     type: "POST",
     dataType: "xml",
     data: {cmd: "getLogEntry"},
     success: function(xml) {
     $("#admin_trouble_list").accordion('destroy');
     $.each($("#admin_trouble_list").find('*'), function() {
     $(this).remove();
     });
     $("#admin_trouble_list").html('');

     $(xml).find('entry').each(function() {
     var $id = $(this).find('id').text();

     var $title = $(this).find('title').text();
     var $place = $(this).find('place').text();

     var $service = $(this).find('service').text();

     var $legend = $(this).find('legend').text();

     var $status = $(this).find('status').text();

     var $timeout = $(this).find('timeout').text();
     var $date_in = $(this).find('date_in').text();
     var $date_out = $(this).find('date_out').text();
     var $description = $(this).find('description').text();

     var $hostId = $(this).find('hostId').text();


     var $head = "<div class='title_p'><div id='" + $id + "_title_item_admin_trouble_list'>" + ($title === "" ? "" : $title + ", ") + $hostId + ", " + $place + "</div><div class='title_ex'>Вемя аварии: " + $date_in + "</div></div>";
     var $h3 = "<h3><div class='panel_kit'>" + $head + "</div></h3>";

     var $block_0 = "<div class=\"text_bold title_rubric\">Заголовок:</div><div id=\"" + $id +  "_title\" class=\"cont_rubric\">" + $title + "</div>";
     var $block_1 = "<div class=\"text_bold title_rubric\">Место расположение узла:</div><div id=\"" + $id +  "_place\" class=\"cont_rubric\">" + $place + "</div>";
     var $block_2 = "<div class=\"text_bold title_rubric\">Затронутые сервисы:</div><div id=\"" + $id +  "_service\" class=\"cont_rubric\">" + $service + "</div>";
     var $block_3 = "<div class=\"text_bold title_rubric\">Легенда:</div><div id=\"" + $id +  "_legend\" class=\"cont_rubric\">" + $legend + "</div>";
     var $block_4 = "<div class=\"text_bold title_rubric\">Статус:</div><div id=\"" + $id +  "_status\" class=\"cont_rubric\">" + $status + "</div>";
     var $block_5 = "<div class=\"text_bold title_rubric\">Сроки устранения:</div><div id=\"" + $id +  "_timeout\" class=\"cont_rubric\">" + $timeout + "</div>";
     var $block_6 = "<div class=\"text_bold title_rubric\">Дата и время аварии:</div><div id=\"" + $id +  "_date_in\" class=\"cont_rubric\">" + $date_in + "</div>";
     var $block_7 = "<div class=\"text_bold title_rubric\">Дата и время устранения аварии:</div><div id=\"" + $id +  "_date_out\" class=\"cont_rubric\">" + $date_out + "</div>";
     var $block_8 = "<div class=\"text_bold title_rubric\">Комментарий:</div><div id=\"" + $id +  "_description\" class=\"cont_rubric\">" + $description + "</div>";

     var $content = "<div class=\"content\" id=\"" + $id + "\">" + $block_0 + $block_1 + $block_2 + $block_3 + $block_4 + $block_5 + $block_6 + $block_7 + $block_8 + "</div>";

     $("<div id=\"" + $id + "_block_item_admin_trouble_lists\">" + $h3 + $content + "</div>").appendTo("#admin_trouble_list");
     });

     $("#admin_trouble_list").accordion({clearStyle: true, collapsible: true, header: "h3", autoHeight: false, alwaysOpen: false, active: false, navigation: true*/
    /*, animated: false*/
    /*}).addClass('ui-accordion-news-moderaor');
     }
     });
     };*/

    /*    var $inter_val = 10000;
     var $inter;*/

    /*    function reloadTab() {
     if ($("#stop_start_reload").attr('checked')) {
     if ($("#speed_of_refresh").html().length >= 20) {
     $("#speed_of_refresh").html("");
     }
     $("#speed_of_refresh").html($("#speed_of_refresh").html() + "|");
     $.fn.get_troble_list();
     }
     }*/

    /*    $.fn.checkSelectedTab = function(index, interval, clear) {
     if (clear == 0) {
     window.clearInterval($inter);
     }
     if (index == 0) {
     $inter = window.setInterval(reloadTab, interval);
     } else {
     window.clearInterval($inter);
     }
     };*/

    /*    $("#set_interval_autoreload").click(function() {
     if (String(parseInt($("#interval_autoreload").val())) !== $("#interval_autoreload").val()) {
     alert("Значение интервала должно быть целым числом");
     } else if (($("#interval_autoreload").val() < 10000) || ($("#interval_autoreload").val() > 1000000)) {
     alert("Значение интервало должно принадлежать отрезку [10000,1000000]");
     } else {
     $.fn.checkSelectedTab(0, $("#interval_autoreload").val(), 0);
     }
     }); */

    /*$("#refresh_trouble_list").click(function() {
     $.fn.get_troble_list();
     });*/

    /*$("#stop_start_reload").click(function() {
     if ($("#stop_start_reload").attr('checked')) {
     $.fn.checkSelectedTab(0,$("#interval_autoreload").val(),0);
     } else {
     $.fn.checkSelectedTab(1,$inter_val,1);
     }
     });*/

//    $.fn.get_troble_list();
});