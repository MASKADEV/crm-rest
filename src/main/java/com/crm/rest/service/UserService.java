package com.crm.rest.service;

import com.crm.rest.exception.ResourceNotFoundException;
import com.crm.rest.model.Role;
import com.crm.rest.model.User;
import com.crm.rest.repository.UserRepository;
import com.crm.rest.util.RoleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;

    final RoleService roleService;

    public Page<User> listAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(UUID id) throws ResourceNotFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public void promote(UUID userId, Set<String> roles) {
        User user = findById(userId);

        user.setRoles(getUserRoles(roles));

        userRepository.save(user);
    }

    public Set<Role> getUserRoles(Set<String> roles) {
        Optional<Set<String>> optionalRoles = Optional.ofNullable(roles);

        Set<String> defaultRoles = Set.of("user");

        return optionalRoles
                .map((innerRoles) -> {
                    if (innerRoles.isEmpty()) {
                        return defaultRoles;
                    }

                    return innerRoles;
                })
                .orElse(defaultRoles)
                .stream()
                .map((role) -> roleService.findByName(RoleUtils.getRoleByString(role)))
                .collect(Collectors.toSet());
    }

}
