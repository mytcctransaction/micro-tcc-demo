package org.micro.tcc.demo.common.db.mapper;

import org.micro.tcc.demo.common.db.domain.Demo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseDemoMapper {

    @Insert("insert into tcc_demo( content, group_id, create_time,app_name) values(#{content}, #{groupId}, #{createTime},#{appName})")
    void save(Demo demo);

    @Delete("delete from tcc_demo where id=#{id}")
    void deleteByKId(Long id);
}
