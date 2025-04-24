<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/assets/css/login.css">
</head>
<body>
<div class="login-container">

    <h2>Đăng nhập</h2>
    <div id="loginFail">Sai tài khoản hoặc mật khẩu</div>
    <form:form class="login-form">
        <div class="input-group">
            <label for="userName">Tài khoản</label>
            <input type="text" id="USERNAME" name="userName" required placeholder="Nhập tài khoản">
        </div>
        <div class="input-group">
            <label for="password">Mật khẩu</label>
            <input type="password" id="PASSWORD" name="password" required placeholder="Nhập mật khẩu">
        </div>
        <!-- Hidden input để gửi redirect_uri -->
        <input type="hidden" id="REDIRECT_URI" name="redirect_uri"/>
        <button type="button" id="LOGIN_BTN">Đăng nhập</button>
    </form:form>


    <p class="message">Chưa có tài khoản? <a href="#">Đăng ký</a></p>

</div>

<%--<script>--%>
<%--    // Lấy redirect_uri từ URL nếu có--%>
<%--    const params = new URLSearchParams(window.location.search);--%>
<%--    const redirectUri = params.get("redirect_uri");--%>
<%--    if (redirectUri) {--%>
<%--        document.getElementById("redirect_uri").value = redirectUri;--%>
<%--    }--%>
<%--</script>--%>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
    $(document).ready(function () {
        const urlParams = new URLSearchParams(window.location.search);
        const redirectUri = urlParams.get('redirect_uri') || "http://localhost:3006/dashboard";
        $('#REDIRECT_URI').val(redirectUri);

        $('#LOGIN_BTN').click(function () {
            $("#loginFail").hide();

            const username = $('#USERNAME').val();
            const password = $('#PASSWORD').val();

            if (!username || !password) {
                $("#loginFail").text("Vui lòng nhập đầy đủ thông tin").show();
                return;
            }

            const requestData = {
                username: username,
                password: password
            };

            $.ajax({
                url: 'http://localhost:8080/api/login-web',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(requestData),
                success: function (res) {
                    if (res.status === true && res.data && res.data.token) {
                        const accessToken = res.data.token;
                        const userName = res.data.user.userName;
                        window.location.href = redirectUri + "?access_token=" + encodeURIComponent(accessToken) + "&userName=" + encodeURIComponent(userName);
                    } else {
                        $("#loginFail").text("Đăng nhập không thành công.").show();
                    }
                },
                error: function () {
                    $("#loginFail").text("Sai tài khoản hoặc mật khẩu").show();
                }
            });
        });
    });
</script>
</body>
</html>
