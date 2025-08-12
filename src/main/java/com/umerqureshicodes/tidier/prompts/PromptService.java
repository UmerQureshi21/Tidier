package com.umerqureshicodes.tidier.prompts;

import org.springframework.stereotype.Service;

@Service
public class PromptService {

    private final PromptRepo promptRepo;

    public PromptService(PromptRepo promptRepo) {
        this.promptRepo = promptRepo;
    }

    public Prompt savePrompt(Prompt prompt) {
        return promptRepo.save(prompt);
    }
}
