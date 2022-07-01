package com.teamharmony.newscommunity.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class UserRole {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	public UserRole(User user, Role role) {
		this.user = user;
		this.role = role;
	}
}
