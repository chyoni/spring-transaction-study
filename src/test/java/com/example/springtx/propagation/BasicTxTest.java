package com.example.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager transactionManager;

    @TestConfiguration
    static class Config {

        /**
         * 트랜잭션 매니저를 직접 만들지 않으면 스프링이 application.yml 파일에 작성된 datasource 설정값을 가지고
         * 자동으로 트랜잭션 매니저를 만들어주지만, 이렇게 직접 빈으로 등록하면 이게 트랜잭션 매니저로 사용된다.
         * application.yml 파일에 datasource 관련 설정값이 없을 땐 에러가 나겠지. 근데 여기는 테스트 코드이고
         * 테스트 레벨에서는 H2 데이터베이스를 사용할 경우 메모리상에서 데이터베이스 테스트를 할 수 있다.
         * */
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("tx start");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("tx commit start");
        transactionManager.commit(status);
        log.info("tx commit end");
    }

    @Test
    void rollback() {
        log.info("tx start");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("tx rollback start");
        transactionManager.rollback(status);
        log.info("tx rollback end");
    }

    @Test
    void double_commit() {
        log.info("tx1 start");
        TransactionStatus tx1 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("tx1 commit start");
        transactionManager.commit(tx1);

        log.info("tx2 start");
        TransactionStatus tx2 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("tx2 commit start");
        transactionManager.commit(tx2);


    }
}
