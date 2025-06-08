package com.example.crud.repository;

import com.example.crud.model.Cart;
import com.example.crud.model.CartItem;
import com.example.crud.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndMenuItem(Cart cart, MenuItem menuItem);
    // Metode lain jika diperlukan
}