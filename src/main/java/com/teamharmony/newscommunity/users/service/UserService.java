package com.teamharmony.newscommunity.users.service;

import com.teamharmony.newscommunity.users.entity.Role;
import com.teamharmony.newscommunity.users.entity.RoleType;
import com.teamharmony.newscommunity.users.entity.User;
import com.teamharmony.newscommunity.users.entity.UserRole;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService {
	User saveUser(User user);
	Role saveRole(Role role);
	UserRole saveUserRole(User user, Role role);
	void addRoleToUser(String username, RoleType roleName);
	User getUser(String username);
	Role getRole(RoleType name);
	Collection<Role> getRoles(User user);
	List<User> getUsers();
	Map<String, Boolean> checkUser(String username);
}
