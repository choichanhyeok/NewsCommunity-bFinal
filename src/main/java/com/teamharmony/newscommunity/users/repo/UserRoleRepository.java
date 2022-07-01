package com.teamharmony.newscommunity.users.repo;

import com.teamharmony.newscommunity.users.entity.Role;
import com.teamharmony.newscommunity.users.entity.User;
import com.teamharmony.newscommunity.users.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	Collection<UserRole> findByUser(User user);
	Collection<UserRole> findByRole(Role role);
}
