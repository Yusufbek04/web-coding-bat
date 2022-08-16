package org.example.condigbat.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.condigbat.entity.template.AbsTitleIntegerEntity;

import javax.persistence.*;

@Entity
@Setter
@Getter
@ToString
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title"}))
public class Language extends AbsTitleIntegerEntity {

    @Column(unique = true, nullable = false)
    private String url;

    @PrePersist
    private void makeUrl(){
        this.url = getTitle();
    }
}
