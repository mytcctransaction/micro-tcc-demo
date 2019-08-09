package org.micro.tcc.demo.common.db.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Demo {
    private Long id;
    private String content;
    private String groupId;
    private Date createTime;
    private String appName;

}
