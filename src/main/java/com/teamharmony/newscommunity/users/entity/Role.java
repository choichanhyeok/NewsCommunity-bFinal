package com.teamharmony.newscommunity.users.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Role {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "role_id")
	private Long id;
	@Enumerated(value = EnumType.STRING)
	private RoleType name;
	@OneToMany(mappedBy="role", cascade = CascadeType.PERSIST)
	private List<UserRole> users = new ArrayList<>();
	
	public Role(RoleType name) {
		this.name = name;
	}
}
