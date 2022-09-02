package com.example.jdbc.repository;

import com.example.jdbc.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static com.example.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryExV1Test {
    MemberRepositoryV1 memberRepositoryV1;

    @BeforeEach // 각 테스트가 실헹전에 호출
    void beforeEach() {
        // DriverManagerDataSource - 항상 새로운 커넥션을 획득
        // DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);


        // 커넥션 풀링: HikariProxyConnection -> JdbcConnection
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        memberRepositoryV1 = new MemberRepositoryV1(dataSource);    // MemberRepositoryV1은 DataSource 의존관계 주입이 필요하다.
    }

    @Test
    void crudTest() throws SQLException, InterruptedException {
        // save, c
        Member member = new Member("memberV1", 1000);
        memberRepositoryV1.save(member);

        // findById, r
        Member findMember = memberRepositoryV1.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(findMember).isEqualTo(member);

        // update, u
        memberRepositoryV1.update(member.getMemberId(), 2000);
        Member updateMember = memberRepositoryV1.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(2000);

        // delete, d
        memberRepositoryV1.delete(member.getMemberId());
        assertThatThrownBy(() -> memberRepositoryV1.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
        //Assertions.assertThrows(NoSuchElementException.class, () -> memberRepositoryV0.findById(member.getMemberId()));

        Thread.sleep(1000);
    }

}