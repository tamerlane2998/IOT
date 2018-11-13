/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.example.model.Device;
import org.example.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author Phan
 */
//Thao tac voi db
public class Util {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    //Tra ve cac thiet bi trong db
    public ArrayList<Device> getListDevice() {
        try {
            Configuration configuration = new Configuration().configure();
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                    applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
            return (ArrayList<Device>) sessionFactory.openSession().createCriteria(Device.class).list();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return new ArrayList<Device>();
    }

    //tra ve thiet bi theo id
    public Device getDevice(int id) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        try {
            return (Device) session.get(Device.class, id);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
        return null;
    }

    public Device getLastDevice() {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        try {
            List list = session.createCriteria(Device.class).list();
            return (Device) list.get(list.size()-1);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        } finally {
            session.close();
        }
    }

    //them 1 thiet bi trong db
    public void addDevice(Device device) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Integer ID = null;
        try {
            tx = session.beginTransaction();
            ID = (Integer) session.save(device);
            tx.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
    }

    //xao 1 thiet bi khoi db
    public void removeDevice(int id) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Device device = (Device) session.get(Device.class, id);
            session.delete(device);
            tx.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
    }

    //update lai du lieu cua thiet bi trong db
    public void updateDevice(int id, String data) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Device device = (Device) session.load(Device.class, id);
            String[] request = data.split(" ", 2);
            if (request[0].equals("toggle")) {
                device.setStatus(request[1]);
            }
            if (request[0].equals("description")) {
                device.setDescription(request[1]);
            }
            if (request[0].equals("license")) {
                device.setLicense(request[1]);
            }
            session.update(device);
            tx.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
    }
//    
//    //update lai session dang thao tac voi thiet bi do
//    public void updateSession(int id, String socketSession){
//        Configuration configuration = new Configuration().configure();
//        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
//                applySettings(configuration.getProperties());
//        sessionFactory = configuration.buildSessionFactory(builder.build());
//        Session session = sessionFactory.openSession();
//        Transaction tx = null;
//        try {
//            tx = session.beginTransaction();
//            Device device = (Device) session.load(Device.class, id);
//            device.setSession(socketSession);
//            session.update(device);
//            tx.commit();
//        } catch (Exception e) {
//            System.out.println(e);
//        } finally {
//            session.close();
//        }
//    }
//    
    //lay ra user theo id

    public User getUser(int id) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        try {
            return (User) session.get(User.class, id);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
        return null;
    }

    //them user va db
    public void addUser(User user) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
    }

    //xoa user khoi db
    public void removeUser(int id) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            User user = (User) session.get(User.class, id);
            session.delete(user);
            tx.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
    }
}
