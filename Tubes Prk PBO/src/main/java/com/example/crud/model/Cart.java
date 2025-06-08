package com.example.crud.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relasi ke User: Satu Cart dimiliki oleh satu User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true) // user_id adalah foreign key di tabel carts
    private User user;

    // Relasi ke CartItem: Satu Cart bisa memiliki banyak CartItem
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // EAGER agar item langsung terload
    private List<CartItem> items = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    // Helper method untuk menambah item ke keranjang
    public void addItem(CartItem item) {
    this.items.add(item);
    item.setCart(this); // Menghubungkan CartItem kembali ke Cart ini
}

// Helper method untuk menghapus item dari keranjang
    public void removeItem(CartItem item) {
    this.items.remove(item);
    item.setCart(null); // Menghapus hubungan
    }
}