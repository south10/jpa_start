package me.south10;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spring on 2017-05-14.
 */
@Entity
@Table(name = "MEMBER")
@NamedQueries({
		@NamedQuery(name = "Member.findByUsename",
				query = "select m from Member m where m.username = :username"),
		@NamedQuery(name = "Member.count",
				query = "select count(m) from Member m")
})

public class Member {
	@Id
	@GeneratedValue()
	@Column(name = "MEMBER_ID")
	private Long id;
	private String username;
	private int age;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEAM_ID", foreignKey = @ForeignKey(name = "FK_TEAM"))
	private Team team;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Order> orders = new ArrayList<>();

	public void setTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}

	public void addOrder(Order order) {
	    this.orders.add(order);
	    order.setMember(this);
    }
	public Member() {
	}

	public Member(String username, int age) {
		this.username = username;
		this.age = age;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Team getTeam() {
		return team;
	}

	public List<Order> getOrders() {
		return orders;
	}

	@Override
	public String toString() {
		return "Member{" +
				"id=" + id +
				", username='" + username + '\'' +
				", age=" + age +
				'}';
	}


}
