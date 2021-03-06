package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

/*
This model class maps to the answer table in DB
 */

@Entity
@Table(name = "Answer", schema = "public")
@NamedQueries({

        @NamedQuery(name = "getAnswerByUuid", query = "select u from AnswerEntity u where u.uuid=:uuid"),
        @NamedQuery(name = "getAnswersByQuestionId", query = "select u from AnswerEntity u where u.question=:question"),

})
public class AnswerEntity {
    //id column is primary key
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //uuid column is universal unique identity field
    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    //ans column will contain the answer
    @Column(name = "ans")
    @Size(max = 255)
    private String answer;

    //Modified & Added by @github.com/vetrivel-muthusamy: date column will contain the date
    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    //quertion_id column will contain the question ID.
    //Modified by @github.com/vetrivel-muthusamy
    @ManyToOne
    @JoinColumn(name = "question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private QuestionEntitiy question;

    //Modified by @github.com/vetrivel-muthusamy
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    //Added by @github.com/vetrivel-muthusamy
    public QuestionEntitiy getQuestion() {
        return question;
    }

    //Added by @github.com/vetrivel-muthusamy
    public void setQuestion(QuestionEntitiy question) {
        this.question = question;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    //Added by @github.com/vetrivel-muthusamy
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
