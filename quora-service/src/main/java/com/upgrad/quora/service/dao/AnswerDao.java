package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To create an answer
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {

        entityManager.persist(answerEntity);
        return answerEntity;
    }


    //To fetch the details from the DB for an answerid
    public AnswerEntity getAnswer(String answerId) {

        try {
            return entityManager.createNamedQuery("getAnswer",AnswerEntity.class).setParameter("uuid",answerId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //To update an answer
    public AnswerEntity editAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    //To delete an answer in DB
    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

     //To get an ArrayList of AnswerEntity
    public ArrayList<AnswerEntity> getAllAnswers(QuestionEntity questionEntity) {

        return (ArrayList<AnswerEntity>) entityManager.createNamedQuery("getAllAnswers",AnswerEntity.class).setParameter("question",questionEntity).getResultList();
    }
}
