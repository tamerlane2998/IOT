/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.websocket;

import java.util.ArrayList;
import org.example.hibernate.Util;
import org.example.model.Device;

/**
 *
 * @author ngoc
 */
public class NewClass {
    public static void main(String[] args) {
        Util util = new Util();
        DeviceSessionHandler dvhl = new DeviceSessionHandler();
        String licenseCode = dvhl.test();
       
        
    }
}
