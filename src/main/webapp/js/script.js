$(document).ready(function() {
    var host = "http://localhost:8080/";
    var ViewModel = {
        needActualProblemTroublesCounter: ko.observable(0),
        waitingCloseTroublesCounter: ko.observable(0),
        currentTroublesCounter: ko.observable(0),
        closedTroublesCounter: ko.observable(0),
        trashedTroublesCounter: ko.observable(0)
    };

    ko.applyBindings(ViewModel);

    function update_trouble_counters() {
        $.ajax({
            url:host + "controller",
            type:"POST",
            data:{
                cmd:"getTroubleCounters"
            },
            success:function (data) {
                ViewModel.needActualProblemTroublesCounter($(data).find('current').text());
                ViewModel.waitingCloseTroublesCounter($(data).find('waiting_close').text());
                ViewModel.closedTroublesCounter($(data).find('close').text());
                ViewModel.trashedTroublesCounter($(data).find('trash').text());
                ViewModel.needActualProblemTroublesCounter($(data).find('need_actual_problem').text());
            }
        });
    }

    update_trouble_counters();
});