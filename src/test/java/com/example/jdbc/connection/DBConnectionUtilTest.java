package com.example.jdbc.connection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionUtilTest {

    @Test
    void getConnectionTest(){
        // given // when
        Connection connection = DBConnectionUtil.getConnection();

        // then
        Assertions.assertThat(connection).isNotNull();
    }
}