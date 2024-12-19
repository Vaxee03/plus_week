package com.example.demo.entity;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.config.JpaConfig;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaConfig.class})
public class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    // 테스트 객체 설정
    private User createUser(String name, String email, String role, String password) {
        return new User(name, email, role, password);
    }

    private Item createItem(String name, String description, User owner, User manager) {
        return new Item(name, description, owner, manager);
    }

    @Test
    @DisplayName("Item 엔티티의 status 필드가 null일 때 저장 실패")
    void checkStatusIsNotNull() {

        // Given : 테스트 유저 객체 생성
        User tester1 = userRepository.save(createUser("user1", "001122jas@gmail.com", "사장", "12345!@#$%"));
        User tester2 = userRepository.save(createUser("user2", "vvvaaaxxxeee@naver.com", "매니저", "12345!@#$%"));

        // When : 테스트 아이템 객체 생성
        Item item = createItem("아이템", "아이템 설명", tester1, tester2);

        // 명시적으로 status를 null로 설정
        item.testSetStatus(null);

        // Then : 예외 발생 확인
        assertThrows(ConstraintViolationException.class, () -> {
            itemRepository.save(item);
        });
    }
}
