
/* global port, io */


var webSocket = new WebSocket("ws://192.168.8.106:8080/Server_test_06/actions");

webSocket.onopen = function () {
    webSocket.send("123");
    alert("ok");
};

webSocket.onmessage = function (evt) {

    var receive;
    receive = evt.data;
    alert("Received");

};

function Login() {

    var username = document.getElementById("Username").value;
    var password = document.getElementById("Password").value;


    var str = JSON.stringify(
            {username: username,
                password: password,
                action: "login"});
    webSocket.send(str);
}