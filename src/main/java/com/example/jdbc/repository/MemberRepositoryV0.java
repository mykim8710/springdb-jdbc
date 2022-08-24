package com.example.jdbc.repository;

import com.example.jdbc.connection.DBConnectionUtil;
import com.example.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(MEMBER_ID, MONEY) VALUES(?, ?)";

        Connection connection = null;   // 연결객체
        PreparedStatement preparedStatement = null; // db에 쿼리를 날리는 객체

        try {
            connection = getConnection();    // DBConnectionUtil 를 통해서 데이터베이스 커넥션을 획득
            preparedStatement = connection.prepareStatement(sql); // 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비
            preparedStatement.setString(1, member.getMemberId());   // SQL의 첫번째 ? 에 값을 지정, String
            preparedStatement.setInt(2, member.getMoney());  // SQL의 두번째 ? 에 값을 지정, int

            preparedStatement.executeUpdate();  // Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달, int를 반환하는데 영향받은 DB row 수를 반환

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();    // db connection 획득
    }

    // 리소스 정리 !!
    // 쿼리를 실행하고 나면 리소스를 정리해야 한다.
    // 여기서는 Connection , PreparedStatement 를 사용했다.
    // 리소스를 정리할 때는 항상 역순으로 해야한다.
    // Connection을 먼저 획득하고 Connection을 통해 PreparedStatement를 만들었기 때문에
    // 리소스를 반환할 때는 PreparedStatement 를 먼저 종료하고, 그 다음에 Connection 을 종료하면 된다.
    // 리소스 정리는 꼭! 해주어야 한다.
    // 따라서 예외가 발생하든, 하지 않든 항상 수행되어야 하므로 finally 구문에 주의해서 작성해야한다.
    // 만약 이 부분을 놓치게 되면 커넥션이 끊어지지 않고 계속 유지되는 문제가 발생할 수 있다.
    // 이런 것을 리소스 누수라고 하는데, 결과적으로 커넥션 부족으로 장애가 발생할 수 있다.

    private void close(Connection connection, Statement statement, ResultSet resultSet) {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }
    }
}
