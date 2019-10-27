package services.implementations;

import dao.implementations.NodeDao;
import models.Node;
import services.interfaces.Service;

import java.util.List;

public class NodeService implements Service<Node> {

    private NodeDao nodeDao = new NodeDao();

    public NodeService() {
    }

    @Override
    public Node find(int id) {
        return nodeDao.findById(id);
    }

    @Override
    public void save(Node node) {
        nodeDao.save(node);
    }

    @Override
    public void delete(Node node) {
        nodeDao.delete(node);
    }

    @Override
    public void update(Node node) {
        nodeDao.update(node);
    }

    @Override
    public List<Node> findAll() {
        return nodeDao.findAll();
    }
}
