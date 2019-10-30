package dao.implementations;

import dao.interfaces.Dao;
import models.Post;
import models.Topic;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateSessionFactoryUtil;

import java.util.List;

public class TopicDao implements Dao<Topic> {

    @Override
    public Topic findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Topic.class, id);
    }

    @Override
    public void save(Topic topic) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(topic);
        tx1.commit();
        session.close();
    }

    @Override
    public void update(Topic topic) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(topic);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Topic topic) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(topic);
        tx1.commit();
        session.close();
    }

    @Override
    public List<Topic> findAll() {
        List<Topic> topics = (List<Topic>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Topic").list();
        return topics;
    }
}
