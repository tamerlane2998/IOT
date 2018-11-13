/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.websocket;

/**
 *
 * @author V khuat
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.example.hibernate.Util;
import org.example.model.Device;
import org.example.model.User;
import java.io.File;

@ApplicationScoped
@SuppressWarnings("unused")
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

    // list quan li cac luong duoc mo ra de check ket noi voi cac thiet bi
    private ArrayList<CustomThread> listThread = new ArrayList<CustomThread>();

    //Key de them moi thiet bi
    private StringBuffer license = new StringBuffer("KZSEIZGVQQEBCEQN");
    @Inject
    private DeviceSessionHandler sessionHandler;

    @OnOpen
    public void open(Session session) {
        //session.setMaxIdleTimeout(5000);
        System.out.println("OPEN\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        session.setMaxIdleTimeout(5000);
    }

    @OnClose
    public void close(Session session) {
        try {
            sessionHandler.removeSession(session);
            for (CustomThread customThread : listThread) {
                if (customThread.getSession().getId().equals(session.getId())) {
                    listThread.remove(customThread);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("OUT ROI");
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println(error.toString());
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        session.setMaxIdleTimeout(600000);
        try {
            for (CustomThread customThread : listThread) {
                if (customThread.getSession().getId().equals(session.getId())) {
                    customThread.resetCount();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            JsonReader reader = Json.createReader(new StringReader(message));
            JsonObject jsonMessage = reader.readObject();

//          check useradmin
//            if ("admin".equals(jsonMessage.getString("action"))) {
//                Util util = new Util();
//                ArrayList<User> listUser = util.getListUser();
//                for (User user : listUser) {
//                    if (user.getPriority() == 1) {
//                        sessionHandler.setAdmin(user.getId());
//                        break;
//                    }
//                }
//                sessionHandler.sendDeviceToUser(session);
//            }
//          request Add khi moi bat dau ket noi cua thiet bi
            if ("add".equals(jsonMessage.getString("action"))) {
                if (addDevice(jsonMessage, session)) {
                    checkConnect(session, 4);
                }
//                Device device = new Device();
//                device.setName(jsonMessage.getString("name"));
//                //
//                //device.setId(jsonMessage.getInt("id"));
//                //
//                device.setDescription(jsonMessage.getString("description"));
//                device.setType(jsonMessage.getString("type"));
//                device.setStatus("Off");
//                sessionHandler.addDevice(device);
            }

//          xoa thiet bi khoi he thong voi quyen admin
            if ("remove".equals(jsonMessage.getString("action"))) {
                removeDevice(jsonMessage, session);
//                int id = (int) jsonMessage.getInt("id");
//                sessionHandler.removeDevice(id);
            }

//          Doi trang thai cua thiet bi do thiet bi yeu cau thay doi trong database va gui trang thai toi admin
            if ("toggle".equals(jsonMessage.getString("action"))) {
                toggleDevice(jsonMessage, session);
//                int id = (int) jsonMessage.getInt("id");
//                sessionHandler.toggleDevice(id);
            }

//          doi trang thai cua thiet bi do nguoi dung yeu cau den thiet bi do
            if ("changeStatus".equals(jsonMessage.getString("action"))) {
                int id = new Integer(jsonMessage.getString("id"));
                changeStatus(id);
            }

//          Dang nhap doi voi user
            if ("login".equals(jsonMessage.getString("action"))) {
                //
                //
                //
                //
            }

            //sinh key add thiet bi khi admin yeu cau 
            if ("genKey".equals(jsonMessage.getString("action"))) {
                //
                //                                                             
                //
                //                  
            }
            if ("GETTEXT".equals(jsonMessage.getString("action"))) {
                sendFileToDevice(session, jsonMessage.getInt("sizetext"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

//  Add thiet bi khi them moi hoac them session khi la thiet bi cu
    private boolean addDevice(JsonObject jsonMessage, Session session) {
        try {
            int id = sessionHandler.exist(jsonMessage.getString("license"));
            if (getLicense().equals(jsonMessage.getString("license")) && license.length() == 16) {
                Device device = new Device();
                device.setName(jsonMessage.getString("name"));
                device.setDescription(jsonMessage.getString("description"));
                device.setType(jsonMessage.getString("type"));
                device.setKind(jsonMessage.getString("kind"));
                device.setSession(session);
                sessionHandler.addDevice(device, session);
                return true;
            } else if (getLicense().equals(jsonMessage.getString("license")) == false
                    && id > 0) {
                sessionHandler.updateConnectDevice(id, session);
                return true;
            } else {
                sessionHandler.errorAdd(session);
                try {
                    session.close();
                } catch (IOException ex) {
                    Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

//  Xoa thiet bi khoi he thong
    private void removeDevice(JsonObject jsonMessage, Session session) {
        int id = (int) jsonMessage.getInt("id");
        sessionHandler.removeDevice(id, session);
    }

//  Gui file toi thiet bi
    public void sendFileToDevice(Session session, int sizeOfString) {
        BufferedReader bufferReader;
        try {
            File file = new File("C:\\Users\\ngoc\\Documents\\NetBeansProjects\\JavaApplication6\\text.txt");
            bufferReader = new BufferedReader(new FileReader(file));
//            int a;
//            int dem = 0;
//            String str = "";
//            int indByte = 0;
//            while ((a = bufferReader.read()) != -1 || dem < sizeOfString ) {
//                str += (char)a;
//                indByte++;
//                dem++;
//                if (dem == sizeOfString || a == -1) {
//                    JsonProvider provider = JsonProvider.provider();
//                    JsonObject fileMessage = provider.createObjectBuilder()
//                            .add("action", "TEXT")
//                            .add("sizetext", dem)
//                            .add("text", str)
//                            .add("file", file.getName())
//                            .add("byte", indByte - dem)
//                            .build();
//
//                    sessionHandler.sendFileToDevice(session, fileMessage);
//                    
//                    dem = 0;
//                    str = "";
//                }
//            }

            ArrayList<Character> listChar = new ArrayList<>();
            int a = 0;
            String str = "";
            while ((a = bufferReader.read()) != -1) {
                listChar.add(new Character((char) a));
            }
            
            int dem = 1;
            int indByte = 0;
            while (dem <= sizeOfString && indByte < listChar.size()) {
                str += (char) listChar.get(indByte);
                if (dem == sizeOfString || indByte == listChar.size()-1) {
                    JsonProvider provider = JsonProvider.provider();
                    JsonObject fileMessage = provider.createObjectBuilder()
                            .add("action", "TEXT")
                            .add("sizetext", dem)
                            .add("text", str)
                            .add("file", file.getName())
                            .add("byte", indByte - dem)
                            .build();

                    sessionHandler.sendFileToDevice(session, fileMessage);
                    dem = 1;
                    str = "";
                    dem=0;
                }
                dem++;
                indByte++;
            }

        } catch (IOException e) {

        }

    }

//  Doi trang thai cua thiet bi trong database va gui trang thai toi admin
    private void toggleDevice(JsonObject jsonMessage, Session session) {
        int id = (int) jsonMessage.getInt("id");
        sessionHandler.toggleDevice(id, session);
    }

//  yeu cau thiet bi thay doi trang thai
    private void changeStatus(int id) {
        Session session = new Util().getDevice(id).getSession();
        sessionHandler.changeStatus(session);
    }

// Gui lien tuc request check ket noi doi voi thiet bi
    private void checkConnect(Session session, int maxCount) {
        //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        CustomThread checkThread = new CustomThread(sessionHandler, session, 1, maxCount);
        checkThread.start();
        listThread.add(checkThread);
        //session.setMaxIdleTimeout(3000);
    }

//  lay ra key hien tai
    public String getLicense() {
        return license.toString();
    }

//  Xoa key(khong cho them moi thiet bi)
    public void resetLicense() {
        this.license = new StringBuffer();
    }

//  Sinh key moi de them thiet bi khi admin cho phep
    public void getNewLicense() {
        resetLicense();
        for (int i = 0; i < 16; i++) {
            this.license.append((char) rand(65, 90));
        }
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

//  class luong de tao luong check ket noi voi cac thiet bi
    class CustomThread extends Thread {

        private int countMiss;
        private int maxCount;
        private DeviceSessionHandler sessionHandler;
        private Session session;

        public CustomThread(DeviceSessionHandler sessionHandler, Session session, int priority, int count) {
            try {
                this.setPriority(priority);
            } catch (Exception e) {
            }
            this.sessionHandler = sessionHandler;
            this.session = session;
            countMiss = 0;
            maxCount = count;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    countMiss += 1;
                    if (countMiss >= maxCount) {
                        session.close();
                        interrupt();
                        return;
                    }
                    sleep(60000);
                    sessionHandler.getData(session);
                    System.out.println("Gui check");
                } catch (Exception ex) {
                    Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public void resetCount() {
            this.countMiss = 0;
        }

        public Session getSession() {
            return session;
        }
    }
}
