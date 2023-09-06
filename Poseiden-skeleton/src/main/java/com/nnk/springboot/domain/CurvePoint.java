package com.nnk.springboot.domain;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;


@Entity
@Table(name = "CurvePoint")
public class CurvePoint {
    // TODO: Map columns in data table CURVEPOINT with corresponding java fields --> DONE
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "Id")
    private int id;

    @Column (name = "CurveId")
    @NotBlank(message = "Must not be null")
    private int curveId;

    @Column (name = "asOfDate")
    private String asOfDate;

    @Column (name = "term")
    private double term;

    @Column (name = "value")
    private double value;

    @Column (name = "creationDate")
    private String creationDate;

    public CurvePoint() {}

    public CurvePoint(int i, double v, double v1) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurveId() {
        return curveId;
    }

    public void setCurveId(int curveId) {
        this.curveId = curveId;
    }

    public String getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(String asOfDate) {
        this.asOfDate = asOfDate;
    }

    public double getTerm() {
        return term;
    }

    public void setTerm(double term) {
        this.term = term;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
