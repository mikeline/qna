package services.implementations;

import dao.implementations.PostDao;
import models.Post;
import services.interfaces.Service;

import java.util.List;

public class PostService implements Service<Post> {

    private PostDao postDao = new PostDao();

    public PostService() {
    }

    @Override
    public Post find(int id) {
        return postDao.findById(id);
    }

    @Override
    public void save(Post post) {
        postDao.save(post);
    }

    @Override
    public void delete(Post post) {
        postDao.delete(post);
    }

    @Override
    public void update(Post post) {
        postDao.update(post);
    }

    @Override
    public List<Post> findAll() {
        return postDao.findAll();
    }
}
