package com.example.jdbc.connection;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ConnectionConstEnums {
    CONNECTION_URL("jdbc:h2:tcp://localhost/~/test"),
    CONNECTION_USERNAME("sa"),
    CONNECTION_PASSWORD(""),;

    ConnectionConstEnums(String contents) {
        this.contents = contents;
    }

    private String contents;

}
