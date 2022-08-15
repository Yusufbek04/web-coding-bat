package org.example.condigbat.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.condigbat.entity.template.AbsLongEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "cases",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"args", "problem_id"}))
public class Case extends AbsLongEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String args;

    @Column(nullable = false, columnDefinition = "text")
    private String expected;

    @Column(nullable = false)
    private Boolean visible;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Problem problem;
}