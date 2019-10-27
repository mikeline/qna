package services.implementations;

import dao.implementations.UserDao;
import models.User;
import services.interfaces.Service;

import java.util.List;

public class UserService implements Service<User> {

    private UserDao userDao = new UserDao();

    public UserService() {
    }

    @Override
    public User find(int id) {
        return userDao.findById(id);
    }

    @Override
    public void save(User user) {
        userDao.save(user);
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }
}