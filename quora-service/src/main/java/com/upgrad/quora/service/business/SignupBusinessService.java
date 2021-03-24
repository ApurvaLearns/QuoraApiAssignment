package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        String username=userEntity.getUserName();
        String email=userEntity.getEmail();
        UserEntity user1=userDao.getUserByEmail(email);
        UserEntity user2=userDao.getUserByUserName(username);

        if(user1 != null)
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        else if (user2 != null)
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");

        else {


            String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);
            return userDao.createUser(userEntity);
        }
    }
}
