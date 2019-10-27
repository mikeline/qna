package services.implementations;

import dao.implementations.AnswerDao;
import models.Answer;
import services.interfaces.Service;

import java.util.List;

public class AnswerService implements Service<Answer> {

    private AnswerDao answerDao = new AnswerDao();

    public AnswerService() {
    }

    @Override
    public Answer find(int id) {
        return answerDao.findById(id);
    }

    @Override
    public void save(Answer answer) {
        answerDao.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerDao.delete(answer);
    }

    @Override
    public void update(Answer answer) {
        answerDao.update(answer);
    }

    @Override
    public List<Answer> findAll() {
        return answerDao.findAll();
    }
}
