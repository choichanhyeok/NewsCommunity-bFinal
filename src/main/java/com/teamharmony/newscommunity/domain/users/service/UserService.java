package com.teamharmony.newscommunity.domain.users.service;

import com.teamharmony.newscommunity.domain.users.dto.*;
import com.teamharmony.newscommunity.domain.users.entity.*;
import com.teamharmony.newscommunity.domain.users.repository.RoleRepository;
import com.teamharmony.newscommunity.domain.users.repository.UserProfileRepository;
import com.teamharmony.newscommunity.domain.users.repository.UserRepository;
import com.teamharmony.newscommunity.domain.users.repository.UserRoleRepository;
import static com.teamharmony.newscommunity.domain.users.util.ProfileUtil.*;
import com.teamharmony.newscommunity.exception.AuthException;
import com.teamharmony.newscommunity.exception.InvalidRequestException;
import com.teamharmony.newscommunity.domain.users.filesotre.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserProfileRepository profileRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;
	private final FileStore fileStore;
	
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	
	/**
	 * 인증을 위한 사용자 조회
	 *
	 * @param username 해당 사용자 ID
	 * @return 사용자 정보를 담은 객체
	 * @see UserDetailsService#loadUserByUsername
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) throw AuthException.builder()
		                                     .message("사용자를 찾을 수 없습니다.")
		                                     .invalidValue("사용자 ID: " + username)
		                                     .code("A410")
		                                     .build();
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Collection<UserRole> userRole = userRoleRepository.findByUser(user);
		Collection<Role> roles = new ArrayList<>();
		userRole.forEach(r -> roles.add(r.getRole()));
		roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName().toString())));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
	}

	/**
	 * 사용자 ID 중복 여부
	 *
	 * @param username 중복 확인할 사용자 ID
	 * @return 사용자 ID 중복 여부
	 */
	public boolean checkUser(String username) {
		return getUser(username) != null;
	}

	/**
	 * 회원 가입
	 *
	 * @param dto 가입 시 입력한 정보
	 * @return 성공 확인
	 */
	public String signUp(SignupRequestDto dto) throws HibernateException {
		User user = new User(dto);
		saveUser(user);
		Role role = getRole(new Role(RoleType.USER).getName());
		
		try {
			if (role == null) {
				saveRole(new Role(RoleType.USER));
			}
			addRoleToUser(user.getUsername(), RoleType.USER);
			// 기본 프로필 추가
			defaultProfile(user);
		} catch (DataIntegrityViolationException | ConstraintViolationException e) {
			log.error("Faild to sign up");
			throw new HibernateException("Failed to add role or profile to user cause=", e.getCause());
		}
		return "success";
	}
	
	/**
	 * 저장소에 사용자 저장
	 *
	 * @param user 저장할 사용자 정보
	 */
	public void saveUser(User user) {
		user.encodePW(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
	
	/**
	 * 저장소에 권한 저장
	 *
	 * @param role 저장할 권한 정보
	 */
	public void saveRole(Role role) {
		roleRepository.save(role);
	}
	
	/**
	 * 회원 가입 시 기본 프로필 적용
	 *
	 * @param user 기본 프로필을 적용할 사용자 정보
	 */
	public void defaultProfile(User user) {
		UserProfile profile = UserProfile.builder()
		                                 .nickname(user.getUsername())
		                                 .profile_pic("default")
		                                 .user(user)
		                                 .build();
		profileRepository.save(profile);
	}

	/**
	 * 사용자에 권한 부여
	 *
	 * @param username 권한을 추가할 사용자 ID
	 * @param roleName 추가할 권한명
	 */
	public void addRoleToUser(String username, RoleType roleName) throws IllegalArgumentException {
		User user = getUser(username);
		Role role = getRole(roleName);
		if (role == null) throw InvalidRequestException.builder()
		                                               .message("해당 권한을 찾을 수 없습니다.")
		                                               .invalidValue("권한명: " + roleName)
		                                               .code("U403")
		                                               .build();
		UserRole userRole = new UserRole(user, role);
		userRoleRepository.save(userRole);
	}
	
	/**
	 * 사용자 정보 조회
	 *
	 * @param username 조회할 사용자 ID
	 * @return 사용자 정보
	 */
	private User getUser(String username) {
		return userRepository.findByUsername(username);
	}
	
	/**
	 * 권한 정보 조회
	 *
	 * @param roleName 조회할 권한명
	 * @return 권한 정보
	 */
	private Role getRole(RoleType roleName) {
		return roleRepository.findByName(roleName);
	}
	
	/**
	 * 전체 사용자 조회
	 *
	 * @return 전체 사용자 정보
	 */
	public List<UserResponseDto> getUsers() {
		List<User> users= userRepository.findAll();
		return users.stream().map(UserResponseDto::toDto).collect(toList());
	}

	/**
	 * 프로필 정보 조회
	 *
	 * @param username 프로필 정보를 가져올 회원 ID
	 * @param status 인증된 사용자 ID와 일치 여부
	 * @return 인증된 사용자 ID와 일치 여부, 프로필 사진 url, 프로필 정보
	 */
	public GetProfileResponseDto getProfile(String username, boolean status) {
		User user = getUser(username);
		UserProfile profile = user.getProfile();
		String link = getProfileImageUrl(username);
		return GetProfileResponseDto.builder()
		                            .profile(profile)
		                            .status(status)
		                            .link(link)
		                            .build();
	}

	/**
	 * 버킷에 저장된 프로필 사진 조회
	 *
	 * @param username 프로필 정보를 가져올 회원 ID
	 * @return 프로필 사진 URL
	 */
	public String getProfileImageUrl(String username) {
		UserProfile profile = getUser(username).getProfile();
		if (profile == null) throw InvalidRequestException.builder()
		                                                  .message("사용자의 프로필을 찾을 수 없습니다.")
		                                                  .invalidValue("사용자 ID: " + username)
		                                                  .code("U401")
		                                                  .build();
		String path = String.format("%s/%s", bucketName, username);
		// 버킷에서 프로필 사진 가져오기
		if (!profile.getProfile_pic().equals("default")) {
			return fileStore.download(path, profile.getProfile_pic());
		} else {
			return "default";
		}
	}

	/**
	 * 프로필 정보 변경
	 *
	 * @param username 프로필을 변경할 사용자 ID
	 * @param requestDto 변경할 프로필 정보
	 * @return 성공 확인, 메시지
	 */
	public String updateProfile(String username, ProfileRequestDto requestDto) {
		UserProfile existingProfile = getUser(username).getProfile();  // 해당 사용자의 기존 프로필 찾기
		if (existingProfile == null) throw InvalidRequestException.builder()
		                                                          .message("사용자의 프로필을 찾을 수 없습니다.")
		                                                          .invalidValue("사용자 ID: " + username)
		                                                          .code("U401")
		                                                          .build();

		MultipartFile file = requestDto.getFile();
		if (file != null) {
			isImage(file); // 파일이 이미지인지 확인
			// 버킷에 저장될 경로, 파일명 그리고 파일의 metadata 생성
			String path = String.format("%s/%s", bucketName, username);
			String fileName = String.format("%s", file.getOriginalFilename());
			Map<String, String> metadata = extractMetadata(file);

			try {
				fileStore.save(path, fileName, Optional.of(metadata), file.getInputStream()); // 변경 파일 저장
			} catch (IOException e) {
				throw new IllegalStateException("프로필 사진 저장에 실패했습니다.", e.getCause());
			}
		}
		// 프로필 변경 사항 적용 후 DB 저장
		existingProfile.update(requestDto);
		profileRepository.save(existingProfile);
		return "success";
	}
}
