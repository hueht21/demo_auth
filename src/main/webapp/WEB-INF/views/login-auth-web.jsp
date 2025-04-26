<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/assets/css/login.css">
    <style>
        #loginFail {
            display: none;
            color: red;
            margin-top: 10px;
        }

        #loadingSpinner {
            display: none;
            margin-top: 10px;
            color: #007bff;
        }
    </style>
</head>
<body>
<div class="login-container">

    <h2>Đăng nhập</h2>
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

    <div id="loadingSpinner">Đang xử lý đăng nhập...</div>
    <div id="loginFail">Sai tài khoản hoặc mật khẩu</div>

    <p class="message">Chưa có tài khoản? <a href="#">Đăng ký</a></p>

    <div style="text-align: center; margin-top: 20px;">
        <a href="/oauth2/authorization/google" id="googleLoginBtn">
            <img src="https://developers.google.com/identity/images/btn_google_signin_dark_normal_web.png"
                 alt="Sign in with Google" />
        </a>
    </div>

</div>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
    $(document).ready(function () {

        const serverData = {
            uri: "<c:out value='${redirect_uri}'/>",
        };
        console.log(serverData.uri)

        const urlParams = new URLSearchParams(window.location.search);
        const redirectUri = urlParams.get('redirect_uri') || serverData.uri;
        $('#REDIRECT_URI').val(redirectUri);

        $('#LOGIN_BTN').click(function () {
            $("#loginFail").hide();
            $("#loadingSpinner").show();

            const username = $('#USERNAME').val();
            const password = $('#PASSWORD').val();

            if (!username || !password) {
                $("#loadingSpinner").hide();
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
                    $("#loadingSpinner").hide();
                    if (res.status === true && res.data && res.data.token) {
                        const accessToken = res.data.token;
                        const userName = res.data.user.userName;
                        console.log("Đăng nhập thành công với token: " + accessToken);
                        window.location.href = redirectUri + "?access_token=" + encodeURIComponent(accessToken) + "&userName=" + encodeURIComponent(userName);
                    } else {
                        $("#loginFail").text("Đăng nhập không thành công.").show();
                    }
                },
                error: function () {
                    $("#loadingSpinner").hide();
                    $("#loginFail").text("Sai tài khoản hoặc mật khẩu").show();
                }
            });
        });

        // $('button#GOOGLE_LOGIN_BTN').click(function(){
        //     console.log("vào google login" + redirectUrl);
        // 	window.location.href = "base_url" + '/oauth2/authorize/google?redirect_uri=' + redirectUrl;
        // });
    });
</script>
</body>
</html>
