package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: 4. find or save 예제 개선
    @Transactional
    public void reportUsers(List<Long> userIds) {

        // 존재 여부 검증
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("비어있거나 존재하지 않는 유저입니다.");
        }

        // 유저 상태 업데이트
        userRepository.findByIdInAndsUpdateStatus(userIds);

    }
}
