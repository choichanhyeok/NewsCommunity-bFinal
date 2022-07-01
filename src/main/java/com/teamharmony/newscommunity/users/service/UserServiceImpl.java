package com.teamharmony.newscommunity.users.service;

import com.teamharmony.newscommunity.users.entity.Role;
import com.teamharmony.newscommunity.users.entity.RoleType;
import com.teamharmony.newscommunity.users.entity.User;
import com.teamharmony.newscommunity.users.entity.UserRole;
import com.teamharmony.newscommunity.users.repo.RoleRepository;
import com.teamharmony.newscommunity.users.repo.UserRepository;
import com.teamharmony.newscommunity.users.repo.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) throw new UsernameNotFoundException("User not found");
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Collection<UserRole> userRole = userRoleRepository.findByUser(user);
		Collection<Role> roles = new ArrayList<>();
		userRole.forEach(r -> roles.add(r.getRole()));
		roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName().toString())));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
	}
	
	@Override
	public User saveUser(User user) {
		log.info("Saving new user {} to the database", user.getUsername());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
	
	@Override
	public Role saveRole(Role role) {
		log.info("Saving new role {} to the database", role.getName());
		return roleRepository.save(role);
	}
	
	@Override
	public UserRole saveUserRole(User user, Role role) {
		log.info("Saving new userRole");
		role = roleRepository.findByName(role.getName());
		UserRole userRole = new UserRole(user, role);
		return userRoleRepository.save(userRole);
	}
	
	@Override
	public void addRoleToUser(String username, RoleType roleName) {
		log.info("Adding role {} to user {}", roleName, username);
		User user = userRepository.findByUsername(username);
		Role role = roleRepository.findByName(roleName);
		UserRole userRole = new UserRole(user, role);
		userRoleRepository.save(userRole);
	}
	
	@Override
	public User getUser(String username) {
		log.info("Fetching user {}", username);
		return userRepository.findByUsername(username);
	}
	
	@Override
	public Role getRole(RoleType name) {
		log.info("Fetching role {}", name);
		return roleRepository.findByName(name);
	}
	
	@Override
	public Collection<Role> getRoles(User user) {
		Collection<UserRole> userRole = userRoleRepository.findByUser(user);
		Collection<Role> roles = new ArrayList<>();
		userRole.forEach(r -> roles.add(r.getRole()));
		return roles;
	}
	
	@Override
	public List<User> getUsers() {
		log.info("Fetching all users");
		return userRepository.findAll();
	}
	
	@Override
	public Map<String, Boolean> checkUser(String username) {
		log.info("Checking duplicates username {}", username);
		Map<String, Boolean> body = new HashMap<>();
		Boolean exists = userRepository.findByUsername(username) != null;
		body.put("exists", exists);
		return body;
	}
}
