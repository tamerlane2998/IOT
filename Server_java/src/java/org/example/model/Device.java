/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.model;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.websocket.Session;

/**
 *
 * @author V khuat
 */
@Entity
@Table(name = "device")
public class Device {

    @Id
    private int id;
    private int user_id;
    private String name;
    private String status;
    private String type;
    private String description;
    private String license;
    private String kind;
    private Session session;
    
    private Set users;

    public Set getUsers() {
        return users;
    }

    public void setUsers(Set users) {
        this.users = users;
    }   
          
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Device(int id, String name, String status, String type, String description, String licnese, String kind) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.type = type;
        this.description = description;
        this.license = licnese;
        this.kind = kind;
    }

    public Device() {
    }

    public int getId() {
        return id;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String str = id + "," + name + "," + status + "," + type + "," + description + "," + license + "," + kind;
        return str;
    }

}
