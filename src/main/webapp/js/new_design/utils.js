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

function getOverdueTime(timestamp, now) {
    if (!timestamp) return -2;

    var diffInSec = ((now - parseInt(timestamp)) / 1000);
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