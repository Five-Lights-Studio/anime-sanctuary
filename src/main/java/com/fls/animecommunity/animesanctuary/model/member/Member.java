package com.fls.animecommunity.animesanctuary.model.member;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import com.fls.animecommunity.animesanctuary.model.note.Note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(length = 100, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @Column(length = 255)
    private String profileImage;

    @Column(length = 20)
    private String provider;  // ex: "google"
    
    @Column(length = 100)
    private String providerId;  // 소셜 로그인 시 제공자에서 받은 고유 ID
    
    @Enumerated(EnumType.STRING)
    private Role role;  // USER, ADMIN 등

    // 사용자가 저장한 노트 리스트 (ManyToMany 관계)
    @ManyToMany
    @JoinTable(
        name = "member_saved_notes",
        joinColumns = @JoinColumn(name = "member_id"),
        inverseJoinColumns = @JoinColumn(name = "note_id")
    )
    private Set<Note> savedNotes;  // 사용자가 저장한 노트 리스트

    // 기본 생성자
    public Member() {}

    // 모든 필드를 포함한 생성자
    public Member(String username, String password, String name, LocalDate birth, String email, GenderType gender) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.gender = gender;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    
    // 소셜 로그인 전용 생성자
    public Member(String name, String email, String provider, String providerId, Role role) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.username = email;
        this.birth = LocalDate.of(1900, 1, 1);  // 기본 생년월일 설정
        this.gender = GenderType.OTHER;  // 기본 성별 설정
    }
}
