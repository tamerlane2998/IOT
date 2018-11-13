function display(user){
    var table = document.getElementById("tableUsers");
    
    var tr = document.createElement("tr");
    table.appendChild(tr);
    
    var username = document.createElement("td");
    var tagA = document.createElement("a");
    tagA.setAttribute("href","#");
    tagA.innerHTML = user.username;
    username.appendChild(tagA);
    tr.appendChild(username);
    
    var password = document.createElement("td");
    password.innerHTML = user.password;
    tr.appendChild(password);
}

window.onload = function () {
    /*var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState === 4 && xhttp.status === 200)
        {
            var device = JSON.stringify(xhttp.responseText);
            display(device);
        }
    };
    xhttp.open("POST","URL",true);
    xhttp.send();*/
    var str = {"username":"admin","password":"admin"};
    var str2 = {"username":"user1","password":"123123"};
    display(str);
    display(str2);
};