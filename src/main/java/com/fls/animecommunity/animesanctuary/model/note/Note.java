package com.fls.animecommunity.animesanctuary.model.note;

import java.util.List;
import java.util.Set;

import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.member.Member;
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
@Getter
@Setter
@ToString
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
    private Member member;  // 작성자를 나타내는 필드 추가

    private Long hit;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    private String imagePath;  // 이미지 경로 추가

    @ElementCollection
    private List<String> tags;

    @ManyToMany(mappedBy = "savedNotes")
    private Set<Member> savedByMembers;

    public Note(String title, String contents, Member member) {
        this.title = title;
        this.contents = contents;
        this.member = member;
    }

    public void update(NoteRequestsDto requestsDto) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        // 필요 시 카테고리나 다른 필드 업데이트
    }
}
