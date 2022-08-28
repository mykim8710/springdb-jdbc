package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepository;

    // 계좌이체 비지니스 로직 : formId 의 회원을 조회해서 toId 의 회원에게 money 만큼의 돈을 계좌이체 하는 로직이다.
    // fromId 회원의 돈을 money 만큼 감소한다. UPDATE SQL 실행
    // toId 회원의 돈을 money 만큼 증가한다. UPDATE SQL 실행
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜젝션 시작
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);   // 예외 상황을 테스트해보기 위해 toId 가 "ex" 인 경우 예외를 발생한다.
        memberRepository.update(toId, toMember.getMoney() + money);

        // 커밋 or 롤백 : 트랜젝션 종료
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
