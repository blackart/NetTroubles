$(document).ready(function () {
    var host = "http://localhost:8080/controller";

    function ViewModel() {
        var self = this;
        self.activeDeviceList = ko.observable(false);
        self.showDeviceList = function() {
            self.activeDeviceList() ? $('#devicesList').modal('hide') : $('#devicesList').modal('show');
        };
        self.activeCommentsList = ko.observable(false);
        self.showCommentsList = function() {
            self.activeCommentsList() ? $('#commentsList').modal('hide') : $('#commentsList').modal('show');
        };

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
            timeout: "",
            actualProblem: "",
            comments: []
        });
        self.chosenTrouble = function (trouble) {
            self.currentTroubleEditing(trouble);
        };
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

    $('#editingDialog').modal({
        show: false
    }).on('hide', function () {
        $('#devicesList').modal('hide');
        $('#commentsList').modal('hide');
    });

    $('#commentsList').modal({
        show: false,
        backdrop: false
    }).on('show',
        function() {
            $('#commentsList .modal-body').css({"height":$('#edition-dialog-body').css("height")});
            viewModel.activeCommentsList(true);
        }
    ).on('hide',
        function() {
            viewModel.activeCommentsList(false);
        }
    );

    $('#devicesList').modal({
        show:false,
        backdrop: false
    }).on('show',
        function() {
            $('#devicesList .modal-body').css({"height":$('#edition-dialog-body').css("height")});
            viewModel.activeDeviceList(true);
        }
    ).on('hide',
        function() {
            viewModel.activeDeviceList(false);
        }
    );

    $('#timeout').datepicker({
        format:'dd/mm/yyyy'
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