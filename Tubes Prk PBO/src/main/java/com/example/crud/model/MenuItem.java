package com.example.crud.model;

import jakarta.persistence.*;
// Pertimbangkan untuk menambahkan anotasi validasi jika diperlukan di masa depan
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.PositiveOrZero;
// import jakarta.validation.constraints.Size;

@Entity
@Table(name = "menu_items") // Nama tabel di database
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @NotBlank(message = "Nama menu tidak boleh kosong")
    // @Size(max = 100, message = "Nama menu maksimal 100 karakter")
    private String name;

    @Lob // Untuk teks yang lebih panjang, bisa juga String biasa jika deskripsi pendek
    // @Size(max = 255, message = "Deskripsi maksimal 255 karakter")
    private String description;

    // @NotNull(message = "Harga tidak boleh kosong")
    // @PositiveOrZero(message = "Harga harus numerik dan tidak negatif")
    private Double price; // Gunakan BigDecimal untuk presisi mata uang jika diperlukan

    // @NotBlank(message = "URL gambar tidak boleh kosong")
    // @Column(length = 500) // Panjang URL gambar bisa cukup besar
    private String imageUrl;

    // @NotBlank(message = "Kategori harus dipilih")
    private String category; // Contoh: "makanan", "minuman"

    // Constructors
    public MenuItem() {
    }

    public MenuItem(String name, String description, Double price, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}