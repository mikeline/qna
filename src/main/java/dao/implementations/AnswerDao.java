package dao.implementations;

import dao.interfaces.Dao;
import models.Answer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateSessionFactoryUtil;

import java.util.List;

public class AnswerDao implements Dao<Answer> {

    @Override
    public Answer findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Answer.class, id);
    }

    @Override
    public void save(Answer answer) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(answer);
        tx1.commit();
        session.close();
    }

    @Override
    public void update(Answer answer) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(answer);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Answer answer) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(answer);
        tx1.commit();
        session.close();
    }

    @Override
    public List<Answer> findAll() {
        List<Answer> answers = (List<Answer>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Topic").list();
        return answers;
    }
}
