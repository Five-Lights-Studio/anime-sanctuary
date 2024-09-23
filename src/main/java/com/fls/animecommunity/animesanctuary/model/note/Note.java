package com.fls.animecommunity.animesanctuary.model.note;

import java.util.List;
import java.util.Set;
import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.tag.Tag;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteRequestsDto;

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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    private Long hit;

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

    // 기존 String 기반의 tags 필드 유지
    @ElementCollection
    private List<String> tags;

    // 새로운 Tag 엔티티와의 관계
    @ManyToMany(mappedBy = "notes")
    private List<Tag> tagEntities;  // 새로운 List<Tag> 필드 추가

    @ManyToMany(mappedBy = "savedNotes")
    private Set<Member> savedByMembers;

    public Note(NoteRequestsDto requestsDto) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        if (requestsDto.getCategoryId() != null) {
            // Category를 가져와서 설정하는 로직 추가 (예: CategoryService를 사용해 가져오기)
        }
    }

    public void update(NoteRequestsDto requestsDto) {
        setContents(requestsDto.getContents());
        setTitle(requestsDto.getTitle());
        if (requestsDto.getCategoryId() != null) {
            // Category를 가져와서 설정하는 로직 추가 (예: CategoryService를 사용해 가져오기)
        } else {
            this.category = null;
        }
    }
}
