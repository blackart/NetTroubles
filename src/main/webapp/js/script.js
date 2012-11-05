$(document).ready(function () {
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


    $("#show-device-list").click(function () {
        alert(111);
        $('#devicesList').modal('show');
    });

    $("#show-comments-list").click(function () {
        $('#commentsList').modal('show');
    });

    $("#hide-device-list").click(function () {
        $('#devicesList').modal('hide');
    });

    $("#hide-comments-list").click(function () {
        $('#commentsList').modal('hide');
    });

    var host = "http://localhost:8080/controller";

    function ViewModel() {
        this.activeDeviceList = ko.observable(false);
        this.activeCommentsList = ko.observable(false);

        this.needActualProblemTroublesCounter = ko.observable(0);
        this.waitingCloseTroublesCounter = ko.observable(0);
        this.currentTroublesCounter = ko.observable(0);
        this.closedTroublesCounter = ko.observable(0);
        this.trashedTroublesCounter = ko.observable(0);

        this.currentTroubles = ko.observableArray();
        this.waitingCloseTroubles = ko.observableArray();
        this.needCRMTroubles = ko.observableArray();
        this.services = ko.observableArray();
        this.currentTroubleEditing = ko.observable(
            {
                title: "",
                devcapsules: [],
                services: [],
                timeout: "",
                actualProblem: "",
                comments: []
            }
        );
        this.chosenTrouble = function () {
            viewModel.currentTroubleEditing(this);
        };
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

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