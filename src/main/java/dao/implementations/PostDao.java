package dao.implementations;

import dao.interfaces.Dao;
import models.Post;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateSessionFactoryUtil;

import java.util.List;

public class PostDao implements Dao<Post> {

    @Override
    public Post findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Post.class, id);
    }

    @Override
    public void save(Post post) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(post);
        tx1.commit();
        session.close();
    }

    @Override
    public void update(Post post) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(post);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Post post) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(post);
        tx1.commit();
        session.close();
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = (List<Post>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Post").list();
        return posts;
    }
}
