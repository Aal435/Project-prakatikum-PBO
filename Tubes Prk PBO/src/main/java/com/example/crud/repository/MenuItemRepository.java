package com.example.crud.repository;

import com.example.crud.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // Anda bisa menambahkan metode query kustom di sini jika diperlukan
    // Contoh:
    List<MenuItem> findByCategory(String category);
    List<MenuItem> findByNameContainingIgnoreCase(String name);
}