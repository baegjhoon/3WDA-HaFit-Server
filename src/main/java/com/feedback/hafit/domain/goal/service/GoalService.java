package com.feedback.hafit.domain.goal.service;


import com.feedback.hafit.domain.goal.dto.request.GoalRequestDTO;
import com.feedback.hafit.domain.goal.dto.response.GoalForDdayDTO;
import com.feedback.hafit.domain.goal.dto.response.GoalResponseDTO;
import com.feedback.hafit.domain.goal.entity.Goal;
import com.feedback.hafit.domain.goal.entity.Keyword;
import com.feedback.hafit.domain.goal.repository.GoalRepository;
import com.feedback.hafit.domain.goal.repository.KeywordRepository;
import com.feedback.hafit.domain.plan.repository.PlanRepository;
import com.feedback.hafit.domain.user.entity.User;
import com.feedback.hafit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final KeywordRepository keywordRepository;
    private final PlanRepository planRepository;

    public void createGoal(GoalRequestDTO goalRequestDTO, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Email: " + email));
        Long keywordId = goalRequestDTO.getKeywordId();
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new EntityNotFoundException("Keyword not found with ID: " + keywordId));
        Goal goal = Goal.builder()
                .user(user)
                .keyword(keyword)
                .goal_target_date(goalRequestDTO.getGoal_target_date())
                .goal_content(goalRequestDTO.getGoal_content())
                .build();

        goalRepository.save(goal);
    }

    public List<GoalResponseDTO> getGoalsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Email: " + email));

        List<Goal> goals = goalRepository.findByUser(user);
        List<GoalResponseDTO> goalResponseDTOs = new ArrayList<>();

        for (Goal goal : goals) {
            Long keywordId = goal.getKeyword().getKeywordId();
            Keyword keyword = keywordRepository.findById(keywordId)
                    .orElseThrow(() -> new EntityNotFoundException("Keyword not found with keywordId: " + keywordId));
            goalResponseDTOs.add(new GoalResponseDTO(goal, keyword));
        }

        return goalResponseDTOs;
    }

    public void updateGoal(Long goalId, GoalRequestDTO goalRequestDTO) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with ID: " + goalId));

        Long keywordId = goalRequestDTO.getKeywordId();
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new EntityNotFoundException("Keyword not found with ID: " + keywordId));

        // Update the goal properties based on the goalRequestDTO
        goal.setGoal_target_date(goalRequestDTO.getGoal_target_date());
        goal.setGoal_content(goalRequestDTO.getGoal_content());
        goal.setKeyword(keyword);

        goalRepository.save(goal);
    }

    public void deleteGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with ID: " + goalId));

        goalRepository.delete(goal);
    }

    public Map<String, Object> getMyGoals(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Email: " + email));

        List<Goal> goals = goalRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId());

        LocalDateTime today = LocalDateTime.now();
        List<GoalForDdayDTO> goalDTOs = new ArrayList<>();

        for (Goal goal : goals) {
            LocalDate targetDate = goal.getGoal_target_date();
            long daysRemaining = ChronoUnit.DAYS.between(today.toLocalDate(), targetDate);
            goalDTOs.add(new GoalForDdayDTO(goal, daysRemaining));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("count", goalDTOs.size());
        result.put("goals", goalDTOs);

        return result;
    }

}