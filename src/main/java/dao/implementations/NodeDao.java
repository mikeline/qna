package dao.implementations;

import dao.interfaces.Dao;
import models.Node;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateSessionFactoryUtil;

import java.util.List;

public class NodeDao implements Dao<Node> {

    @Override
    public Node findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Node.class, id);
    }

    @Override
    public void save(Node node) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(node);
        tx1.commit();
        session.close();
    }

    @Override
    public void update(Node node) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(node);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Node node) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(node);
        tx1.commit();
        session.close();
    }

    @Override
    public List<Node> findAll() {
        List<Node> nodes = (List<Node>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Node").list();
        return nodes;
    }
}
