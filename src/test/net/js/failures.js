$(document).ready(function() {
    $("#page_for_print").click(function() {
        var $win = window.open("", "myWindow");
        var $table = $($("#failures_table").html());
        $($table).find(".delete_devc").detach();

        $win.document.write("<style type='text/css'>div {margin: 5px 0 5px 0; font-size: 7.5pt;} table {font-size: 7.5pt; border-top: 1px solid #000; width: 100%;border-left: 1px solid #000; width: 100%; } table td {border-right: 1px solid #000;border-bottom: 1px solid #000;}</style>");
        $win.document.write("<div><div>" + $(".title_failures").html() + "</div><table cellpadding='0' cellspacing='0'>" + $($table).html() + "</table><div>" + $(".statistics").html() + "</div></div>");

    });

    function select_time_interval() {
        $("#select_date").bind("change", (function() {
            $(".date").html("");
            var $naumberOfmonth = 1;
            switch ($(this).val()) {
                case '1':
                    $(".date").append("Дата: <input type='text' id='minDate'/>");
                    break;
                case '2':
                    $(".date").append("Дата: <input type='text' id='minDate'/>");
                    break;
                case '3' :
                    $(".date").append("Дата: <input type='text' id='minDate'/>");
                    break;
                case '4':
                    $(".date").append("c: <input type='text' id='minDate'/> до <input type='text' id='maxDate'/>");
                    $naumberOfmonth = 3;
                    break;
            }

            var dates = $(".date input").datepicker({
                defaultDate: "+0",
                numberOfMonths: $naumberOfmonth,
                changeMonth: true,
                changeYear: true,
                closeText: 'X',
                showButtonPanel: true,
                dayNames: ['Воскресение', 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница', 'Суббота'],
                dayNamesShort: ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
                dayNamesMin: ['Вс','Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
                firstDay: 1,
                hideIfNoPrevNext: true,
                monthNames: ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'],
                monthNamesShort: ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'],
                showOtherMonths: true,
                altFormat: 'dd/mm/yy',
                dateFormat: 'dd/mm/yy',
                appendText: ''/*,
                 onSelect: function(selectedDate) {
                 var option = this.id == "from" ? "minDate" : "maxDate", instance = $(this).data("datepicker"), date = $.datepicker.parseDate(instance.settings.dateFormat || $.datepicker._defaults.dateFormat, selectedDate, instance.settings);
                 dates.not(this).datepicker("option", option, date);
                 }*/
                /*onClose: function(dateText, inst) {
                 alert($(this).attr('id'));
                 },*/
                /*onChangeMonthYear: function(year, month, inst) {
                 var date = new Date();
                 $(this).datepicker("setDate", 30 + "/" + month + "/" + year);
                 }*/
            });
            $(".date input").datepicker("setDate", "+0");
        }));
    }

    function delete_devc() {
        var del_row_class = $(this).attr("class");

        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "delDevc",
                id: $(this).attr("class").replace("_trouble_id","")
            },
            beforeSend: function() {
                return true;
            },
            success: function(data) {
                $.each($("." + del_row_class),function() {
                    $(this).parent().parent().detach();
                });
            }
        });
    }

    function find_failures() {
        $("#find_failures").click(function() {
            switch ($("#type_find_select").val()) {
                case '1':
                    var $name = $.trim($("#device_name").val());
                    var $find_entry = 0;
                    var $minDate = null;
                    var $maxDate = null;

                    if ($("#check_time_interval").attr("checked")) {
                        $find_entry = $("#select_date").val();
                        $minDate = $("#minDate").val();
                        if ($find_entry == 4) {
                            $maxDate = $("#maxDate").val();
                        }
                    }

                    $.ajax({
                        url : "/controller",
                        type : "POST",
                        data : {
                            cmd: "getFailuresListForName",
                            dateUse: $("#check_time_interval").attr("checked") ? 1 : 0,
                            name: $name,
                            find_entry: $find_entry,
                            dateMin: $minDate,
                            dateMax: $maxDate
                        },
                        beforeSend: function() {
                            if ($name == "") {
                                alert("Введите имя устройства!");
                                return false;
                            } else {
                                if ($("#check_time_interval").attr("checked")) {
                                    if ($find_entry == 4) {
                                        if (($minDate == '') || ($maxDate == '')) {
                                            alert("Введите дату поиска!");
                                            return false;
                                        }
                                    } else if ($find_entry == 0) {
                                        alert("Введите временной период!");
                                        return false;
                                    } else {
                                        if ($minDate == '') {
                                            alert("Введите дату поиска!");
                                            return false;
                                        }
                                    }
                                }
                            }
                            return true;
                        },
                        success: function(data) {
                            $('#failures').html("");
                            $('#failures').append("<div class='title_failures'>" + $(data).find("failures").find('title').text() + "</div>");
                            $('#failures').append("<table id='failures_table' border='1px' cellpadding='5px' cellspacing='0' width='100%'></table>");
                            $('#failures_table').append("<tr><td>№</td><td>Время падения</td><td>Время простоя</td><td>Узел</td><td>Статус</td><td>Описание узла</td><td>Регион</td><td>Причина аварии</td><td>ФИО инженера</td><td class='delete_devc'>Действие</td></tr>");
                            var count = 0;
                            $(data).find("failures").find("failures_entry").each(function() {
                                count++;
                                $('#failures_table').append("<tr><td>" + count + "</td><td>" + $(this).find('timedown').text() + "</td><td>" + $(this).find('interval').text() + "</td><td>" + $(this).find('devicename').text() + "</td><td>" + $(this).find('devicehoststatus').text() + "</td><td>" + $(this).find('devicedesc').text() + "</td><td>" + $(this).find('region').text() + "</td><td>" + $(this).find('troubledesc').text() + "</td><td>" + $(this).find('author').text() + "</td><td class='delete_devc'><input type='button' value='удалить' class='" + $(this).find('trouble_id').text() + "_trouble_id'/></td></tr>");
                            });
                            if (count == 0) {
                                $('#failures').append("<br><div style='text-align: center;'>По вашему запросу ничего не найдено.</div>");
                            } else {
                                $('#failures').append("<div class='statistics'><div>Общее время простоя: " + $(data).find("failures").find('all_down_interval').text() + "</div><div>Количесво аварий: " + $(data).find("failures").find('count_failures').text() + "</div><div>Среднее время простоя: " + $(data).find("failures").find('for_every_down_interval').text() + "</div></div>");
                            }

                            $("#failures_table .delete_devc input").bind("click", delete_devc);
                        }
                    });

                    break;
                case '2':
                    $find_entry = $("#select_date").val();
                    $minDate = $("#minDate").val();
                    $maxDate = null;
                    var $regions = "";

                    if ($find_entry == 4) {
                        $maxDate = $("#maxDate").val();
                    }

                    if ($("#check_region").attr("checked")) {
                        $.each($("#region_dev").find(":checkbox"), function() {
                            if ($(this).attr("checked")) {
                                $regions += $(this).val() + ";";
                            }
                        });
                    }

                    $.ajax({
                        url : "/controller",
                        type : "POST",
                        data : {
                            cmd: "getFailuresListForDate",
                            regionUse: $("#check_region").attr("checked") ? 1 : 0,
                            regions: $regions,
                            find_entry: $find_entry,
                            dateMin: $minDate,
                            dateMax: $maxDate
                        },
                        beforeSend: function() {
                            if ($find_entry == 4) {
                                if (($.trim($minDate) == '') || ($.trim($maxDate) == '')) {
                                    alert("Введите дату поиска!");
                                    return false;
                                }
                            } else if ($find_entry == 0) {
                                alert("Введите временной период!");
                                return false;
                            } else {
                                if ($.trim($minDate) == '') {
                                    alert("Введите дату поиска!");
                                    return false;
                                }
                            }
                            return true;
                        },
                        success: function(data) {
                            $('#failures').html("");
                            $('#failures').append("<div class='title_failures'>" + $(data).find("failures").find('title').text() + "</div>");
                            $('#failures').append("<table id='failures_table' border='1px' cellpadding='5px' cellspacing='0' width='100%'></table>");
                            $('#failures_table').append("<tr><td>№</td><td>Время падения</td><td>Время простоя</td><td>Узел</td><td>Статус</td><td>Описание узла</td><td>Регион</td><td>Причина аварии</td><td>ФИО инженера</td><td class='delete_devc'>Действие</td></tr>");
                            var count = 0;
                            $(data).find("failures").find("failures_entry").each(function() {
                                count++;
                                $('#failures_table').append("<tr><td>" + count + "</td><td>" + $(this).find('timedown').text() + "</td><td>" + $(this).find('interval').text() + "</td><td>" + $(this).find('devicename').text() + "</td><td>" + $(this).find('devicehoststatus').text() + "</td><td>" + $(this).find('devicedesc').text() + "</td><td>" + $(this).find('region').text() + "</td><td>" + $(this).find('troubledesc').text() + "</td><td>" + $(this).find('author').text() + "</td><td class='delete_devc'><input type='button' value='удалить' class='" + $(this).find('trouble_id').text() + "_trouble_id'/></td></tr>");
                            });
                            if (count == 0) {
                                $('#failures').append("<br><div style='text-align: center;'>По вашему запросу ничего не найдено.</div>");
                            } else {
                                $('#failures').append("<div class='statistics'><div>Общее время простоя: " + $(data).find("failures").find('all_down_interval').text() + "</div><div>Количесво аварий: " + $(data).find("failures").find('count_failures').text() + "</div><div>Среднее время простоя: " + $(data).find("failures").find('for_every_down_interval').text() + "</div></div>");
                            }

                            $("#failures_table .delete_devc input").bind("click", delete_devc);
                        }
                    });

                    break;
                case '3':
                    var $status = "";
                    var $regions = "";
                    $find_entry = 0;
                    $minDate = null;
                    $maxDate = null;

                    $.each($("#status_dev").find(":checkbox"), function() {
                        if ($(this).attr("checked")) {
                            $status += $(this).val() + ";";
                        }
                    });

                    if ($("#check_time_interval").attr("checked")) {
                        $find_entry = $("#select_date").val();
                        $minDate = $("#minDate").val();
                        if ($find_entry == 4) {
                            $maxDate = $("#maxDate").val();
                        }
                    }

                    if ($("#check_region").attr("checked")) {
                        $.each($("#region_dev").find(":checkbox"), function() {
                            if ($(this).attr("checked")) {
                                $regions += $(this).val() + ";";
                            }
                        });
                    }

                    $.ajax({
                        url : "/controller",
                        type : "POST",
                        data : {
                            cmd: "getFailuresListForStatus",
                            dateUse: $("#check_time_interval").attr("checked") ? 1 : 0,
                            regionUse: $("#check_region").attr("checked") ? 1 : 0,
                            regions: $regions,
                            status: $status,
                            find_entry: $find_entry,
                            dateMin: $minDate,
                            dateMax: $maxDate
                        },
                        beforeSend: function() {
                            if ($status == "") {
                                alert("Выберите статус устройства!");
                                return false;
                            }
                            if ($("#check_time_interval").attr("checked")) {
                                if ($find_entry == 4) {
                                    if (($minDate == '') || ($maxDate == '')) {
                                        alert("Введите дату поиска!");
                                        return false;
                                    }
                                } else if ($find_entry == 0) {
                                    alert("Введите временной период!");
                                    return false;
                                } else {
                                    if ($minDate == '') {
                                        alert("Введите дату поиска!");
                                        return false;
                                    }
                                }
                            }
                            if ($("#check_region").attr("checked")) {
                                if ($regions === "") {
                                    alert("Выберите регион!");
                                    return false;
                                }
                            }
                            return true;
                        },
                        success: function(data) {
                            $('#failures').html("");
                            $('#failures').append("<div class='title_failures'>" + $(data).find("failures").find('title').text() + "</div>");
                            $('#failures').append("<table id='failures_table' border='1px' cellpadding='5px' cellspacing='0' width='100%'></table>");
                            $('#failures_table').append("<tr><td>№</td><td>Время падения</td><td>Время простоя</td><td>Узел</td><td>Статус</td><td>Описание узла</td><td>Регион</td><td>Причина аварии</td><td>ФИО инженера</td><td class='delete_devc'>Действие</td></tr>");
                            var count = 0;
                            $(data).find("failures").find("failures_entry").each(function() {
                                count++;
                                $('#failures_table').append("<tr><td>" + count + "</td><td>" + $(this).find('timedown').text() + "</td><td>" + $(this).find('interval').text() + "</td><td>" + $(this).find('devicename').text() + "</td><td>" + $(this).find('devicehoststatus').text() + "</td><td>" + $(this).find('devicedesc').text() + "</td><td>" + $(this).find('region').text() + "</td><td>" + $(this).find('troubledesc').text() + "</td><td>" + $(this).find('author').text() + "</td><td class='delete_devc'><input type='button' value='удалить' class='" + $(this).find('trouble_id').text() + "_trouble_id'/></td></tr>");
                            });
                            if (count == 0) {
                                $('#failures').append("<br><div style='text-align: center;'>По вашему запросу ничего не найдено.</div>");
                            } else {
                                $('#failures').append("<div class='statistics'><div>Общее время простоя: " + $(data).find("failures").find('all_down_interval').text() + "</div><div>Количесво аварий: " + $(data).find("failures").find('count_failures').text() + "</div><div>Среднее время простоя: " + $(data).find("failures").find('for_every_down_interval').text() + "</div></div>");
                            }

                            $("#failures_table .delete_devc input").bind("click", delete_devc);
                        }
                    });

                    break;
            }
        });
    }

    $("#type_find_select").bind("change", (function() {
        $(".other_panel").html("");
        switch ($(this).val()) {
            case '1':
                $(".other_panel").append("<div class='name_find_panel'>Имя устройства: <input id='device_name' type='text'/></div>");
                $(".name_find_panel").append("<div class='time_interval_quest'><input id='check_time_interval' type='checkbox'/> учитывать веременной промежуток?</div>");
                $(".name_find_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");

                var $height_block = $(".button_panel").css("height");
                $("#check_time_interval").click(function() {
                    if ($("#check_time_interval").attr("checked")) {
                        $(".other_panel").append("<div class='time_interval'></div>");
                        $(".time_interval").append("<div class='date_find'>Временной промежуток: <select id='select_date'><option value='0'></option><option value='1'>за день</option><option value='2'>за месяц</option><option value='3'>за год</option><option value='4'>за период</option></select></div><div class='date'></div>");
                        $(".submit_find_button").detach();
                        $(".time_interval").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");

                        $(".time_interval").css("height", $height_block);
                        $(".name_find_panel").css("height", $height_block);
                        select_time_interval();
                        find_failures()
                    } else {
                        $(".time_interval").detach();
                        $(".submit_find_button").detach();
                        $(".name_find_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");
                        find_failures()
                    }
                });

                $(".name_find_panel").css("height", $height_block);
                break;
            case '2':
                $(".other_panel").append("<div class='date_find_panel'></div>");
                $(".date_find_panel").append("<div class='date_find'>Временной промежуток: <select id='select_date'><option value='0'></option><option value='1'>за день</option><option value='2'>за месяц</option><option value='3'>за год</option><option value='4'>за период</option></select></div>");
                $(".date_find_panel").append("<div class='date'></div>");
                $(".date_find_panel").append("<div class='region_quest'><input id='check_region' type='checkbox'/> учитывать регион?</div>");
                $(".date_find_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");

                select_time_interval();

                var $height_block = $(".button_panel").css("height");

                $("#check_region").click(function() {
                    if ($("#check_region").attr("checked")) {
                        $(".other_panel").append("<div class='region_panel'></div>");
                        $(".region_panel").append("<div class='region_find'>Регион: <div id='region_dev'></div></div>");

                        $.each($("#host_region_replace option"), function() {
                            $("#region_dev").append("<div><input type='checkbox' value='" + $(this).val() + "'/> " + $(this).text() + "</div>");
                        });

                        $(".submit_find_button").detach();
                        $(".region_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");

                        $(".region_panel").css("height", $height_block);
                        $(".date_find_panel").css("height", $height_block);
                        find_failures();
                    } else {
                        $(".region_panel").detach();
                        $(".submit_find_button").detach();
                        $(".date_find_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");
                        find_failures();
                    }
                });
                $(".name_find_panel").css("height", $height_block);
                break;
            case '3':
                $(".other_panel").append("<div class='status_find_panel'>Статус устройства: <div id='status_dev'></div></div>");
                $(".status_find_panel").append("<div class='time_interval_quest'><input id='check_time_interval' type='checkbox'/> учитывать веременной промежуток?</div>");
                $(".status_find_panel").append("<div class='region_quest'><input id='check_region' type='checkbox'/> учитывать регион?</div>");
                $(".status_find_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");

                $.each($("#host_status_replace option"), function() {
                    $("#status_dev").append("<div><input type='checkbox' value='" + $(this).val() + "'/> " + $(this).text() + "</div>");
                });

                var $height_block = $(".button_panel").css("height");

                $("#check_region").click(function() {
                    if ($("#check_region").attr("checked")) {
                        $(".other_panel").append("<div class='region_panel'></div>");
                        $(".region_panel").append("<div class='region_find'>Регион: <div id='region_dev'></div></div>");

                        $.each($("#host_region_replace option"), function() {
                            $("#region_dev").append("<div><input type='checkbox' value='" + $(this).val() + "'/> " + $(this).text() + "</div>");
                        });

                        $(".submit_find_button").detach();
                        $(".region_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");

                        $(".region_panel").css("height", $height_block);
                        $(".status_find_panel").css("height", $height_block);
                        find_failures();
                    } else {
                        $(".region_panel").detach();
                        $(".submit_find_button").detach();
                        $(".status_find_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");
                        find_failures();
                    }
                });

                $("#check_time_interval").click(function() {
                    if ($("#check_time_interval").attr("checked")) {
                        $(".other_panel").append("<div class='time_interval'></div>");
                        $(".time_interval").append("<div class='date_find' id='for_status'>Временной промежуток: <br><select id='select_date'><option value='0'></option><option value='1'>за день</option><option value='2'>за месяц</option><option value='3'>за год</option><option value='4'>за период</option></select></div><div class='date'></div>");
                        $(".submit_find_button").detach();
                        $(".time_interval").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");

                        $(".time_interval").css("height", $height_block);
                        $(".status_find_panel").css("height", $height_block);
                        select_time_interval();
                        find_failures()
                    } else {
                        $(".time_interval").detach();
                        $(".submit_find_button").detach();
                        $(".status_find_panel").append("<div class='submit_find_button'><input type='button' value='поиск' id='find_failures'/></div>");
                        find_failures()
                    }
                });
                $(".status_find_panel").css("height", $height_block);
                break;
        }
        find_failures();

    }));

    $(".find_panel").css("display", ($("#openControlPanel_failures_list").val() == "true" ? "block" : "none"));

    $("#failures_page_control_panel").click(function() {
        $.ajax({
            url : "/controller",
            type : "POST",
            data : {
                cmd: "openControlPanel",
                value: $(".find_panel").css("display") == "none"
            }
        });

        if ($(".find_panel").css("display") == "none") {
            $(".find_panel").show("blind", { direction: "vertical" }, 300);
        } else {
            $(".find_panel").hide("blind", { direction: "vertical" }, 300);
        }
    });
});