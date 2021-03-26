package com.upgrad.quora.service.entity;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
//This entity would be mapped to table answer in DB
@Entity
@Table(name = "answer", schema = "public")

@NamedQueries({
        @NamedQuery(name = "getAnswer" , query = "select a from AnswerEntity a where a.uuid = :uuid"),
        @NamedQuery(name = "getAllAnswers" , query = "select a from AnswerEntity a where a.question = :question")
})
public class AnswerEntity implements Serializable {

   ////@Id annotation specifies that the corresponding attribute is a primary key
    //@Column annotation specifies that the attribute will be mapped to the column in the DB
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "ans")
    @NotNull
    @Size(max = 500)
    private String ans;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;


    //The 'answer' table is mapped to 'users' table with Many:One mapping
    //One answer can have only one user (owner) but one user can have multiple  answers
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    //The 'answer' table is mapped to 'Question' table with Many:One mapping
    //One answer can belong to  only one Question but one Question can have multiple  answers

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }


}
