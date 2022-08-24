package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Problem;
import org.example.condigbat.entity.UserProblem;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserProblemDTO;
import org.example.condigbat.repository.ProblemRepository;
import org.example.condigbat.repository.UserProblemRepository;
import org.example.condigbat.repository.UserRepository;
import org.example.condigbat.service.serviceInt.UserProblemService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserProblemServiceImpl implements UserProblemService {

    private final UserProblemRepository repository;

    private final ProblemRepository problemRepository;

    private final UserRepository userRepository;

    @Override
    public ApiResult<UserProblemDTO> getUserProblem(Integer userId, Integer problemId) {

        Optional<UserProblem> byUserIdAndProblemId = repository.getByUserIdAndProblemId(userId, problemId);
        UserProblemDTO userProblemDTO;

        if (byUserIdAndProblemId.isEmpty()){
            Problem byId = problemRepository.findById(problemId).orElseThrow(() ->
                    RestException.restThrow("Problem not found", HttpStatus.NOT_FOUND));
            boolean existsById = userRepository.existsById(userId);
            if (existsById)
                throw RestException.restThrow("User not found", HttpStatus.NOT_FOUND);
            userProblemDTO = new UserProblemDTO();
            userProblemDTO.setUserId(userId);
            userProblemDTO.setProblem(byId);
            userProblemDTO.setSolution(byId.getMethodSignature());
            userProblemDTO.setSolved(false);
        } else {
            userProblemDTO = mapUserProblemToUserProblemDTO(byUserIdAndProblemId.get());
        }

        return ApiResult.successResponse(userProblemDTO);
    }

    @Override
    public ApiResult<List<UserProblemDTO>> getUserProblems() {
        return null;
    }

    private UserProblemDTO mapUserProblemToUserProblemDTO(UserProblem userProblem) {

        return new UserProblemDTO(
                userProblem.getUser().getId(),
                userProblem.getProblem(),
                userProblem.getSolution(),
                userProblem.getSolved()
        );
    }

}
