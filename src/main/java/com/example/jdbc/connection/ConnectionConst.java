package com.example.jdbc.connection;

// 데이터베이스에 접속하는데 필요한 기본 정보를 편리하게 사용할 수 있도록 상수
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
