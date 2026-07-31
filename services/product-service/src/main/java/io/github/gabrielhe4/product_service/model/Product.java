package io.github.gabrielhe4.product_service.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Setter
    private String sku;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(columnDefinition = "TEXT")
    @Setter
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @Setter
    private BigDecimal price;

    @Column(precision = 5, scale = 2)
    @Setter
    private BigDecimal discountPercentage;

    @Setter
    private LocalDateTime discountStartsAt;

    @Setter
    private LocalDateTime discountEndsAt;

    @Column(name = "image_url")
    @Setter
    private String imageUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private Category category;

    // tells JPA/Hibernate to not map that field or method to a database column. It's ignored during persistence entirely
    @Transient
    public BigDecimal getFinalPrice() {
        if (!isDiscountActive())
            return price;

        BigDecimal factor = BigDecimal.ONE
            .subtract(discountPercentage.divide(BigDecimal.valueOf(100)));

        return price.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    public boolean isDiscountActive() {
        if (discountPercentage == null || discountPercentage.signum() <= 0)
            return false;

        LocalDateTime now = LocalDateTime.now();
        if (discountStartsAt != null && now.isBefore(discountStartsAt))
            return false;

        if (discountEndsAt != null && now.isAfter(discountEndsAt))
            return false;

        return true;
    }


}
