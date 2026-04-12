package com.example.deal.Service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.deal.dto.ApplicationStatus;
import com.example.deal.dto.Passport;
import com.example.deal.entity.ClientEntity;
import com.example.deal.entity.StatementEntity;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.StatementRepository;
import com.example.deal.service.StatementService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class StatementLockTest {

    @Autowired
    private StatementService statementService;
    @Autowired
    private StatementRepository statementRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private EntityManager entityManager;

    private UUID statementId;

    @BeforeEach
    void setUp() {
        statementId = transactionTemplate.execute(status -> {
            // Очищаем в правильном порядке
            statementRepository.deleteAll();
            clientRepository.deleteAll();
            statementRepository.flush();

            // Формируем клиента
            Passport passport = new Passport();
            passport.setSeries("1234");
            passport.setNumber("123456");

            ClientEntity client = new ClientEntity();
            client.setFirstName("Ivan");
            client.setLastName("Ivanov");
            // Генерируем уникальный email, чтобы не ловить Duplicate Key
            client.setEmail("ivan_" + UUID.randomUUID() + "@example.com");
            client.setBirthDate(LocalDate.of(1990, 1, 1));
            client.setPassport(passport);

            client = clientRepository.saveAndFlush(client);

            // Формируем запись
            StatementEntity entity = new StatementEntity();
            entity.setClient(client);
            entity.setApplicationStatus(ApplicationStatus.PREAPPROVAL);
            entity.setCreationDate(LocalDateTime.now()); // ());

            entity = statementRepository.saveAndFlush(entity);
            return entity.getStatementId();
        });

        entityManager.clear();
    }

    @Test
    void shouldWaitForPessimisticLock() throws Exception {
        AtomicLong firstThreadEndTime = new AtomicLong();
        AtomicLong secondThreadStartTime = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Поток 1: Захватывает замок
        CompletableFuture<Void> thread1 = CompletableFuture.runAsync(() -> {
            transactionTemplate.execute(status -> {
                System.out.println(LocalTime.now().format(formatter) + " Thread 1: Пытаюсь взять Lock...");
                statementService.getStatementForUpdate(statementId);
                System.out.println(LocalTime.now().format(formatter) + " Thread 1: Lock взят");

                latch.countDown(); // Стартуем второй поток

                try {
                    Thread.sleep(5000); // Держим замок 5 секунд
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                firstThreadEndTime.set(System.currentTimeMillis());
                System.out.println(LocalTime.now().format(formatter) + " Thread 1: Завершаю транзакцию и отпускаю Lock");
                return null;
            });
        });

        // Поток 2: Ждет сигнала и пытается обновить
        CompletableFuture<Void> thread2 = CompletableFuture.runAsync(() -> {
            try {
                latch.await(); // Ждем, пока первый поток точно захватит замок
                Thread.sleep(100);

                LoanOfferDto offer = new LoanOfferDto();
                offer.setStatementId(statementId);

                System.out.println(LocalTime.now().format(formatter) + " Thread 2: Пытаюсь обновить (жду освобождения)...");
                statementService.updateStatementWithChoosedOffer(offer);

                secondThreadStartTime.set(System.currentTimeMillis());
                System.out.println(LocalTime.now().format(formatter) + " Thread 2: Обновление выполнено");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // Ждём, пока оба потока завершат работу(или упрутся в таймаут в 10 секунд)
        CompletableFuture.allOf(thread1, thread2).get(10, TimeUnit.SECONDS);
        assertTrue(secondThreadStartTime.get() >= firstThreadEndTime.get(),
                "Thread 2 действительно ждал Thread 1, который должен был освободить ресурс");
    }
}
