package com.teamharmony.newscommunity.users.repo;

import com.teamharmony.newscommunity.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
