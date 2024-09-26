package com.fls.animecommunity.animesanctuary.model.note;

import java.util.List;
import java.util.Set;
import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.tag.Tag;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteRequestsDto;

import jakarta.persistence.Column;
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

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 작성자를 나타내는 필드

    private Long hit;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    private String imagePath;  // 이미지 경로 필드

    // 새로운 Tag 엔티티와의 ManyToMany 관계
    @ManyToMany(mappedBy = "notes")
    private List<Tag> tagEntities;  // Tag 엔티티와의 연관관계 필드

    @ManyToMany(mappedBy = "savedNotes")
    private Set<Member> savedByMembers;

    // 생성자
    public Note(String title, String contents, Member member) {
        this.title = title;
        this.contents = contents;
        this.member = member;
    }

    // update 메서드
    public void update(NoteRequestsDto requestsDto) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        if (requestsDto.getCategoryId() != null) {
            // 필요 시 카테고리 업데이트 로직 추가
        }
    }
    
    // setCategory 메서드
    public void setCategory(Category category) {
        this.category = category;
    }
}
