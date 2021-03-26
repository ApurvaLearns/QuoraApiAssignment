package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;

@Service
public class QuestionService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

//Method to create a Question once the access token is validated and access token is signed in
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            else {
                questionEntity.setUser(userAuthTokenEntity.getUser());
                return questionDao.createQuestion(questionEntity);
            }

        }

    }

    //Method to get all Question once the access token is validated and access token is signed in
    @Transactional(propagation = Propagation.REQUIRED)
    public ArrayList<QuestionEntity> getDetails(String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            else {
                return questionDao.getAllQuestion();
            }

        }
    }


    //Method to edit a question when an access token is validated and user is signed in . Only question owner
    //can edit a question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(String questionId, String authorization, String content) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else
        {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            else
            {
                QuestionEntity questionEntity = questionDao.getQuestion(questionId);
                if(questionEntity == null)
                    throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
                else
                {
                    if(!(userAuthTokenEntity.getUser() == questionEntity.getUser()))
                    {
                        throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
                    }
                    else {
                        questionEntity.setContent(content);
                        questionEntity.setDate(ZonedDateTime.now());
                        return questionDao.updateQuestion(questionEntity);
                    }
                }

            }
        }
    }

//Method to delete a Quetsion . Only the question owner or admin delete a question
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String questionId, String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else
        {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete a question");
            else
            {
                QuestionEntity questionEntity = questionDao.getQuestion(questionId);
                if(questionEntity == null)
                    throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
                else
                    {
                        if(!(userAuthTokenEntity.getUser() == questionEntity.getUser()))
                        {
                            String role = userAuthTokenEntity.getUser().getRole();
                            if(!role.equalsIgnoreCase("admin"))
                            {
                                throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
                            }
                          else
                            {
                                questionDao.deleteQuestion(questionEntity);
                            }

                        }

                        else
                        {
                            questionDao.deleteQuestion(questionEntity);
                        }

                }

                }
            }
    }


    //Method to get all Questions posted by a user

    @Transactional(propagation = Propagation.REQUIRED)
    public ArrayList<QuestionEntity> getAllQuestionsByUser(String userid,String authorization) throws AuthorizationFailedException, UserNotFoundException {

        System.out.println("UserId"+userid);
        System.out.println("Authorization"+authorization);

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else
        {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
            else
            {
                UserEntity userEntity = userDao.getUserByUuid(userid);
                if(userEntity == null)
                    throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
                 else
                {

                  //  System.out.println("userId  "+userEntity.getId());

                     return questionDao.getAllQuestionsByUser(userEntity);
                }

            }
    }
    }

}
