#include <ArduinoJson.h>


#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266mDNS.h>
#include <ESP8266WebServer.h>
#include <WebSocketsClient.h>

#include <FS.h>   // Include the SPIFFS library

WebSocketsClient webSocket;
ESP8266WebServer server(80);    // Create a webserver object that listens for HTTP request on port 80
#define MAX_BUFFER  500

String getContentType(String filename); // convert the file extension to the MIME type
bool handleFileRead(String path);       // send the right file to the client (if it exists)
void handleFileUpload();                // upload a new file to the SPIFFS


char* status_wf = "connetion";

char* SSID_STA = "";
char* PASS_STA = "";

char* SSID_AP = "Hoang oc cho";
char* PASS_AP = "12345678";
char* license = "qazxswedcvfrtgbn";



 char* ipWebSocket = "192.168.8.105";
 int portWebSocket = 8080;
 char* linkWebSocket = "/Server/actions";

void writedatatest()
{
  SPIFFS.begin(); 
  SPIFFS.remove("/text.txt");  
  File f = SPIFFS.open("/text.txt", "r");

  if (!f) {
    Serial.println("[FILE]file open failed");
  }else{
    Serial.println("[FILE]Mo file thanh cong");
    String message = "";
    while(f.available())
    {
      message += f.readStringUntil('\n');
      Serial.printf("[FILE]Noi dung file:");
      Serial.println(message);
    }
  }
  SPIFFS.end();
}
void read_data_wifi()
{
  SPIFFS.begin(); 
  File f = SPIFFS.open("/data_wifi.tg", "r");
  if (!f) {
    Serial.println("[FILE]file open failed");
  }else{
    Serial.println("[FILE]Mo file thanh cong");
    String message = "";
    while(f.available())
    {
      message += f.readStringUntil('\n');
      Serial.printf("[FILE]Noi dung file:");
      Serial.println(message);
    }
    
    StaticJsonBuffer<MAX_BUFFER> file_json;
    JsonObject& file_json_encode = file_json.parseObject(message);
    if (file_json_encode.success()){
      SSID_STA = strdup(file_json_encode["SSID_STA"]);
      PASS_STA = strdup(file_json_encode["PASS_STA"]);
      ipWebSocket = strdup(file_json_encode["ipWebSocket"]);
      portWebSocket = file_json_encode["portWebSocket"];
      linkWebSocket = strdup(file_json_encode["linkWebSocket"]);
      license = strdup(file_json_encode["license"]);
      Serial.printf("[WIFI]SSID: %s \n     PASS: %s \n     IPWC: %s \n", SSID_STA, PASS_STA, ipWebSocket);
    }else Serial.println("[WIFI] Sai cau truc");
    f.close();
    Serial.println("[FILE] Close file");
  }
  
 
  SPIFFS.end();
}

void write_data_wifi(char* message)
{
  SPIFFS.begin(); 
  File f = SPIFFS.open("/data_wifi.tg", "w");
  if (!f) {
    Serial.println("[FILE]file open failed");
  }else{
      Serial.println("[FILE]Mo file ghi thanh cong");
      f.print(message);
      Serial.printf("[FILE]Ghi thanh cong:");
      Serial.println(message);
      f.close();
      Serial.println("[FILE] Close file");
    }
   
  
  SPIFFS.end();
}

void write_new_license(char* message)
{
  SPIFFS.begin(); 
  File f = SPIFFS.open("/data_wifi.tg", "w");
  if (!f) {
    Serial.println("[FILE]file open failed");
  }else{
      Serial.println("[FILE]Mo file ghi thanh cong");
      
      // ma hoa json 
       StaticJsonBuffer<MAX_BUFFER> file_json;
       JsonObject& file_json_encode = file_json.createObject();
       file_json_encode["SSID_STA"] = SSID_STA;  
       file_json_encode["PASS_STA"] = PASS_STA;  
       file_json_encode["ipWebSocket"] = ipWebSocket;  
       file_json_encode["portWebSocket"] = portWebSocket;
       file_json_encode["linkWebSocket"] = linkWebSocket;
       file_json_encode["license"] = message;

 
      char JSONmessageBuffer[MAX_BUFFER+ 5];// chuyen ve string de gui 
      file_json_encode.prettyPrintTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
    
      f.print(JSONmessageBuffer);
      Serial.printf("[FILE]Ghi thanh cong:");
      Serial.println(JSONmessageBuffer);
      f.close();
      Serial.println("[FILE] Close file");
    }
   
  
  SPIFFS.end();
}

// Websocket--------------------------------------
bool wifiDisconneted = true;
void webSocketEvent( WStype_t type, uint8_t * payload, size_t length) {
  switch(type) {
    case WStype_DISCONNECTED:
      if (wifiDisconneted == true)  Serial.printf("[WSc] Disconnected!\n");
      wifiDisconneted = false;
      break;
    case WStype_CONNECTED: {
      Serial.printf("[WSc] Connected to url: \n");
      wifiDisconneted = true;
      // send message to server when Connected
      encode_json_send_start(webSocket);
      break;
      }
      
    case WStype_TEXT:
      Serial.printf("[WSc] get text: %s\n", payload);
      //Serial.printf("vua nhan duoc mot tin nhan");
      decode_json_receive(webSocket,(char*) payload);
      break;
    case WStype_BIN:
      Serial.printf("[WSc] get binary length: %u\n", length);
      hexdump(payload, length);

      // send data to server
      // webSocket.sendBIN(payload, length);
      break;
  }

}

// bat tay 2 thiet bi 
// gui lan 1


void encode_json_send_start(WebSocketsClient  &webSocket)
{
  StaticJsonBuffer<MAX_BUFFER> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
  JSONencoder["action"] = "add";
  JSONencoder["kind"] = "receive";
  JSONencoder["name"] = "esp8266_voi_phun_nuoc";
  JSONencoder["license"] = license;
  JSONencoder["type"] = "123";
  JSONencoder["description"] = "asdfsa";
  char JSONmessageBuffer[MAX_BUFFER+1];
  JSONencoder.prettyPrintTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  Serial.println("[KEY]Gui du lieu bat tay");
  Serial.println(JSONmessageBuffer);
  
  webSocket.sendTXT(JSONmessageBuffer); 
}

void send_json_connect(WebSocketsClient  &webSocket)
{
  StaticJsonBuffer<MAX_BUFFER> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
  JSONencoder["action"] = "post";
  JSONencoder["data"] = "receive";
  JSONencoder["id"] = "esp8266_voi_phun_nuoc";

  char JSONmessageBuffer[MAX_BUFFER+4];
  JSONencoder.prettyPrintTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  Serial.println("[DATA]Gui du lieu");
  Serial.println(JSONmessageBuffer);
  
  webSocket.sendTXT(JSONmessageBuffer); 
} 

//=---------------------------------------------------------------------------------
void open_file_write_data(String link, String message)
{
  SPIFFS.begin(); 
  File f = SPIFFS.open(("/" + link).c_str(), "a");
  if (!f) {
    Serial.println("[FILE]file open failed: /" + link);
  }else{
      Serial.println("[FILE]Mo file ghi thanh cong/" + link);
      f.print(message);
      Serial.printf("[FILE]Ghi thanh cong:");
      Serial.println(message);
      f.close();
      Serial.println("[FILE] Close file");
    }
  SPIFFS.end();
}
void send_data(WebSocketsClient  &webSocket, String link_file, int byte_h, int size_of_text)
{
  StaticJsonBuffer<MAX_BUFFER> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
  JSONencoder["action"] = "GETTEXT";
  JSONencoder["file"] = link_file;
  JSONencoder["byte"] = byte_h;
  JSONencoder["sizetext"] = size_of_text;
  
  char JSONmessageBuffer[MAX_BUFFER+1];
  JSONencoder.prettyPrintTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  Serial.println("[DATA]Gui du lieu");
  Serial.println(JSONmessageBuffer);
  webSocket.sendTXT(JSONmessageBuffer); 
}
int end_data = 0;

void get_data(WebSocketsClient &webSocket, String message)
{
  StaticJsonBuffer<MAX_BUFFER> JSONBuffer;                     //Memory pool
  JsonObject& parsed = JSONBuffer.parseObject(message); //Parse message
   if (!parsed.success()) {   //Check for errors in parsing
    Serial.println("[DATA]Parsing failed");
    return;
  }
  char *link_file = strdup(parsed["file"]);
  int byte_h = parsed["byte"];
  int size_of_text = parsed["sizetext"];
  char *text = strdup(parsed["text"]);
  Serial.println("[DATA] File: " + String(link_file));
  Serial.println("[DATA] text: " + String(text));
  
  open_file_write_data(link_file, text);

  send_data(webSocket, link_file, byte_h + size_of_text, size_of_text);
  Serial.println("[DATA] Da gui yeu cau");
}
//-----------------------------------------------------------------------------------

void decode_json_receive(WebSocketsClient &webSocket, String message)
{
  StaticJsonBuffer<MAX_BUFFER> JSONBuffer;                     //Memory pool
  JsonObject& parsed = JSONBuffer.parseObject(message); //Parse message
 
  if (!parsed.success()) {   //Check for errors in parsing
    Serial.println("[KEY]Parsing failed");
    return;
  }
  char* action = strdup(parsed["action"]);           //Get sensor type value
  if (strcmp(action,"setLicense") == 0)
  {
    Serial.println("[KEY]Nhan duoc key moi");
    license =  strdup(parsed["license"]);
    write_new_license(license);
    Serial.printf("[KEY]Da doi key moi: %s\n", license);
    Serial.println("Send data");
    send_data(webSocket, "text.txt",0, 300);
  
    return;  
  }
  if (strcmp(action, "get") == 0)
  {
     Serial.println("[DATA]Phan hoi");
     send_json_connect(webSocket);
  }
  if (strcmp(action, "error") == 0)
  {
      status_wf = strdup(parsed["data"]);
      strcat(status_wf, " error");
      webSocket.setReconnectInterval(10000000);
  }

  if (strcmp(action,"TEXT") ==0)
  {
    Serial.println("[DATA] receive");
    get_data(webSocket, message);
  }
  
}
// server ----------------------------------------------------

//
String anchars = "abcdefghijklmnopqrstuvwxyz0123456789" ;// truong key
String sessioncookie = "";  // sessionkie duy tri dang nhap 
void make_session(){
  sessioncookie = "";
  for(int  i = 0; i < 32; i++) sessioncookie += anchars[random(0, anchars.length())]; // random key co trong anchars
}
  
//
bool check_user_login()
{
  if (server.hasHeader("Cookie")){
    String cookie = server.header("Cookie"), authk = "c=" + sessioncookie;
    if (cookie.indexOf(authk) != -1) return true;
  }
  return false;
}
void func_header()
{
  if (!check_user_login()) // check server login 
  {
    String header = "HTTP/1.1 301 OK\r\n Location:/login\r\nCache-Control: no-cache\r\n\r\n";
                    
    server.sendContent(header);
    return;
  }
   server.send(200, "text/html", "<h1>Welcome, " + server.arg("username") + "!</h1><p>"+status_wf +"</p><form action=\"/writefile\" method=\"POST\"><input type=\"text\" name=\"ssid_sta\" placeholder=\"SSID Station\"></br><input type=\"text\" name=\"pass_sta\" placeholder=\"PASS Station\"></br><input type=\"text\" name=\"ip_web_socket\" placeholder=\"IP WebSocket\"></br><input type=\"text\" name=\"port_web_socket\" placeholder=\"Port WebSocket\"></br><input type=\"text\" name=\"link_web_socket\" placeholder=\"Link WebSocket\"></br><input type=\"text\" name=\"license\" placeholder=\"License\"></br><input type=\"submit\" value=\"Save\"></form>");
   
  
}
void func_login()
{
  
  String web = "             <!DOCTYPE html>"
  "             <html>"
  "             <head>"
  "             <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
  "             <style>"
  "             body {font-family: Arial, Helvetica, sans-serif; background-color: #262525}"
  "             form {border: 3px solid #262525;}"
  "             input[type=text], input[type=password] {"
  "                 width: 100%;"
  "                 padding: 12px 20px;"
  "                 margin: 10px 6px 15px;"
  "                 display: inline-block;"
  "                 border: 1px solid #ccc;"
  "                 box-sizing: border-box; " 
  "             }"
  "             .container {"
  "               margin-left: 25%;"
  "               margin-top: 10%;"
  "               width: 40%;"
  "                 padding: 30px 60px 20px;"
  "               box-shadow: 3px 4px 5px 5px rgba(0, 0, 0, 0.2);"
  "               background-color: #bc0505;"
  "               border-radius: 10px;"
  "             }"
  "             button{"
  "               width: 90px;"
  "               height: 35px;"
  "                 box-shadow: 1px 1px 1px 1px rgba(0, 0, 0, 0.2);"
  "             }"
  "               .h2{"
  "                 margin-top:30%;"
  "               }"
  "             </style>"
  "             </head>"
  "             <body>"
  "             <center>"
  "               <h2>&nbsp;</h2>"
  "             </center>"
  "             <form action = \"/login\" \"method=\"POST\">"
  "               <div class=\"container\">"
  "                 <center><h2>LOGIN</h2></center>"
  "                 <label><strong>Username</strong></label>"
  "                 <input type=\"text\"  placeholder=\"Enter Username\" name =\"username\" >"
  "                 <label><b>Password</b></label>"
  "                 <input type=\"password\"  placeholder=\"Enter Password\" name=\"password\" >" 
  "                 <center><input type=\"submit\" value=\"Login\"></center>"
  "               </div>"
  "             </form>"
  "             </body>"
  "             </html>";

  

  
  
  String msg = "";
  if( server.hasArg("username") && server.hasArg("password") 
      && server.arg("username") != NULL && server.arg("password") != NULL)
  {
     if(server.arg("username") == "admin" && server.arg("password") == "admin")
     { 
        String header = "HTTP/1.1 301 OK\r\nSet-Cookie: c=" + sessioncookie + "\r\nLocation: /\r\nCache-Control: no-cache\r\n\r\n"; //if above values are good, send 'Cookie' header with variable c, with format 'c=sessioncookie'
        server.sendContent(header);  
        return ;
     } else {                                                                            
      msg = "<center><br>Wrong username/password</center>";
   }
  }

  server.send(200, "text/html", web + msg);
}

void func_index()
{
  

}
void func_write_file()
{
  if(  ! server.hasArg("ssid_sta")) {// If the POST request doesn't have username and password data
    server.send(400, "text/plain", "400: Invalid Request");         // The request is invalid, so send HTTP status 400
    return;
  }
  server.send(200, "text/html", "<p>Da save da lieu</p><p>Restart ESP8266</p>");
  StaticJsonBuffer<MAX_BUFFER> file_json;
  JsonObject& file_json_encode = file_json.createObject();
  if (server.arg("ssid_sta") == NULL) file_json_encode["SSID_STA"] = SSID_STA;  
  else file_json_encode["SSID_STA"] = server.arg("ssid_sta");
  if (server.arg("pass_sta") == NULL) file_json_encode["PASS_STA"] = PASS_STA;  
  else file_json_encode["PASS_STA"] = server.arg("pass_sta");
  
  if (server.arg("ip_web_socket") == NULL) file_json_encode["ipWebSocket"] = ipWebSocket;  
  else file_json_encode["ipWebSocket"] = server.arg("ip_web_socket");
  if (server.arg("port_web_socket") == NULL) file_json_encode["portWebSocket"] = portWebSocket;
  else file_json_encode["portWebSocket"] = server.arg("port_web_socket");
  if (server.arg("link_web_socket") == NULL) file_json_encode["linkWebSocket"] = linkWebSocket;
  else file_json_encode["linkWebSocket"] = server.arg("link_web_socket");

  if (server.arg("license") == NULL) file_json_encode["license"] = license;
  else file_json_encode["license"] = server.arg("license");
  

  
  char JSONmessageBuffer[MAX_BUFFER+ 4];
  file_json_encode.prettyPrintTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  write_data_wifi(JSONmessageBuffer);
  ESP.restart();
}
void func_not_found()
{

  server.send(404, "text/plain", "404: Not found"); 
}
//code --------------------------------------------
void setup()
{
  Serial.begin(115200);         // Start the Serial communication to send messages to the computer
  for(int i = 1; i<= 3; i++)
  {
    Serial.printf("%d ", i);
    delay(1000);

  }
  writedatatest();
  Serial.println('\n');
  read_data_wifi();
  WiFi.mode(WIFI_AP_STA); 
  Serial.printf("[WIFI]SSID: %s \n     PASS: %s \n", SSID_STA, PASS_STA);
  delay(1000);
  WiFi.begin(SSID_STA, PASS_STA);
  WiFi.softAP(SSID_AP, PASS_AP);

  // server
  server.on("/", func_header);             
  server.on("/login",  func_login);
  
  server.on("/index", func_index) ;         // sau khi login vao ham dang nhap
  server.on("/writefile", func_write_file);
  server.onNotFound(func_not_found);        // When a client requests an unknown URI (i.e. something other than "/"), call function "handleNotFound"
  make_session();
  Serial.println("Set sission");
  const char * headerkeys[] = {"User-Agent","Cookie"} ;
  size_t headerkeyssize = sizeof(headerkeys)/sizeof(char*);
  server.collectHeaders(headerkeys, headerkeyssize );
  Serial.println("Connect session");


  server.begin();                           // Actually start the server
  Serial.println("HTTP server started");
  Serial.println("Connecting ...");
  int i = 0;

  
  while (WiFi.status() != WL_CONNECTED) { // Wait for the Wi-Fi to connect
    delay(1000);
    Serial.print(++i); Serial.print(' ');
    if (i > 10) break;
  }
  if (WiFi.status() == WL_CONNECTED)
  {
    Serial.println('\n');
    Serial.print("Connected to ");
    Serial.println(WiFi.SSID());              // Tell us what network we're connected to
    Serial.print("IP address:\t");
    Serial.println(WiFi.localIP());           // Send the IP address of the ESP8266 to the computer

    // websocket
    webSocket.begin(ipWebSocket,portWebSocket,linkWebSocket);
    // event handler
    webSocket.onEvent(webSocketEvent);
    webSocket.setReconnectInterval(5000);
  }
  
}

void loop() {
  webSocket.loop();
  server.handleClient();  
  if (WiFi.status() != WL_CONNECTED) status_wf = "connect error";
}
