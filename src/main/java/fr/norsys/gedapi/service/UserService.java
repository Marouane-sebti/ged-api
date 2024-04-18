package fr.norsys.gedapi.service;

import fr.norsys.gedapi.dao.UserDao;
import fr.norsys.gedapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public int registre(User user) {
        return userDao.registre(user);
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public Optional<User> findById(int id) {
        return userDao.findById(id);
    }
}
