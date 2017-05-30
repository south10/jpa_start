package me.south10;

import javax.persistence.*;
import java.util.List;

/**
 * Created by spring on 2017-05-27.
 */
@Entity
@Table(name = "ORDERS")
public class Order {
	@Id
	@GeneratedValue
	@Column(name = "ORDER_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEMBER_ID", foreignKey = @ForeignKey(name = "FK_MEMBER"))
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID", foreignKey = @ForeignKey(name = "FK_PRODUCT"))
	private Product product;

	private int orderAmount;

	@Embedded
	private Address address;

	// ==연관관계 메서드==//
	public void setMember(Member member) {
		this.member = member;
		member.getOrders().add(this);
	}

	public void setProduct(Product product) {
	    this.product = product;
    }
	// ==연관관계 메서드==//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Member getMember() {
		return member;
	}

	public Address getAddress() {
		return address;
	}

	public int getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(int orderAmount) {
		this.orderAmount = orderAmount;
	}

    public void setAddress(Address address) {
        this.address = address;
    }

    public Product getProduct() {
        return product;
    }

	@Override
	public String toString() {
		return "Order{" +
				"id=" + id +
				", orderAmount=" + orderAmount +
				", address=" + address +
				'}';
	}
}
