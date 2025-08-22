package com.umerqureshicodes.tidier.prompts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepo extends JpaRepository<Prompt, Long> {
}
