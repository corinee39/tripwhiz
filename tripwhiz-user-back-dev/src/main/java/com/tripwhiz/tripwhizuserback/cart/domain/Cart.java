package com.tripwhiz.tripwhizuserback.cart.domain;

import com.tripwhiz.tripwhizuserback.member.domain.MemberEntity;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"product", "member"})
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", nullable = false)
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pno", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false)
    @Builder.Default
    private boolean delFlag = false;

    public void setQty(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be less than zero.");
        }
        this.qty = qty;
    }

    public void softDelete() {
        this.delFlag = true;
    }

    // 필요시 사용할 delFlag setter
    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }

}
