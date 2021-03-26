package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider CryptographyProvider;

    //Method to authenticate a user while signing in, An access token will be generated if the user name
    //and password are correct, which will be required for further operations
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = CryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());

            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthTokenEntity);

            userDao.updateUser(userEntity);

            return userAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    //Method to check if access token provided is  a valid one
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity checkUser(String authorization) throws SignOutRestrictedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        else {
            //userDao.updateUserToken(userAuthTokenEntity);
            userAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
            userDao.updateUserToken(userAuthTokenEntity);
            return userAuthTokenEntity.getUser();

        }


    }

    //Method to Get details of user on passing the uuid once the access token is verified
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(String uuid, String authorization) throws AuthenticationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
        else {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null) {
                throw new AuthenticationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            } else {
                UserEntity userEntity = userDao.getUserByUuid(uuid);
                if (userEntity == null)
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                else
                    return userEntity;
            }
        }
    }

  // Method to delete a user , once the access token is verified. Only an admin can delete any user
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(String userUuid, String authorization) throws AuthenticationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
        else {

            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthenticationFailedException("ATHR-002", "User is signed out");
                else {

                    String role = userAuthTokenEntity.getUser().getRole();
                    if(!role.equalsIgnoreCase("admin"))
                        throw new AuthenticationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
                    else
                    {
                        UserEntity userEntity = userDao.getUserByUuid(userUuid);
                        if (userEntity == null)
                            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                        else
                            userDao.deleteUser(userEntity);
                    }

                }


        }

    }


}