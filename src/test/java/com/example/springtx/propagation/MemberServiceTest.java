package com.example.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService @Transactional OFF
     * memberRepository @Transactional ON
     * logRepository @Transactional ON
     * */
    @Test
    void outerTxOff_success() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.findByUsername(username).isPresent()).isTrue();
        assertThat(logRepository.findByMessage(username).isPresent()).isTrue();
    }

    /**
     * memberService @Transactional OFF
     * memberRepository @Transactional ON
     * logRepository @Transactional ON (RuntimeException)
     * */
    @Test
    void outerTxOff_fail() {
        String username = "로그예외_outerTxOff_fail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findByUsername(username).isPresent()).isTrue();
        assertThat(logRepository.findByMessage(username).isEmpty()).isTrue();
    }

    /**
     * memberService @Transactional ON
     * memberRepository @Transactional OFF
     * logRepository @Transactional OFF
     * */
    @Test
    void singleTx() {
        String username = "singleTx";

        memberService.joinV1(username);

        assertThat(memberRepository.findByUsername(username).isPresent()).isTrue();
        assertThat(logRepository.findByMessage(username).isPresent()).isTrue();
    }

    /**
     * memberService @Transactional ON
     * memberRepository @Transactional ON
     * logRepository @Transactional ON
     * */
    @Test
    void outerTxOn_success() {
        String username = "outerTxOn_success";

        memberService.joinV1(username);

        assertThat(memberRepository.findByUsername(username).isPresent()).isTrue();
        assertThat(logRepository.findByMessage(username).isPresent()).isTrue();
    }

    /**
     * memberService @Transactional ON
     * memberRepository @Transactional ON
     * logRepository @Transactional ON (RuntimeException)
     * */
    @Test
    void outerTxOn_fail() {
        String username = "로그예외_outerTxOn_success";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findByUsername(username).isPresent()).isFalse();
        assertThat(logRepository.findByMessage(username).isPresent()).isFalse();
    }

    /**
     * memberService @Transactional ON
     * memberRepository @Transactional ON
     * logRepository @Transactional ON (RuntimeException)
     * */
    @Test
    void recoverException_fail() {
        String username = "로그예외_outerTxOn_fail";

        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        assertThat(memberRepository.findByUsername(username).isPresent()).isFalse();
        assertThat(logRepository.findByMessage(username).isPresent()).isFalse();
    }

    /**
     * memberService @Transactional ON
     * memberRepository @Transactional ON
     * logRepository @Transactional ON(REQUIRED_NEW) (RuntimeException)
     * */
    @Test
    void recoverException_success() {
        String username = "로그예외_outerTxOn_success";

        memberService.joinV2(username);

        assertThat(memberRepository.findByUsername(username).isPresent()).isTrue();
        assertThat(logRepository.findByMessage(username).isPresent()).isFalse();
    }

    /**
     * memberService @Transactional ON (RuntimeException)
     * memberRepository @Transactional ON
     * logRepository @Transactional ON(REQUIRED_NEW)
     * */
    @Test
    void outerRollback_innerCommit() {
        String username = "outerTxOn_success2";

        assertThatThrownBy(() -> memberService.joinV3(username)).isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findByUsername(username).isPresent()).isFalse();
        assertThat(logRepository.findByMessage(username).isPresent()).isTrue();
    }
}