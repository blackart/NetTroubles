$(document).ready(function () {
    var host = "/controller";

    function ViewModel() {
        var self = this;
        self.troubles = ko.observableArray();
        self.searchCategory = ko.observableArray([
            {id: 1, text: 'Day'},
            {id: 2, text: 'Month'},
            {id: 3, text: 'Year'}
        ]);
        self.selectedSearchCategory = ko.observable(1);
        self.searchDate = ko.observable("");
        self.search = function() {
            var date = self.searchDate();
            var category = self.selectedSearchCategory();
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "getClosedTroubleList",
                    category: category,
                    date: date
                },
                beforeSend: function() {
                    if (!date) return false;
                    return true;
                },
                success: function(data) {
                    $.each(data, function() {
                        if (!this.timeout)this.timeout = "";
                        this.timeToResolve = ko.observable();
                        this.checked = ko.observable(false);
                    });
                    self.troubles(data);
                }
            });
        };

        self.user = ko.observable({
            id: NaN,
            login: "",
            block: false,
            fio: "",
            changePassword: false,
            menu: null
        });

        self.modalDialog = {
            status: ko.observable(''),
            caption: ko.observable(''),
            errorAlert: {
                show: ko.observable(false),
                message: ko.observable("")
            }
        };
        self.modalDialog.errorAlert.showAlertTab = ko.computed(function() {
            if (self.modalDialog.errorAlert.show()) $("#edition-dialog-body a:first").tab('show');
        });

        self.services = ko.observableArray();
        self.troubleForEditing = {
            id: ko.observable(),
            title: ko.observable(''),
            actualProblem: ko.observable(''),
            devcapsules: ko.observableArray([]),
            comments: ko.observableArray([]),
            services: ko.observableArray([]),
            timeout: ko.observable(''),
            time: ko.observable(''),
            date: ko.observable(''),
            close: ko.observable(false),
            crm: ko.observable(false)
        };
        self.troubleForEditing.timeoutObj = ko.computed(function() {
            return convertFormattedStringToDate(this.troubleForEditing.timeout());
        }, this);
        self.mergeTimeout = ko.computed(function() {
            var time = $.trim(self.troubleForEditing.time());
            var date = $.trim(self.troubleForEditing.date());
            self.troubleForEditing.timeout($.trim(date + " " + time));
        });
        self.calcTroubleForEditing = function() {
            var trouble = this;
            var troubleForEditing = self.troubleForEditing;

            self.modalDialog.errorAlert.show(false);
            self.modalDialog.caption("Edit information about the trouble");
            self.modalDialog.status('edit');

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
                $.each(trouble.devcapsules, function() {
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
                $.each(trouble.comments, function() {
                    this.timeFormatted = getRightTimeFormat(this.time);
                });
            }
            troubleForEditing.comments(trouble.comments ? trouble.comments : []);
        };
        self.saveTrouble = function() {
            var trouble = this;
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "editTrouble",
                    trouble: ko.toJSON(trouble)
                },
                beforeSend: function() {
                    var errorAlert = self.modalDialog.errorAlert;
                    trouble.timeout($.trim(trouble.timeout()));
                    var expr = /(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/][2]\d{3}[ ]([0-1][0-9]|2[0-4])[:][0-5][0-9][:][0-5][0-9]/;
                    if (!$.trim(trouble.title())) {
                        errorAlert.show(true);
                        errorAlert.message("Please, enter the title of trouble.");
                        return false;
                    } else if (trouble.timeout() && !expr.exec(trouble.timeout())) {
                        errorAlert.show(true);
                        errorAlert.message("Please, enter the time of resolving problem in correct format.");
                        return false;
                    }
                    errorAlert.show(false);
                    return true;
                },
                success: function(data) {
                    self.search();
                    $('#editingDialog').modal('hide');
                }
            });
        };
        self.deleteTrouble = function() {
            var trouble = this;
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "deleteTroubleNew",
                    trouble: ko.toJSON(trouble)
                },
                beforeSend: function() {
                    return true;
                },
                success: function(data) {
                    self.search();
                    $('#editingDialog').modal('hide');
                }
            });
        };
        self.sendComment = function() {
            var trouble = this;
            var comment = $("#text-comment").val();
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "addCommentNew",
                    id: this.id(),
                    text: comment
                },
                beforeSend: function() {
                    if (!$.trim(comment)) {
                        $("#control-group-add-crm-comment").addClass("error");
                        $("#text-comment").popover('show');
                        return false;
                    }
                    return true;
                },
                success: function(data) {
                    $("#text-comment").val("");
                    data.timeFormatted = getRightTimeFormat(data.time);
                    viewModel.troubleForEditing.comments.push(data);
                    viewModel.troubleForEditing.comments.sort(sDecrease);
                    getJSONData();
                }
            });
        };
        self.calcMergeTrouble = function() {
            var devcapsules = [];
            var comments = [];
            var close = true;
            var ids = [];

            $.each(self.troubles(), function() {
                if (this.checked()) {
                    comments = comments.concat(this.comments);
                    devcapsules = devcapsules.concat(this.devcapsules);
                    close = close && this.close;
                    ids.push(this.id);
                }
            });

            if (ids.length > 0) {
                $.each(comments, function() {
                    this.timeFormatted = getRightTimeFormat(this.time);
                });

                $.each(devcapsules, function() {
                    this.timeup = this.timeup ? getRightTimeFormat(this.timeup) : "";
                    this.timedown = this.timedown ? getRightTimeFormat(this.timedown) : "";
                });

                var trouble = self.troubleForEditing;

                trouble.comments(comments.sort(sDecrease));
                trouble.close(close);
                trouble.devcapsules(devcapsules);
                trouble.ids = ids;

                trouble.id();
                trouble.title('');
                trouble.actualProblem('');
                trouble.services([]);
                trouble.timeout('');
                trouble.time('');
                trouble.date('');
                trouble.crm(false);

                self.modalDialog.errorAlert.show(false);
                self.modalDialog.caption("Merge the selected troubles");
                self.modalDialog.status('merge');

                $('#editingDialog').modal("show");
            }
        };
        self.mergeTroubles = function() {
            var trouble = this;
            var errorAlert = self.modalDialog.errorAlert;
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "mergeTroublesNew",
                    ids: ko.toJSON(trouble.ids),
                    trouble: ko.toJSON(trouble)
                },
                beforeSend: function() {
                    trouble.timeout($.trim(trouble.timeout()));
                    var expr = /(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/][2]\d{3}[ ]([0-1][0-9]|2[0-4])[:][0-5][0-9][:][0-5][0-9]/;
                    if (!$.trim(trouble.title())) {
                        errorAlert.show(true);
                        errorAlert.message("Please, enter the title of trouble.");
                        return false;
                    }
                    return true;
                },
                success: function(data) {
                    if (!data.status) {
                        errorAlert.message(data.message);
                        errorAlert.show(true);
                    } else {
                        $('#editingDialog').modal('hide');
                    }
                    self.search();
                }
            });
        };
        self.checkAll = function() {
            var allTroubles = self.troubles();
            var checked = $("#checkAll").attr("checked");
            $.each(allTroubles, function() {
                this.checked(checked);
            })
        };
        self.unmerge = function() {
            var devc = this;
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "unmergeTroubleNew",
                    id_devc: this.id
                },
                beforeSend: function() {
                    return true;
                },
                success: function(data) {
                    var devc_arr = self.troubleForEditing.devcapsules;
                    var index = devc_arr().indexOf(devc);
                    devc_arr.splice(index, 1);
                    self.search();
                }
            });
        }
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

    $("#date-timeout").datepicker({
        format: "dd/mm/yyyy",
        weekStart: 1,
        startView: 0,
        todayHighlight: true,
        keyboardNavigation: true
    });

    $("#date-search").datepicker({
        format: "dd/mm/yyyy",
        weekStart: 1,
        startView: 0,
        autoclose: true,
        todayHighlight: true,
        keyboardNavigation: true
    });

    $("#time-timeout").timepicker({
        showMeridian: false,
        minuteStep: 10,
        disableFocus: true,
        showSeconds: true,
        defaultTime: 'value'
    });

    $('#editingDialog')
        .modal({show:false})
        .on('hidden', function() {
            $("#checkAll").attr("checked", false);
            $("#text-comment").popover('hide');
        });

    $("#send-comment").click(function() {
        $("#text-comment").val();
    });

    $("#text-comment").popover({
        placement: "top",
        html: true,
        content: "Please, enter the comment",
        title: "<h5 class='text-error'>Comment error</h5>",
        trigger: "manual"
    });

    $("#text-comment").bind("focus", function() {
        $("#control-group-add-crm-comment").removeClass("error");
        $(this).popover('hide');
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

    (function() {
        $.get(host, {cmd:"getServices"},
            function (data) {
                viewModel.services(data);
            }, "json"
        );
    })();

    getUser();
});