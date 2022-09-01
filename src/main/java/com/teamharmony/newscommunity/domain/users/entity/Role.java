package com.teamharmony.newscommunity.domain.users.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Role {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "role_id")
	private Long id;
	@ApiModelProperty(value = "권한명", example = "USER", required = true)
	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private RoleType name;
	
	@OneToMany(mappedBy="role", cascade = CascadeType.ALL)
	private List<UserRole> users = new ArrayList<>();
	
	public Role(RoleType name) {
		this.name = name;
	}
}
