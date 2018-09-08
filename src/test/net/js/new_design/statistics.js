$(document).ready(function () {

    var host = "/controller";

    function ViewModel() {
        var self = this;

        self.user = ko.observable({
            id:NaN,
            login:"",
            block:false,
            fio:"",
            changePassword:false,
            menu:null
        });
        self.hostStatuses = {
            available: ko.observableArray(),
            selected: ko.observableArray()
        };
        self.regions = {
            available: ko.observableArray(),
            selected: ko.observableArray()
        };
        self.searchParam = {
            startSearchDate: ko.observable(),
            endSearchDate: ko.observable(),
            deviceName: ko.observable(),
            hostStatuses: ko.computed(function() {
                var available = self.hostStatuses.available();
                var selected = self.hostStatuses.selected();
                var hostStatuses = [];
                for (var i = 0; i < available.length; i++) {
                    for (var j = 0; j < selected.length; j++) {
                        if (available[i].id == selected[j]) hostStatuses.push(available[i])
                    }
                }
                return hostStatuses;
            }),
            regions: ko.computed(function() {
                var available = self.regions.available();
                var selected = self.regions.selected();
                var regions = [];
                for (var i = 0; i < available.length; i++) {
                    for (var j = 0; j < selected.length; j++) {
                        if (available[i].id == selected[j]) regions.push(available[i])
                    }
                }
                return regions;
            }, this)
        };
        self.searchParam.checkDate = ko.computed(function() {
            if (self.searchParam.startSearchDate() > self.searchParam.endSearchDate()) {
                console.log("Wrong date interval! Start date must be fewer end date!");
            }
        }, this);
        self.getReport = function() {
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "getTroubleReport",
                    param: ko.toJSON(self.searchParam)
                },
                beforeSend: function() {

                },
                success: function(data) {

                }
            });
        };
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

    $("#start-search-date").datepicker({
        format: "dd/mm/yyyy",
        weekStart: 1,
        startView: 0,
        autoclose: true,
        todayHighlight: true,
        keyboardNavigation: true
    }).on('changeDate', function(ev) {
        viewModel.searchParam.startSearchDate(ev.date.valueOf());
    });

    $("#end-search-date").datepicker({
        format: "dd/mm/yyyy",
        weekStart: 1,
        startView: 0,
        autoclose: true,
        todayHighlight: true,
        keyboardNavigation: true
    }).on('changeDate', function(ev){
        viewModel.searchParam.endSearchDate(ev.date.valueOf());
    });

    function getUser() {
        $.get(host, {cmd:"getUser"},
            function (data) {
                if (data) {
                    viewModel.user(data);
                }
            }, "json"
        );
    }
    getUser();

    (function () {
        $.get(host, {cmd:"getRegions"},
            function (data) {
                viewModel.regions.available(data);
            }, "json"
        );

        $.get(host, {cmd:"getHostStatuses"},
            function (data) {
                viewModel.hostStatuses.available(data);
            }, "json"
        );
    })();
});