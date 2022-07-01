package com.teamharmony.newscommunity.users.repo;


import com.teamharmony.newscommunity.users.entity.Role;
import com.teamharmony.newscommunity.users.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(RoleType name);
}

