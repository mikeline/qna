package services.implementations;

import dao.implementations.TopicDao;
import models.Topic;
import services.interfaces.Service;

import java.util.List;

public class TopicService implements Service<Topic> {

    private TopicDao topicDao = new TopicDao();

    public TopicService() {
    }

    @Override
    public Topic find(int id) {
        return topicDao.findById(id);
    }

    @Override
    public void save(Topic topic) {
        topicDao.save(topic);
    }

    @Override
    public void delete(Topic topic) {
        topicDao.delete(topic);
    }

    @Override
    public void update(Topic topic) {
        topicDao.update(topic);
    }

    @Override
    public List<Topic> findAll() {
        return topicDao.findAll();
    }
}
