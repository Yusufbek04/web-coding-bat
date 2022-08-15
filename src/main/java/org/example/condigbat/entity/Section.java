package org.example.condigbat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.condigbat.entity.template.AbsTitleIntegerEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title", "language_id"}))
public class Section extends AbsTitleIntegerEntity {

    @Column(unique = true, nullable = false)
    private String url;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Short maxRate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Language language;
}
