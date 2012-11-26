$(document).ready(function () {
    var host = "/controller";

    function twoChar(param) {
        return param.toString().length == 1 ? "0" + param : param;
    }

    function sDecrease(a, b) { // По убыванию
        if (a.time > b.time)
            return -1;
        else if (a.time < b.time)
            return 1;
        else
            return 0;
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

    function convertFormattedStringToDate(str) {
        var exprDateFormat = /(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/]([2]\d{3})[ ]([0-1][0-9]|2[0-4])[:]([0-5][0-9])[:]([0-5][0-9])/;
        var date;
        if (exprDateFormat.exec(str)) {
            date = new Date();
            var dateParts = str.replace(exprDateFormat, "$1 $2 $3 $4 $5 $6").split(" ");
            date.setDate(dateParts[0]);
            date.setMonth(dateParts[1]-1);
            date.setFullYear(dateParts[2]);
            date.setHours(dateParts[3]);
            date.setMinutes(dateParts[4]);
            date.setSeconds(dateParts[5]);
        }
        return date;
    }

    function getTimeToResolve(timestamp, now) {
        if (!timestamp) return -2;

        var diffInSec = ((parseInt(timestamp) - now) / 1000);
        if (diffInSec <= 0) return -1;

        var days = parseInt(diffInSec / (3600 * 24));
        diffInSec = diffInSec - (days * 3600 * 24);
        var hours = parseInt(diffInSec / 3600);
        diffInSec = diffInSec - (hours * 3600);
        var minutes = parseInt(diffInSec / 60);
        diffInSec = diffInSec - (minutes * 60);
        var seconds = parseInt(diffInSec);

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
        self.allTroubles = ko.observableArray();

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

        self.timeNow = ko.observable(new Date().getTime());
        self.timeToResolve = ko.computed(function() {
            $.each(self.currentTroubles(), function() {
                this.timeToResolve(getTimeToResolve(this.timeout, self.timeNow()));
            });
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
                    this.time = getRightTimeFormat(this.time);
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
                    cmd: "editTroubleOfCurrentTroubleListNew",
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
                    $('#editingDialog').modal('hide');
                }
            });
        };
        self.sendToCRM = function() {
            var trouble = this;
            var errorAlert = self.modalDialog.errorAlert;
            $.ajax({
                url : "/controller",
                type : "POST",
                dataType: 'JSON',
                data: {
                    cmd: "sendToCRMNew",
                    trouble: ko.toJSON(trouble)
                },
                beforeSend: function() {
                    trouble.timeout($.trim(trouble.timeout()));
                    var expr = /(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/][2]\d{3}[ ]([0-1][0-9]|2[0-4])[:][0-5][0-9][:][0-5][0-9]/;
                    if (!$.trim(trouble.title())) {
                        errorAlert.show(true);
                        errorAlert.message("Please, enter the title of trouble.");
                        return false;
                    } else if (trouble.services().length == 0) {
                        errorAlert.show(true);
                        errorAlert.message("Please, select the affected services.");
                        return false;
                    } else if (!trouble.timeout()) {
                        errorAlert.show(true);
                        errorAlert.message("Please, enter the time of resolving problem.");
                        return false;
                    } else if (trouble.timeout() && !expr.exec(trouble.timeout())) {
                        errorAlert.show(true);
                        errorAlert.message("Please, enter the time of resolving problem in correct format.");
                        return false;
                    } else if (!trouble.close() && getTimeToResolve(trouble.timeoutObj().getTime(), new Date().getTime()) == "-1") {
                        errorAlert.show(true);
                        errorAlert.message("Please, more actual resolving time.");
                        return false;
                    } else if (trouble.comments().length == 0) {
                        errorAlert.show(true);
                        errorAlert.message("Please, add at least one comment.");
                        return false;
                    }
                    errorAlert.show(false);
                    return true;
                },
                success: function(data) {
                    if (!data.status) {
                        errorAlert.show(true);
                        errorAlert.message(data.message);
                    } else {
                        $('#editingDialog').modal('hide');
                    }
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
                    data.time = getRightTimeFormat(data.time);
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

            $.each(self.allTroubles(), function() {
                if (this.checked()) {
                    comments = comments.concat(this.comments);
                    devcapsules = devcapsules.concat(this.devcapsules);
                    close = close && this.close;
                    ids.push(this.id);
                }
            });

            if (ids.length > 0) {
                $.each(comments, function() {
                    this.time = getRightTimeFormat(this.time);
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
                }
            });
        };
        self.stopRefreshOnChecked = function check() {
            resetRefreshDataInterval();
        };
        self.checkAll = function() {
            var allTroubles = self.allTroubles();
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
                    getJSONData();
                }
            });
        }
    }

    var viewModel = new ViewModel();
    ko.applyBindings(viewModel);

    var updateResolveTime;

    function setUpdateResolveTimeInterval() {
        updateResolveTime = setInterval(function() {
            viewModel.timeNow(new Date().getTime());
        }, 1000);
    }

    function resetUpdateResolveTimeInterval() {
        clearInterval(updateResolveTime);
        setUpdateResolveTimeInterval();
    }

    setUpdateResolveTimeInterval();

    $("#date-timeout").datepicker();

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
            getJSONData();
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

    function getJSONData() {
        $.get(host, {cmd:"getCurrentTroubleListGroup"},
            function (data) {
                var allTroubles = data.wait.troubles.concat(data.need.troubles).concat(data.current.troubles);
                $.each(allTroubles, function() {
                    if (!this.timeout)this.timeout = "";
                    this.timeToResolve = ko.observable();
                    this.checked = ko.observable(false);
                });
                viewModel.currentTroubles(data.current.troubles);
                viewModel.waitingCloseTroubles(data.wait.troubles);
                viewModel.needCRMTroubles(data.need.troubles);

                viewModel.allTroubles(allTroubles);
            }, "json"
        );
    }

    var refreshData;

    function setRefreshDataInterval() {
        refreshData = setInterval(function() {
            getJSONData();
        }, 30000);
    }

    function resetRefreshDataInterval() {
        clearInterval(refreshData);
        setRefreshDataInterval();
    }

    setRefreshDataInterval();

    $("#refesh-page").click(function() {
        getJSONData();
    });

    (function() {
        $.get(host, {cmd:"getServices"},
            function (data) {
                viewModel.services(data);
            }, "json"
        );
    })();

    getJSONData();
});