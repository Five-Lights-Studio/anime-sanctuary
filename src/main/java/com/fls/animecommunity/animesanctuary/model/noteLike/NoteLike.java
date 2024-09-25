package com.fls.animecommunity.animesanctuary.model.noteLike;

import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.Note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * class 이름이 SQL예약어 like랑 겹치기 때문에 NoteLike로 했음
 * 그리고 따로 좋아요객체를 만들어서  @JoinColumn member_id 와 note_id 를 외래키로 참조하도록함 
 * 그리고 좋아요 수를 세는 로직은 이 객체자체의 수를 세는 방식으로 함.
*/

@Entity
@Getter
@Table(name = "noteLike")
@NoArgsConstructor
public class NoteLike {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "noteLike_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "note_id")
	private Note note;
	
//	//생성자
	public NoteLike(Note note, Member member) {
		this.note = note;
		this.member = member;
	}
}
