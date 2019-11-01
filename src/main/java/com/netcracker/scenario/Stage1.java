package com.netcracker.scenario;

import com.netcracker.config.HibernateConfig;
import com.netcracker.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import com.netcracker.service.repo.UserRepo;
import com.netcracker.utils.QnaRole;

import javax.annotation.Resource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = { HibernateConfig.class },
        loader = AnnotationConfigContextLoader.class)
public class Stage1 {

    @Resource
    private UserRepo userRepo;

    @Test
    public void newUserCreated() {
        User user = new User("Ivan Ivanov", "vanya1287",
                "vanya1287", "example2@gmail.com", QnaRole.ordinary);
        userRepo.save(user);
    }

}
