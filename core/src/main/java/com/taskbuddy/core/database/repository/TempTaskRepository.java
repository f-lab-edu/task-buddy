package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.port.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//TODO (#16) API가 정상 실행되도록 하기 위해 임시 생성함. Persistence Layer 구현시 대체될 예정
@Repository
public class TempTaskRepository implements TaskRepository {

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Task save(Task task) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
