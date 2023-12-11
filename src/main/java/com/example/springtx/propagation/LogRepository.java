package com.example.springtx.propagation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Log logMessage) {
        log.info("log save");
        em.persist(logMessage);

        if (logMessage.getMessage().contains("로그예외")) {
            log.info("Error when save the Log");
            throw new RuntimeException("예외 발생");
        }
    }

    public Optional<Log> findByMessage(String message) {
        return em.createQuery("SELECT l FROM Log l WHERE l.message =: message", Log.class)
                .setParameter("message", message)
                .getResultList()
                .stream()
                .findAny();
    }
}
