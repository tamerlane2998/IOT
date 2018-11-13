/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.websocket;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import org.example.model.Device;
import org.example.hibernate.Util;
import org.example.model.User;

/**
 *
 * @author V khuat
 */
@ApplicationScoped
public class DeviceSessionHandler {

    private int deviceId;

    public Util getUtil() {
        return util;
    }

    static private User admin;                      //admin khi dang nhap
    private Set<User> users;                 //list session cua user dang dang nhap
    private Set<Device> devices;            //list cac thiet bi dang ket noi
    private Util util;                      //oject thao tac db

    public void setAdmin(int id) {
        User admin = util.getUser(id);
    }

    public DeviceSessionHandler() {
        users = new HashSet<User>();
        admin = null;
        util = new Util();
        devices = new HashSet<Device>();
        if (util.getLastDevice() != null) {
            deviceId = util.getLastDevice().getId() + 1;
        } else {
            deviceId = 1;
        }
    }

//  go bo session cua client khi ngat ket noi
    public void removeSession(Session session) {
        try {
            for (Device device : devices) {
                if (device.getSession().getId().equals(session.getId())) {
                    devices.remove(device);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            for (User user : users) {
                if (user.getSession().getId().equals(session.getId())) {
                    users.remove(user);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            session.close();
        } catch (Exception ex) {
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//  Them moi 1 thiet bi trong he thong    
    public void addDevice(Device device, Session session) {
        device.setId(deviceId);
        device.setStatus("Off");
        deviceId++;
        String licenseCode = getNewLicense();
        Util util = new Util();
        ArrayList<Device> list = util.getListDevice();
        System.out.println(list.get(0).getLicense());
        System.out.println(licenseCode);
        boolean tmp = false;
        while (tmp == false) {
            tmp = true;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getLicense().equals(licenseCode)) {
                    licenseCode = getNewLicense();
                    tmp = false;
                    break;
                }
//                if(i == list.size()) tmp = true;                            
            }
        }
        device.setLicense(licenseCode);
        JsonProvider provider = JsonProvider.provider();
        JsonObject setLicenseMessage = provider.createObjectBuilder()
                .add("action", "setLicense")
                .add("license", licenseCode)
                .build();
        sendToSession(session, setLicenseMessage);
        util.addDevice(device);
        devices.add(device);
//            sendToAllConnectedSessions(removeMessage);
//        JsonObject addMessage = createAddMessage(device);
//        sendToAllConnectedSessions(addMessage);
    }

//  update key moi cho moi thiet bi khi ket noi
    public void updateConnectDevice(int id, Session session) {
        String licenseCode = getNewLicense();
        JsonProvider provider = JsonProvider.provider();
        JsonObject setLicenseMessage = provider.createObjectBuilder()
                .add("action", "setLicense")
                .add("license", licenseCode)
                .build();
        util.updateDevice(id, "license " + licenseCode);
        Device device = util.getDevice(id);
        device.setSession(session);
        devices.add(device);
        sendToSession(session, setLicenseMessage);
    }

//  Sinh key moi cho moi thiet bi
    private String getNewLicense() {
        StringBuffer newLicense = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            newLicense.append((char) rand(65, 90));
        }
        return newLicense.toString();
    }

//  Kiem tra xem thiet bi da o trong he thong hay chua    
    public int exist(String license) {
        for (Device temp : util.getListDevice()) {
            if (license.equals(temp.getLicense())) {
                return temp.getId();
            }
        }
        return 0;
    }

//  Go bo 1 thiet bi trong he thong voi quyen admin
    public void removeDevice(int id, Session session) {
        Device device = util.getDevice(id);
        if (device != null && session.getId().equals(admin.getSession().getId())) {
            try {
                for (Device tempDevice : devices) {
                    if (device.getId() == tempDevice.getId()) {
                        device.getSession().close();
                        devices.remove(device);
                    }
                }
                util.removeDevice(id);
            } catch (Exception e) {
            }
//            JsonProvider provider = JsonProvider.provider();
//            JsonObject removeMessage = provider.createObjectBuilder()
//                    .add("action", "remove")
//                    .add("id", id)
//                    .build();
//            sendToAllConnectedSessions(removeMessage);
        }
    }

//  Doi trang thai cua thiet bi voi quyen user    
    public void toggleDevice(int id, Session session) {
//        JsonProvider provider = JsonProvider.provider();
        Device device = getDeviceById(id);
        if (device != null && isConnected(device)) {
            if ("On".equals(device.getStatus())) {

                device.setStatus("Off");
                util.updateDevice(id, "toggle Off");
            } else {
                device.setStatus("On");
                util.updateDevice(id, "toggle On");
            }
            JsonObject changeStatusMessage = JsonProvider.provider().createObjectBuilder()
                    .add("action", "toggle")
                    .build();
            sendToSession(admin.getSession(), changeStatusMessage);
        }
    }

//  yeu cau thiet bi thay doi trang thai
    public void changeStatus(Session session) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject changeStatusMessage = provider.createObjectBuilder()
                .add("action", "changeStatus")
                .build();
        sendToSession(session, changeStatusMessage);
    }
    
//    gui file toi thiet bi
    public void sendFileToDevice(Session session, JsonObject message){
        sendToSession(session, message);
    }

//  tra ve session dang hoat dong voi id cua thiet bi, neu khong co tra ve null    
//    private Session getSessionById(int id) {
//        String sessionId = util.getDevice(id).getSession();
//        for (Session session : sessionsDevice) {
//            if (session.getId().equals(sessionId)) {
//                return session;
//            }
//        }
//        return null;
//    }
//  Tra ve thiet bi voi id    
    private Device getDeviceById(int id) {
        for (Device device : devices) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

//  Kiem tra thiet bi co ket noi    
    private boolean isConnected(Device device) {
        for (Device tempDevice : devices) {
            if (device.getId() == tempDevice.getId()) {
                return true;
            }
        }
        return false;
    }

    private JsonObject createAddMessage(Device device) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", device.getId())
                .add("name", device.getName())
                .add("type", device.getType())
                .add("status", device.getStatus())
                .add("description", device.getDescription())
                .build();
        return addMessage;
    }

//  Gui message toi tat ca cac ket noi    
    private void sendToAllConnectedSessions(JsonObject message) {
        try {
            for (User user : users) {
                sendToSession(user.getSession(), message);
            }
        } catch (Exception e) {
        }
        try {
            for (Device device : devices) {
                sendToSession(device.getSession(), message);
            }
        } catch (Exception e) {
        }
    }

//  Gui ten nhan toi 1 session    
    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void errorAdd(Session session) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject erorrMessage = provider.createObjectBuilder()
                .add("action", "error")
                .add("data", "license")
                .build();
        sendToSession(session, erorrMessage);
    }

    public void getData(Session session) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject erorrMessage = provider.createObjectBuilder()
                .add("action", "get")
                .build();
        sendToSession(session, erorrMessage);
    }

    public static int rand(int min, int max) {
        try {
            Random rn = new Random();
            int range = max - min + 1;
            int randomNum = min + rn.nextInt(range);
            return randomNum;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void sendDeviceToUser(Session session) {

        ArrayList<Device> listDevice = util.getListDevice();
        JsonProvider provider = JsonProvider.provider();
        JsonArrayBuilder builder = Json.createArrayBuilder();

        for (Device dv : listDevice) {
            JsonObject deviceMessage = provider.createObjectBuilder()                   
                    .add("id", dv.getId())
                    .add("name", dv.getName())
                    .add("status", dv.getStatus())
                    .add("type", dv.getType())
                    .add("description", dv.getDescription())
                    .add("license", dv.getLicense())
                    .add("kind", dv.getKind())
                    .build();
            builder.add(deviceMessage);
        }
        JsonArray jsonArray = builder.build();
             
        JsonObject listDeviceMessage = provider.createObjectBuilder()
                .add("action", "showDevices")
                .add("listDevice", jsonArray)
                .build();
        
        sendToSession(session, listDeviceMessage);
    }

//    public void sendDeivesToUser(User user, Session session) {
//        Set<Device> devices = util.getDevicesForUser(user);
//        JsonProvider provider = JsonProvider.provider();
//        String str = null;
//        for (Device device : devices) {
//            str = str + device.toString() + ";";
//        }
//        JsonObject showMessage = provider.createObjectBuilder()
//                .add("action", "add")
//                .add("data", str)
//                .build();
//        sendToSession(session, showMessage);
//    }
    public String test() {
        String str = null;
        str = getNewLicense();
        return str;
    }
}
