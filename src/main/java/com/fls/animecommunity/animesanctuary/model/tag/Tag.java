package com.fls.animecommunity.animesanctuary.model.tag;

import com.fls.animecommunity.animesanctuary.model.note.Note;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
        name = "note_tag",
        joinColumns = @JoinColumn(name = "tag_id"),
        inverseJoinColumns = @JoinColumn(name = "note_id")
    )
    private List<Note> notes;

    public Tag(String name) {
        this.name = name;
    }
}
