package com.fls.animecommunity.animesanctuary.model.note;

import java.util.List;
import java.util.Set;

import com.fls.animecommunity.animesanctuary.dto.note.NoteRequestsDto;
import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor
public class Note extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제목
    @Column(nullable = false)
    private String title;

    // 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    // 조회수
    private Long hit;

    // 카테고리
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = true)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // Getter and Setter - member
    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    // 노트와 연관된 태그들
    @ElementCollection
    private List<String> tags;

    // 이 노트를 저장한 사용자들 (ManyToMany 관계)
    @ManyToMany(mappedBy = "savedNotes")
    private Set<Member> savedByMembers;

    public Note(NoteRequestsDto requestsDto) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        if (requestsDto.getCategoryId() != null) {
            // Category를 가져와서 설정하는 로직 추가 (예: CategoryService를 사용해 가져오기)
            // this.category = categoryService.getCategoryById(requestsDto.getCategoryId());
        }
    }

    public void update(NoteRequestsDto requestsDto) {
        setContents(requestsDto.getContents());
        setTitle(requestsDto.getTitle());
        if (requestsDto.getCategoryId() != null) {
            // Category를 가져와서 설정하는 로직 추가 (예: CategoryService를 사용해 가져오기)
            // this.category = categoryService.getCategoryById(requestsDto.getCategoryId());
        } else {
            this.category = null;
        }
    }
}
