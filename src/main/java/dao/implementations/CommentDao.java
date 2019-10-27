package dao.implementations;

import dao.interfaces.Dao;
import models.Comment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateSessionFactoryUtil;

import java.util.List;

public class CommentDao implements Dao<Comment> {

    @Override
    public Comment findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Comment.class, id);
    }

    @Override
    public void save(Comment comment) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(comment);
        tx1.commit();
        session.close();
    }

    @Override
    public void update(Comment comment) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(comment);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Comment comment) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(comment);
        tx1.commit();
        session.close();
    }

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = (List<Comment>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Comment").list();
        return comments;
    }
}
