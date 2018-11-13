
/* global port, io */


//var webSocket = new WebSocket("");

function Login() {
    
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var receive;
    
    //var str = JSON.stringify({username: username, password: password, action: login});
    //var jSon = JSON.parse(str);
    if ((username === "admin") && (password === "admin")) {
        location.assign("file:///E:/Project/NCKH/HTML/Admin/Admin/public_html/ListDevices.html");
    }
    else{
        alert("Wrong password or username");
    }
}