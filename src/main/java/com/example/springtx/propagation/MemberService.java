package com.example.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    @Transactional
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("=== MemberRepository called ===");
        memberRepository.save(member);
        log.info("=== MemberRepository called End ===");

        log.info("=== LogRepository called ===");
        logRepository.save(logMessage);
        log.info("=== LogRepository called End ===");
    }

    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("=== MemberRepository called ===");
        memberRepository.save(member);
        log.info("=== MemberRepository called End ===");

        log.info("=== LogRepository called ===");
        try {
            logRepository.save(logMessage);
        } catch (RuntimeException e) {
            log.info("log 저장에 실패했습니다. logMessage={}", logMessage.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("=== LogRepository called End ===");
    }

    @Transactional
    public void joinV3(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("=== MemberRepository called ===");
        memberRepository.save(member);
        log.info("=== MemberRepository called End ===");

        log.info("=== LogRepository called ===");
        try {
            logRepository.save(logMessage);
        } catch (RuntimeException e) {
            log.info("log 저장에 실패했습니다. logMessage={}", logMessage.getMessage());
            log.info("정상 흐름 반환");
        }

        log.info("=== LogRepository called End ===");
        throw new RuntimeException("밖에서 롤백 안에서 커밋");
    }
}
