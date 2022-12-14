package com.example.jdbc.repository;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.exception.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제 해결
 * 체크 예외를 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLException 제거
 */

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4_1 implements MemberRepository {
    private final DataSource dataSource;

    // 회원저장
    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO member(MEMBER_ID, MONEY) VALUES(?, ?)";

        Connection con = null;
        PreparedStatement psmt = null;

        try {
            con = getConnection();
            psmt = con.prepareStatement(sql);
            psmt.setString(1, member.getMemberId());
            psmt.setInt(2, member.getMoney());
            psmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, psmt, null);
        }
    }

    // 회원조회
    @Override
    public Member findById(String memberId)  {
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
            throw new MyDbException(e);
        } finally {
            close(con, psmt, rs);
        }
    }

    // 회원수정
    @Override
    public void update(String memberId, int money) {
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
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    // 회원삭제
    @Override
    public void delete(String memberId) {
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
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    // Get Connection from DataSource
    private Connection getConnection() {
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {}, class = {}", con, con.getClass());
        return con;
    }

    // 리소스 정리
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, dataSource); // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
    }
}
