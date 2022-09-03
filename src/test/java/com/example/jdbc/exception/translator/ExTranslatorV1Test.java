package com.example.jdbc.exception.translator;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.exception.MyDbException;
import com.example.jdbc.repository.exception.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.example.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {
    FooRepository fooRepository;
    FooService fooService;

    @Test
    void duplicateKeySaveTest() {
        String memberId = "myId";
        fooService.create(memberId);
        fooService.create(memberId);
    }


    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        fooRepository = new FooRepository(dataSource);
        fooService = new FooService(fooRepository);
    }

    @RequiredArgsConstructor
    static class FooService {
        private final FooRepository fooRepository;

        public void create(String memberId) {
            Member member = new Member(memberId, 0);

            try {
                fooRepository.save(member);
                log.info("saveId={}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                log.info("retryId={}", retryId);
                fooRepository.save(new Member(retryId, 0));
            } catch (MyDbException e) {
                log.info("데이터 접근 계층 예외", e);
                throw e;
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    static class FooRepository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "INSERT INTO member(MEMBER_ID, MONEY) VALUES(?, ?)";

            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                if(e.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(e);
                }

                throw new MyDbException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }


}
