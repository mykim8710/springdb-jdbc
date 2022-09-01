package com.example.jdbc.exception.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CheckedAppTest {

    @Test
    void checked_test() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.callService()).isInstanceOf(Exception.class);
    }

    static class Controller {
        Service service = new Service();

        public void callService() throws SQLException, ConnectException {
            service.bizLogic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void bizLogic() throws SQLException, ConnectException {
            repository.callSQLException();
            networkClient.callConnectionException();
        }
    }

    static class NetworkClient {
        public void callConnectionException() throws ConnectException {
            throw new ConnectException("Connect Exception 발생, 연결실패");
        }
    }

    static class Repository {
        public void callSQLException() throws SQLException {
            throw new SQLException("SQL Exception 발생");
        }
    }

}
