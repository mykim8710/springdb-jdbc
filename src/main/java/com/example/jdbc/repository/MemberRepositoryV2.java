package com.example.jdbc.repository;

import com.example.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV2 {
    private final DataSource dataSource;

    // 회원저장
    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(MEMBER_ID, MONEY) VALUES(?, ?)";

        Connection con = null;         // 연결객체
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

    // 회원조회 : 커넥션 유지가 필요한 메서드
    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE MEMBER_ID = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();   // select Query, 실행 후 select 쿼리의 결과를 반환

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
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            // JdbcUtils.closeConnection(con); : connection은 여기서 닫지 않는다.
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

    // 회원수정 : 커넥션 유지가 필요한 메서드
    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET MONEY = ? WHERE MEMBER_ID=?";

        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
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

    // Get Connection from DataSource
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection = {}, class = {}", con, con.getClass());
        return con;    // db connection 획득
    }

    // 리소스 정리 !!
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
}
