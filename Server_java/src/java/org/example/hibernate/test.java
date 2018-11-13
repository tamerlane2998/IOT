/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import org.example.model.Device;
import org.example.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author ngoc
 */
public class test {

    public static void main(String[] args) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject deviceMessage1 = provider.createObjectBuilder()
                .add("action", "showDevice")
                .add("id", 1)
                .add("name", "ngoc")
                .build();

        JsonObject deviceMessage2 = provider.createObjectBuilder()
                .add("action", "good")
                .add("id", 1)
                .add("name", "ngoc")
                .build();

//        JsonArray value = Json.createArrayBuilder()
//                .add(deviceMessage1)
//                .add(deviceMessage2)
//                .build();

        
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(deviceMessage1);
        builder.add(deviceMessage2);
        
        JsonArray value = builder.build();
        
        JsonObject listDeviceMessage = provider.createObjectBuilder()
                .add("action", "showDevices")
                .add("listDevice", value)
                .build();
        
        System.out.println(listDeviceMessage.toString());
        

    }
}
