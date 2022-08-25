package com.example.jdbc.repository;

import com.example.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    void crudTest() throws SQLException {
        // save, c
        Member member = new Member("memberV1", 1000);
        memberRepositoryV0.save(member);

        // findById, r
        Member findMember = memberRepositoryV0.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(findMember).isEqualTo(member);

        // update, u
        memberRepositoryV0.update(member.getMemberId(), 2000);
        Member updateMember = memberRepositoryV0.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(2000);

        // delete, d
        memberRepositoryV0.delete(member.getMemberId());
        assertThatThrownBy(() -> memberRepositoryV0.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
        //Assertions.assertThrows(NoSuchElementException.class, () -> memberRepositoryV0.findById(member.getMemberId()));
    }

}