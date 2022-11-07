package com.nli.probation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
@Entity
@Table(name = "task")
public class TaskEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "estimated_time")
    private double estimatedTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignee")
    private UserAccountEntity userAccountEntity;

    @Column(name = "actual_time")
    private double actualTime;

    @Column(name = "status")
    private int status;
}
