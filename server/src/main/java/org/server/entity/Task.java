package org.server.entity;

import lombok.Data;

import java.sql.Date;

@Data
public class Task {
    private Integer id;
    private Date dateKey;
    private String project;
    private String description;
    private Float hoursSpent;
}
