package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;

@Service
public class AnswerService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(String questionId, String authorization, AnswerEntity answerEntity) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
            else {
                QuestionEntity questionEntity = questionDao.getQuestion(questionId);
                if (questionEntity == null)
                    throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
                else {
                    answerEntity.setUser(userAuthTokenEntity.getUser());
                    answerEntity.setQuestion(questionEntity);
                    answerEntity.setDate(ZonedDateTime.now());
                    return answerDao.createAnswer(answerEntity);
                }
            }
        }

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String answerId, String authorization, String content) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
            else {
                AnswerEntity answerEntity = answerDao.getAnswer(answerId);
                if (answerEntity == null)
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                else {
                    if (!(answerEntity.getUser() == userAuthTokenEntity.getUser()))
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
                    else {
                        answerEntity.setDate(ZonedDateTime.now());
                        answerEntity.setAns(content);
                        return answerDao.editAnswer(answerEntity);
                    }
                }
            }

        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(String answerId, String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to Sign in first to delete an answer");
            else {
                AnswerEntity answerEntity = answerDao.getAnswer(answerId);
                if (answerEntity == null)
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                else {
                    if (!(answerEntity.getUser() == userAuthTokenEntity.getUser())) {

                        String role = userAuthTokenEntity.getUser().getRole();
                        if (!role.equalsIgnoreCase("admin")) {
                            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the answer");
                        } else {
                            answerDao.deleteAnswer(answerEntity);
                        }

                    } else
                        answerDao.deleteAnswer(answerEntity);

                }

            }

        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ArrayList<AnswerEntity> getAnswers(String questionId, String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByToken(authorization);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else {
            ZonedDateTime logouttime = userAuthTokenEntity.getLogoutAt();
            if (logouttime != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
            else {
                QuestionEntity questionEntity = questionDao.getQuestion(questionId);
                if (questionEntity == null)
                    throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
                else {

                      return answerDao.getAllAnswers(questionEntity);

                }


            }
        }
    }

}