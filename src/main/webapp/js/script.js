$(document).ready(function () {
    var host = "/controller";

    function twoChar(param) {
        return param.toString().length == 1 ? "0" + param : param;
    }

    function getRightTimeFormat(timestamp) {
        var date = new Date();
        date.setTime(timestamp);

        return twoChar(date.getDate()) + "/" +
            twoChar(date.getMonth() + 1) + "/" +
            twoChar(date.getFullYear()) + " " +
            twoChar(date.getHours()) + ":" +
            twoChar(date.getMinutes()) + ":" +
            twoChar(date.getSeconds());
    }

    function getTimeToResolve(timestamp, now) {
        if (!timestamp) return -2;

        var diffInSec = ((parseInt(timestamp) - now) / 1000) - (3600);

        var days = parseInt(diffInSec / (3600 * 24));
        diffInSec = diffInSec - (days * 3600 * 24);
        var hours = parseInt(diffInSec / 3600);
        diffInSec = diffInSec - (hours * 3600);
        var minutes = parseInt(diffInSec / 60);
        diffInSec = diffInSec - (minutes * 60);
        var seconds = parseInt(diffInSec);

        if ((days <= 0) && (hours <= 0) && (minutes <= 0) && (seconds <=0)) return -1;

        return (days == 0 ? "" : days + "d ") + twoChar(hours) + ":" + twoChar(minutes) + ":" + twoChar(seconds) + "";
    }

    function ViewModel() {
        var self = this;
        self.needActualProblemTroublesCounter = ko.observable(0);
        self.waitingCloseTroublesCounter = ko.observable(0);
        self.currentTroublesCounter = ko.observable(0);
        self.closedTroublesCounter = ko.observable(0);
        self.trashedTroublesCounter = ko.observable(0);

        self.currentTroubles = ko.observableArray();
        self.waitingCloseTroubles = ko.observableArray();
        self.needCRMTroubles = ko.observableArray();

        self.timeNow = ko.observable(new Date().getTime());
        self.timeToResolve = ko.computed(function() {
            $.each(self.currentTroubles(), function() {
                this.timeToResolve(getTimeToResolve(this.timeout, self.timeNow()));
            });
        });
        self.services = ko.observableArray();
        self.troubleForEditing = ko.observable({
            id: ko.observable(),
            title: ko.observable(''),
            actualProblem: ko.observable(''),
            devcapsules: ko.observableArray([]),
            comments: ko.observableArray([]),
            services: ko.observableArray([]),
            timeout: ko.observable(''),
            time: ko.observable(''),
            date: ko.observable('')
        });
        self.mergeTimeout = ko.computed(function() {
            var time = $.trim(self.troubleForEditing().time());
            var date = $.trim(self.troubleForEditing().date());
            self.troubleForEditing().timeout($.trim(date + " " + time));
        });
        self.calcTroubleForEditing = function() {
            var trouble = this;
            var troubleForEditing = self.troubleForEditing();

            troubleForEditing.id(trouble.id);
            troubleForEditing.title(trouble.title);
            troubleForEditing.actualProblem(trouble.actualProblem);

            troubleForEditing.timeout(trouble.timeout ? getRightTimeFormat(trouble.timeout) : "");
            var timeout = troubleForEditing.timeout();
            if (timeout) {
                var timeout_parts = timeout.split(" ");
                self.troubleForEditing().date(timeout_parts[0]);
                self.troubleForEditing().time(timeout_parts[1]);
            }

            troubleForEditing.date_in = getRightTimeFormat(trouble.date_in);
            troubleForEditing.date_out = trouble.date_out ? getRightTimeFormat(trouble.date_out) : "";
            troubleForEditing.author = trouble.author;

            troubleForEditing.devcapsules([]);
            if (trouble.devcapsules) {
                $.each(trouble.devcapsules, function() {
                    var devcapsule = {};
                    devcapsule.device = {};
                    devcapsule.timeup = this.timeup ? getRightTimeFormat(this.timeup) : "";
                    devcapsule.timedown = this.timedown ? getRightTimeFormat(this.timedown) : "";
                    devcapsule.complete = this.complete;
                    devcapsule.device.name = this.device.name;
                    devcapsule.device.description = this.device.description;
                    troubleForEditing.devcapsules.push(devcapsule);
                });
            }

            troubleForEditing.services([]);
            if (trouble.services) {
                $.each(trouble.services, function () {
                    troubleForEditing.services.push(this.id);
                });
            }

            troubleForEditing.close = trouble.close;
            troubleForEditing.crm = trouble.crm;

            troubleForEditing.comments([]);
            if (trouble.comments) {
                $.each(trouble.comments, function() {
                    var comment = {};
                    comment.crm = this.crm;
                    comment.text = this.text;
                    comment.time = getRightTimeFormat(this.time);
                    comment.author = this.author;
                    troubleForEditing.comments.push(comment);
                });
            }

            $("#date-timeout").datepicker();

            $("#time-timeout").timepicker({
                showMeridian: false,
                minuteStep: 10,
                disableFocus: true,
                showSeconds: true,
                defaultTime: 'value'
            });
        };
        self.saveEditionTrouble = function() {
            var trouble = this;
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "editTroubleOfCurrentTroubleListNew",
                    "trouble": ko.toJSON(trouble)
                },
                beforeSend: function() {
                    trouble.timeout($.trim(trouble.timeout()));
                    var expr = /(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/][2]\d{3}[ ]([0-1][0-9]|2[0-4])[:][0-5][0-9][:][0-5][0-9]/;
                    if (trouble.timeout() == "") {
                        return true;
                    } else if (!expr.exec(trouble.timeout())) {
                        return false;
                    }
                    return true;
                },
                success: function(data) {
                    getJSONData();
                }
            });
        };
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

    var updateResolveTime = setInterval(function() {
        viewModel.timeNow(new Date().getTime());
    }, 1000);

    $('#editingDialog')
        .modal({show:false})
        .on('hidden', function() {
            update_trouble_counters();
        });

    function getJSONData() {
        $.get(host, {cmd:"getCurrentTroubleListGroup"},
            function (data) {
                $.each(data.current.troubles, function() {
                    if (!this.timeout)this.timeout = "";
                    this.timeToResolve = ko.observable();
                });
                viewModel.currentTroubles(data.current.troubles);
                viewModel.waitingCloseTroubles(data.wait.troubles);
                viewModel.needCRMTroubles(data.need.troubles);
            }, "json"
        );
    }

    var refreshData = setInterval(function() {
        getJSONData()
    }, 30000);

    $("#refesh-page").click(function() {
        getJSONData();
    });

    function update_trouble_counters() {
        $.get(host, {cmd:"getTroubleCounters"},
            function (data) {
                viewModel.currentTroublesCounter(data.current);
                viewModel.waitingCloseTroublesCounter(data.waiting_close);
                viewModel.closedTroublesCounter(data.close);
                viewModel.trashedTroublesCounter(data.trash);
                viewModel.needActualProblemTroublesCounter(data.need_actual_problem);
            }, "json"
        );
    }

    (function() {
        $.get(host, {cmd:"getServices"},
            function (data) {
                viewModel.services(data);
            }, "json"
        );
    })();

    getJSONData();
    update_trouble_counters();
});