package services.implementations;

import dao.implementations.CommentDao;
import models.Comment;
import services.interfaces.Service;

import java.util.List;

public class CommentService implements Service<Comment> {

    private CommentDao commentDao = new CommentDao();

    public CommentService() {
    }

    @Override
    public Comment find(int id) {
        return commentDao.findById(id);
    }

    @Override
    public void save(Comment comment) {
        commentDao.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentDao.delete(comment);
    }

    @Override
    public void update(Comment comment) {
        commentDao.update(comment);
    }

    @Override
    public List<Comment> findAll() {
        return commentDao.findAll();
    }
}
