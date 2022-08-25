package com.example.jdbc.repository;

import com.example.jdbc.connection.DBConnectionUtil;
import com.example.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV0 {
    // 회원저장
    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(MEMBER_ID, MONEY) VALUES(?, ?)";

        Connection con = null;   // 연결객체
        PreparedStatement psmt = null; // db에 쿼리를 날리는 객체

        try {
            con = getConnection();    // DBConnectionUtil 를 통해서 데이터베이스 커넥션을 획득
            psmt = con.prepareStatement(sql); // 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비
            psmt.setString(1, member.getMemberId());   // SQL의 첫번째 ? 에 값을 지정, String
            psmt.setInt(2, member.getMoney());  // SQL의 두번째 ? 에 값을 지정, int

            psmt.executeUpdate();  // Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달, int를 반환하는데 영향받은 DB row 수를 반환

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, psmt, null);
        }
    }

    // 회원조회
    public Member findById(String memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE MEMBER_ID = ?";

        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            psmt = con.prepareStatement(sql);
            psmt.setString(1, memberId);

            rs = psmt.executeQuery();   // select Query, 실행 후 select 쿼리의 결과를 반환

            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("MEMBER_ID"));
                member.setMoney(rs.getInt("MONEY"));

                return member;
            } else {
                throw new NoSuchElementException("member not found, memberId = " +memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, psmt, rs);
        }
    }

    // 회원수정
    public void update(String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET MONEY = ? WHERE MEMBER_ID=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 회원삭제
    public void delete(String memberId) throws SQLException {
        String sql = "DELETE FROM member WHERE MEMBER_ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
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

    // PreparedStatement 는 Statement 의 자식 타입인데, ? 를 통한 파라미터 바인딩을 가능하게 해준다.
    // 참고로 SQL Injection 공격을 예방하려면 PreparedStatement 를 통한 파라미터 바인딩 방식을 사용해야 한다.

    private void close(Connection con, Statement stmt, ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }
    }
}
