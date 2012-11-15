$(document).ready(function () {
    var host = "http://localhost:8080/controller";

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

        self.services = ko.observableArray();
        self.currentTroubleEditing = ko.observable({
            title: "",
            devcapsules: [],
            services: [],
            timeoutTime: "",
            actualProblem: "",
            comments: []
        }) ;
        self.currentTroubleTimeoutTime = ko.observable('');
        self.currentTroubleServices = ko.observableArray([]);
        self.chosenTrouble = function (trouble) {
            console.log(trouble);
            self.currentTroubleEditing(trouble);
            $('#date-out').datepicker({format:'dd/mm/yyyy'});
            if (trouble.services) {
                var services = [];
                $.each(trouble.services, function() {
                    services.push(this.id);
                });
                self.currentTroubleServices(services);
            }

            if (trouble.timeout) {
                var timeout = new Date();
                timeout.setTime(trouble.timeout);
                $('#date-out').datepicker('setDate', timeout);
                self.currentTroubleTimeoutTime(timeout.getHours() - 1 + ':' + timeout.getMinutes());
            } else {
                self.currentTroubleTimeoutTime('');
            }
        };
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

    $('#editingDialog')
        .modal({show:false})
        .on('hidden', function() {
            update_trouble_counters();
            update_troubles();
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

    function update_troubles() {
        $.get(host, {cmd:"getCurrentTroubleListGroup"},
            function (data) {
                viewModel.currentTroubles(data.current.troubles);
                viewModel.waitingCloseTroubles(data.wait.troubles);
                viewModel.needCRMTroubles(data.need.troubles);
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

    update_troubles();
    update_trouble_counters();
});