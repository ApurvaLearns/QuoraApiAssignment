package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminBusinessService {

    @Autowired
    UserDao userDao;

    //This  method is called when a user signs up
    public UserEntity createUser(UserEntity userEntity) {
        return userDao.createUser(userEntity);
    }
}
