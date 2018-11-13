var webSocket = new WebSocket('ws://192.168.8.100:8080/Server_java/actions');

webSocket.onopen = function () {
    alert('Opened connection ');

    var jSon = JSON.stringify({action: 'admin'});
    webSocket.send(jSon);
};

webSocket.onmessage = function (event) {
    var receive = event.data;
    if (receive.action === 'toggle') {
        if (status === "On<a href=\"#\" onclick=\"toggleButton(" + id + ")\"> (Turn off)</a>")
        {
            button.innerHTML = "Off<a href=\"#\" onclick=\"toggleButton(" + id + ")\"> (Turn on)</a>";
        } else if (status === "Off<a href=\"#\" onclick=\"toggleButton(" + id + ")\"> (Turn on)</a>") {
            button.innerHTML = "On<a href = \"#\" onclick = toggleButton(" + id + ") > (Turn off)</a>";
        }
    }
    if(receive.action === 'showDevice'){
        
    }
};

window.onload = function () {
    alert('okfksdkfksdfk');
//    var str = {"name": "Bong den", "status": "On", "type": "Rang dong", "description": "Hinh tron", "license": "Accepted", "kind": "Don't know", "id": "1"};
//    var str2 = {"name": "Laptop", "status": "Off", "type": "Dell", "description": "14inch", "license": "Accepted", "kind": "Huynh", "id": "4"};
//    display(str);
//    display(str2);

};

function toggleButton(id) {
    var button = document.getElementById(id);
    var status = button.innerHTML;
    var jSon = JSON.stringify({action: 'changeStatus', id: id});
    webSocket.send(jSon);
}

function display(device) {

    var table = document.getElementById("tableDevices");

    var tr = document.createElement("tr");
    table.appendChild(tr);

    var name = document.createElement("td");
    name.innerHTML = device.name;
    tr.appendChild(name);

    var status = document.createElement("td");
    status.setAttribute("id", device.id);
    if (device.status === "On")
    {
        status.innerHTML = device.status + "<a href = \"#\" onclick = toggleButton(" + device.id + ") > (Turn off)</a>";
    } else if (device.status === "Off") {
        status.innerHTML = device.status + "<a href = \"#\" onclick = toggleButton(" + device.id + ") > (Turn on)</a>";
    }
    tr.appendChild(status);

    var type = document.createElement("td");
    type.innerHTML = device.type;
    tr.appendChild(type);

    var description = document.createElement("td");
    description.innerHTML = device.description;
    tr.appendChild(description);

    var license = document.createElement("td");
    license.innerHTML = device.license;
    tr.appendChild(license);

    var kind = document.createElement("td");
    kind.innerHTML = device.kind;
    tr.appendChild(kind);
}







