package com.example.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unChecked_test() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    @Test
    void printException() {
        Controller controller = new Controller();

        try {
            controller.request();
        } catch (Exception e) {
//            e.printStackTrace(); // 좋지않은 방법
            log.info("exception", e);
        }
    }

    static class Controller {
        Service service = new Service();

        public void request() {
            service.bizLogic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void bizLogic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결실패");
        }
    }

    static class Repository {
        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }

        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }
    }


    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }
    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {}
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
