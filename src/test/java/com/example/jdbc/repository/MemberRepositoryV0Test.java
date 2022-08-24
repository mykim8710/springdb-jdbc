package com.example.jdbc.repository;

import com.example.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryV0Test {
    MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    void crudTest() throws SQLException {
        Member member = new Member("memberV0", 1000);
        memberRepositoryV0.save(member);
    }

}