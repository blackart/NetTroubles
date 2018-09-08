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
        self.troubles = ko.observableArray();
        self.troubleForEditing = {
            id:ko.observable(),
            title:ko.observable(''),
            actualProblem:ko.observable(''),
            devcapsules:ko.observableArray([]),
            comments:ko.observableArray([]),
            services:ko.observableArray([]),
            timeout:ko.observable(''),
            time:ko.observable(''),
            date:ko.observable(''),
            close:ko.observable(false),
            crm:ko.observable(false)
        };
        self.troubleForEditing.timeoutObj = ko.computed(function() {
            return convertFormattedStringToDate(this.troubleForEditing.timeout());
        }, this);
        self.mergeTimeout = ko.computed(function() {
            var time = $.trim(self.troubleForEditing.time());
            var date = $.trim(self.troubleForEditing.date());
            self.troubleForEditing.timeout($.trim(date + " " + time));
        });
        self.services = ko.observableArray();
        self.calcTroubleForEditing = function () {
            var trouble = this;
            var troubleForEditing = self.troubleForEditing;

            troubleForEditing.id(trouble.id);
            troubleForEditing.title(trouble.title);
            troubleForEditing.actualProblem(trouble.actualProblem);

            troubleForEditing.timeout(trouble.timeout ? getRightTimeFormat(trouble.timeout) : "");
            var timeout = troubleForEditing.timeout();
            if (timeout) {
                var timeout_parts = timeout.split(" ");
                troubleForEditing.date(timeout_parts[0]);
                troubleForEditing.time(timeout_parts[1]);
            } else {
                troubleForEditing.date("");
                troubleForEditing.time("");
            }

            troubleForEditing.date_in = getRightTimeFormat(trouble.date_in);
            troubleForEditing.date_out = trouble.date_out ? getRightTimeFormat(trouble.date_out) : "";
            troubleForEditing.author = trouble.author;

            if (trouble.devcapsules) {
                $.each(trouble.devcapsules, function () {
                    this.timeup = this.timeup ? getRightTimeFormat(this.timeup) : "";
                    this.timedown = this.timedown ? getRightTimeFormat(this.timedown) : "";
                });
            }
            troubleForEditing.devcapsules(trouble.devcapsules ? trouble.devcapsules : []);

            troubleForEditing.services([]);
            if (trouble.services) {
                $.each(trouble.services, function () {
                    troubleForEditing.services.push(this.id);
                });
            }

            troubleForEditing.close(trouble.close);
            troubleForEditing.crm(trouble.crm);


            if (trouble.comments) {
                trouble.comments.sort(sDecrease);
                $.each(trouble.comments, function () {
                    this.timeFormated = getRightTimeFormat(this.time);
                });
            }
            troubleForEditing.comments(trouble.comments ? trouble.comments : []);
        };
        self.restoreTrouble = function () {
            var trouble = this;
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "recoveryTrouble",
                    id: this.id
                },
                beforeSend: function() {
                    return true;
                },
                success: function(data) {
                    getJSONData();
                }
            });
        };
        self.destroyTrouble = function() {
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "destroyTrouble",
                    id: this.id
                },
                beforeSend: function() {
                    return true;
                },
                success: function(data) {
                    getJSONData();
                }
            });
        }
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

    $("#destroy-all").click(function() {
        $.post(host, {"cmd": "clearTrashList"});
        getJSONData();
    });

    function getJSONData() {
        $.get(host, {cmd:"getTrashedTroubleList"},
            function (data) {
                $.each(data.troubles, function () {
                    if (!this.timeout)this.timeout = "";
                    this.timeToResolve = ko.observable();
                });
                viewModel.troubles(data.troubles);
            }, "json"
        );
    }

    function getUser() {
        $.get(host, {cmd:"getUser"},
            function (data) {
                if (data) {
                    viewModel.user(data);
                }
            }, "json"
        );
    }

    (function () {
        $.get(host, {cmd:"getServices"},
            function (data) {
                viewModel.services(data);
            }, "json"
        );
    })();

    getJSONData();
    getUser();
});