$(document).ready(function() {
    var popup1 = new PopupMenu();
    popup1.add('open new window', function(target) {
        window.open(location.href);
    });
    popup1.add('alert', function(target) {
        alert('alert!');
    });
    popup1.addSeparator();
    popup1.add('close', function(target) {
        window.close();
    });
    popup1.setSize(140, 0);
    popup1.bind('polygon'); // target is this pre block

    /*$("#polygon").click(function() {
     alert();
     });*/
});