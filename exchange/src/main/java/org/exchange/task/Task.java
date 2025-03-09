package org.exchange.task;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
@Getter @Setter @Data
public class Task implements Serializable {
    private Integer id;
    private Date date_key;
    private String project;
    private String description;
    private Float hoursSpent;
}
