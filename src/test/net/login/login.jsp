<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>Вход в систему администрирования</title>
    <link rel="stylesheet" type="text/css" href="../css/login.css"/>
    <script type="text/javascript" src="../js/jQuery/js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="../js/jQuery/js/jquery.form.js"></script>
    <script type="text/javascript" src="../js/login.js"></script>
</head>
<body>

<div id="form-box-id">
    <form action="/controller" method="post" id="login-form-id">

        <div>Имя пользователя</div>
        <div><input type="text" name="login" id="login"/></div>

        <div>Пароль</div>
        <div><input type="password" name="passwd" id="passwd"/></div>

        <input type="hidden" name="cmd" value="login"/>

        <div><input type="submit" id="login-button" value="Войти в систему"/></div>

    </form>
</div>
<div id="response"></div>

</body>
</html>