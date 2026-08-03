package com.tpe.mybatis;

public interface SnowflakeDataMapper {

}

/*
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;

@Mapper
public interface SnowflakeDataMapper {

    @Select("SELECT rule_id, name FROM rules")
    @Options(fetchSize = 1000) // Tells JDBC driver to fetch 1000 records at a time
    Cursor<Rule> streamAllRules();
}
*/
