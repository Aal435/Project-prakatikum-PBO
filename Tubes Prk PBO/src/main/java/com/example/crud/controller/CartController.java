package com.example.crud.controller;

import com.example.crud.model.Cart;
import com.example.crud.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    // Menampilkan halaman keranjang belanja
    @GetMapping("/cart")
    public String showCartPage(Model model, Authentication authentication) {
        // Cek jika user sudah login
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && 
                                  !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()));

        if (!isAuthenticated) {
            // Jika tidak login, tampilkan keranjang kosong atau redirect ke login
            // Di sini kita biarkan template yang menampilkan pesan "Keranjang kosong"
            model.addAttribute("cart", null);
            model.addAttribute("grandTotal", 0.0);
            model.addAttribute("cartItemCount", 0);
        } else {
            // Jika login, ambil data keranjang dari service
            Cart cart = cartService.getCurrentUserCart(authentication);
            model.addAttribute("cart", cart);

            // Hitung total harga
            double grandTotal = 0.0;
            if (cart != null && cart.getItems() != null) {
                for (var item : cart.getItems()) {
                    if (item.getMenuItem() != null && item.getMenuItem().getPrice() != null) {
                        grandTotal += item.getMenuItem().getPrice() * item.getQuantity();
                    }
                }
            }
            model.addAttribute("grandTotal", grandTotal);
            model.addAttribute("cartItemCount", cartService.getCartItemCount(authentication));
        }
        return "keranjang"; // Mengarahkan ke templates/keranjang.html
    }

    // Endpoint UTAMA untuk semua aksi keranjang (tambah, kurang, hapus)
    @PostMapping("/cart/update")
    public ResponseEntity<?> updateCartItem(@RequestParam("menuItemId") Long menuItemId,
                                            @RequestParam("quantity") int quantity,
                                            Authentication authentication) {
        // Cek jika user sudah login
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && 
                                  !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()));
        
        if (!isAuthenticated) {
            return ResponseEntity.status(401).body(Map.of("error", "Anda harus login untuk memodifikasi keranjang."));
        }
        
        String username = authentication.getName();
        try {
            // Memanggil metode service untuk mengubah kuantitas item
            // Metode ini akan menambah, mengupdate, atau menghapus (jika quantity <= 0)
            cartService.setItemQuantity(username, menuItemId, quantity);
            
            int totalItemsInCart = cartService.getCartItemCount(authentication);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Keranjang berhasil diperbarui!");
            response.put("cartItemCount", totalItemsInCart);
            
            // Mengirim kembali respons JSON ke JavaScript
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Menangani error jika user atau menu item tidak ditemukan
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}