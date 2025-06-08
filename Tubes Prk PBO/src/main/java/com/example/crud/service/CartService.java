package com.example.crud.service;

import com.example.crud.model.Cart;
import com.example.crud.model.CartItem;
import com.example.crud.model.MenuItem;
import com.example.crud.model.User;
import com.example.crud.repository.CartRepository;
import com.example.crud.repository.MenuItemRepository;
import com.example.crud.repository.UserRepository;
import com.example.crud.repository.CartItemRepository; // <-- TAMBAHKAN IMPORT INI
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository; // <-- TAMBAHKAN DEPENDENSI INI

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username);
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    public Cart getCurrentUserCart(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        if (user == null) {
            return null;
        }
        return getOrCreateCart(user);
    }

    // Metode UTAMA yang sudah disempurnakan
    public Cart setItemQuantity(String username, Long menuItemId, int newQuantity) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User tidak ditemukan: " + username);
        }

        Cart cart = getOrCreateCart(user);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item tidak ditemukan dengan ID: " + menuItemId));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getMenuItem() != null && item.getMenuItem().getId().equals(menuItemId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            if (newQuantity > 0) {
                existingItem.setQuantity(newQuantity);
                cartItemRepository.save(existingItem); // Simpan perubahan pada CartItem
            } else {
                cart.removeItem(existingItem); // Hapus dari koleksi
                cartItemRepository.delete(existingItem); // Hapus dari database
            }
        } else if (newQuantity > 0) {
            CartItem newItem = new CartItem();
            newItem.setMenuItem(menuItem);
            newItem.setQuantity(newQuantity);
            cart.addItem(newItem); // Gunakan helper method untuk menyinkronkan relasi
            cartItemRepository.save(newItem); // Simpan CartItem baru secara eksplisit
        }
        
        // Mengembalikan cart yang sudah di-refresh dari database untuk memastikan konsistensi
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    public int getCartItemCount(Authentication authentication) {
        Cart cart = getCurrentUserCart(authentication);
        if (cart == null || cart.getItems() == null) {
            return 0;
        }
        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

}