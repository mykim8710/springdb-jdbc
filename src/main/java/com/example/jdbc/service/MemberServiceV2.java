package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            // 트랜젝션 시작
            con.setAutoCommit(false);

            // 순수한 비지니스 로직 시작
            bizLogic(con, fromId, toId, money);

            // 트랜젝션 종료 : 성공시 커밋
            con.commit();
        } catch (Exception e) {
            // 트랜젝션 종료 : 실패시 롤백
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(con);
            // finally {..} 를 사용해서 커넥션을 모두 사용하고 나면 안전하게 종료한다.
            // 그런데 커넥션 풀을 사용하면 con.close() 를 호출 했을 때 커넥션이 종료되는 것이 아니라 풀에 반납된다.
            // 현재 수동 커밋 모드로 동작하기 때문에 풀에 돌려주기 전에 기본 값인 자동 커밋 모드로 변경하는 것이 안전하다.
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);   // 예외 상황을 테스트해보기 위해 toId 가 "ex" 인 경우 예외를 발생한다.
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void release(Connection con) {
        if(con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.error("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
