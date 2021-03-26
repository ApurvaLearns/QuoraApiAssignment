package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To create a quetsion
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {

        entityManager.persist(questionEntity);
        return questionEntity;
    }

     //To get all question from DB
    public ArrayList<QuestionEntity> getAllQuestion() {
        try {
            return (ArrayList<QuestionEntity>) entityManager.createNamedQuery("getAllquestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestion(String questionId) {

        try {
            return entityManager.createNamedQuery("getQuestion", QuestionEntity.class).setParameter("uuid",questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity updateQuestion(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
        return questionEntity;
    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    public ArrayList<QuestionEntity> getAllQuestionsByUser(UserEntity userEntity) {
         return (ArrayList<QuestionEntity>) entityManager.createNamedQuery("getQuestionByUser",QuestionEntity.class).setParameter("user",userEntity).getResultList();

    }
}
