package com.teamharmony.newscommunity.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamharmony.newscommunity.users.dto.SignupDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class User  extends Timestamped {
	@JsonIgnore
	@Id @GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_id")
	private Long id;
	@Column(unique = true)
	private String username;
	private String password;
	private String email;
	@OneToMany(mappedBy="user", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private Collection<UserRole> userRoles = new ArrayList<>();
	
	@Builder
	public User(SignupDto dto) {
		this.username = dto.getUsername_give();
		this.password = dto.getPassword_give();
	}
}
