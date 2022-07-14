package com.teamharmony.newscommunity.users.service;

import com.teamharmony.newscommunity.users.dto.*;
import com.teamharmony.newscommunity.users.vo.ProfileVO;
import com.teamharmony.newscommunity.users.entity.*;
import com.teamharmony.newscommunity.users.filesotre.FileStore;
import com.teamharmony.newscommunity.users.repository.*;
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
import static org.apache.http.entity.ContentType.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserProfileRepository profileRepository;
	private final UserRoleRepository userRoleRepository;
	private final TokensRepository tokensRepository;
	
	private final PasswordEncoder passwordEncoder;
	private final FileStore fileStore;
	
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	
	/**
	 * 인증을 위한 사용자 조회
	 *
	 * @param 		username 해당 사용자 ID
	 * @return 		사용자 정보를 담은 객체
	 * @see   		UserDetailsService#loadUserByUsername
	 */
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
	
	public void signUp(SignupRequestDto dto) throws HibernateException {
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
		} catch (DataIntegrityViolationException|ConstraintViolationException e) {
			log.error("Faild to sign up");
			throw new HibernateException("Failed to add role or profile to user cause=", e.getCause());
		}
	}
	
	/**
	 * 저장소에 사용자 저장
	 *
	 * @param 		user 저장할 사용자 정보
	 */
	public void saveUser(User user) {
		log.info("Saving new user {} to the database", user.getUsername());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
	
	/**
	 * 저장소에 권한 저장
	 *
	 * @param 		role 저장할 권한 정보
	 */
	public void saveRole(Role role) {
		log.info("Saving new role {} to the database", role.getName());
		roleRepository.save(role);
	}
	
	/**
	 * 토큰 값 변경
	 *
	 * @param 		username 해당 사용자 ID
	 * @param 		access_token 허용된 접근 토큰 값
	 * @param 		refresh_token 허용된 갱신 토큰 값
	 * @return 		저장된 토큰 정보
	 */
	public Tokens updateTokens(String username, String access_token, String refresh_token) {
		Tokens tokens = getTokens(username);
		tokens.update(access_token, refresh_token);
		return tokensRepository.save(tokens);
	}
	
	/**
	 * 회원 가입시 기본 프로필 적용
	 *
	 * @param 		user 기본 프로필을 적용할 사용자 정보
	 */
	public void defaultProfile(User user) {
		UserProfile profile = UserProfile.builder()
		                                 .nickname(user.getUsername())
		                                 .profile_pic("default")
		                                 .build();
		
		profile.setUser(user);
		profileRepository.save(profile);
	}
	
	/**
	 * 프로필 정보 변경
	 *
	 * @param 		username 프로필을 변경할 사용자 ID
	 * @param 		profileVO 변경할 프로필 정보
	 * @return 		성공 확인, 메시지
	 */
	public Map<String, String> updateProfile(String username, ProfileVO profileVO) {
		MultipartFile file = profileVO.getFile();
		User user = getUser(username);
		UserProfile existingProfile = user.getProfile();	// 해당 유저의 기존 프로필 찾기
		if (existingProfile == null) throw new IllegalArgumentException(String.format("User profile %s not found", username));
		
		if (!file.isEmpty()) {
			isImage(file); // 파일이 이미지인지 확인
			// 버킷에 저장될 경로, 파일명 그리고 파일의 metadata 생성
			String path = String.format("%s/%s", bucketName, username);
			String fileName = String.format("%s", file.getOriginalFilename());
			Map<String, String> metadata = extractMetadata(file);
			
			try {
				if(!existingProfile.getProfile_pic().equals("default")) fileStore.delete(path, existingProfile.getProfile_pic()); // 기존 파일 삭제
				fileStore.save(path, fileName, Optional.of(metadata), file.getInputStream()); // 업데이트 파일 저장
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
			
			// 프로필 변경 사항 적용 후 DB 저장
			existingProfile.update(profileVO);
			profileRepository.save(existingProfile);

		} else {
			// 프로필 사진 외 변경 사항 적용 후 DB 저장
			existingProfile.notUpdatePic(profileVO);
			profileRepository.save(existingProfile);
		}
		Map<String, String> body = new HashMap<>();
		body.put("result", "success");
		body.put("msg", "프로필 변경이 완료되었습니다.");
		return body;
	}
	
	/**
	 * 버킷에 저장된 프로필 사진 조회
	 *
	 * @param 		username 프로필 정보를 가져올 회원 ID
	 * @param 		profile 프로필 정보
	 * @return 		프로필 사진 URL
	 */
	public String getProfileImageUrl(String username, UserProfile profile) {
		if (profile == null) throw new IllegalArgumentException(String.format("User profile %s not found", username));
		String path = String.format("%s/%s", bucketName,username);
		// 버킷에서 프로필 사진 가져오기
		if (!profile.getProfile_pic().equals("default")) {
			return fileStore.download(path, profile.getProfile_pic());
		} else {
			return "default";
		}
	}
	
	private Map<String, String> extractMetadata(MultipartFile file) {
		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		return metadata;
	}
	
	private void isImage(MultipartFile file) {
		if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType())
		           .contains(file.getContentType()))
			throw new IllegalArgumentException("File must be an image [ "+ file.getContentType() +" ]");
	}
	
	/**
	 * 사용자에 권한 부여
	 *
	 * @param 		username 권한을 추가할 사용자 ID
	 * @param 		roleName 추가할 권한명
	 */
	public void addRoleToUser(String username, RoleType roleName) {
		log.info("Adding role {} to user {}", roleName, username);
		User user = getUser(username);
		Role role = getRole(roleName);
		UserRole userRole = new UserRole(user, role);
		userRoleRepository.save(userRole);
	}
	
	/**
	 * 사용자 정보 조회
	 *
	 * @param 		username 조회할 사용자 ID
	 * @return 		사용자 정보
	 */
	public User getUser(String username) {
		log.info("Fetching user {}", username);
		return userRepository.findByUsername(username);
	}
	
	/**
	 * 권한 정보 조회
	 *
	 * @param 		roleName 조회할 권한명
	 * @return 		권한 정보
	 */
	public Role getRole(RoleType roleName) {
		log.info("Fetching role {}", roleName);
		Role role = roleRepository.findByName(roleName);
		if (role == null) throw new IllegalArgumentException(String.format("%s not found", roleName));
		return role;
	}
	
	/**
	 * 사용자가 지닌 권한 정보 조회
	 *
	 * @param 		user 조회할 사용자 ID
	 * @return 		사용자가 지닌 권한 정보
	 * @see				UserService#getRoles
	 */
	public Collection<Role> getRoles(User user) {
		Collection<UserRole> userRole = userRoleRepository.findByUser(user);
		Collection<Role> roles = new ArrayList<>();
		userRole.forEach(r -> roles.add(r.getRole()));
		return roles;
	}
	
	/**
	 * 전체 사용자 조회
	 *
	 * @return 		전체 사용자 정보
	 */
	public List<UserResponseDto> getUsers() {
		log.info("Fetching all users");
		List<User> users= userRepository.findAll();
		return users.stream().map(UserResponseDto::toDto).collect(toList());
	}
	
	/**
	 * 프로필 정보 조회
	 *
	 * @param 		username 프로필 정보를 가져올 회원 ID
	 * @param 		status 인증된 사용자 ID와 일치 여부
	 * @return 		인증된 사용자 ID와 일치 여부, 프로필 사진 url, 프로필 정보
	 */
	public Map<String, Object> getProfile(String username, boolean status) {
		log.info("Fetching profile of user {}", username);
		User user = getUser(username);
		UserProfile profile = user.getProfile();
		ProfileResponseDto profileDto = new ProfileResponseDto(profile);
		Map<String, Object> body = new HashMap<>();
		body.put("status", status);
		body.put("link", getProfileImageUrl(username, profile));
		body.put("profile", profileDto);
		return body;
	}
	
	/**
	 * 사용자의 허용 토큰 정보 조회
	 *
	 * @param 		username 조회할 사용자 ID
	 * @return 		사용자의 허용 토큰 정보
	 */
	public Tokens getTokens(String username) {
		log.info("Fetching tokens of user {}", username);
		return tokensRepository.findByUsername(username);
	}
	
	/**
	 * 사용자 ID 중복 여부
	 *
	 * @param 		username 중복 확인할 사용자 ID
	 * @return 		사용자 ID 중복 여부
	 */
	public Map<String, Boolean> checkUser(String username) {
		log.info("Checking duplicates username {}", username);
		Map<String, Boolean> body = new HashMap<>();
		Boolean exists = getUser(username) != null;
		body.put("exists", exists);
		return body;
	}
}
