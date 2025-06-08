package com.example.crud.controller;

import com.example.crud.model.Cart;
import com.example.crud.model.CartItem;
import com.example.crud.model.MenuItem;
import com.example.crud.repository.MenuItemRepository;
import com.example.crud.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CartService cartService; 

    @GetMapping({"/", "/index"})
    public String PetaSitus(Model model, Authentication authentication) {
        // Mengambil semua item menu dari database
        List<MenuItem> allItems = menuItemRepository.findAll();

        // Memisahkan item berdasarkan kategori "makanan" dan "minuman"
        List<MenuItem> makananItems = allItems.stream()
                                            .filter(item -> "makanan".equalsIgnoreCase(item.getCategory()))
                                            .collect(Collectors.toList());
        List<MenuItem> minumanItems = allItems.stream()
                                            .filter(item -> "minuman".equalsIgnoreCase(item.getCategory()))
                                            .collect(Collectors.toList());

        model.addAttribute("makananItems", makananItems);
        model.addAttribute("minumanItems", minumanItems);

        // Logic untuk mendapatkan kuantitas item di keranjang pengguna yang sedang login
        Map<Long, Integer> itemQuantitiesInCart = new HashMap<>();
        int totalCartItemCountForBadge = 0;

        // === PERBAIKAN DI SINI ===
        // Lakukan pengecekan autentikasi secara langsung di sini
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            
            // Jika pengguna sudah login, ambil data keranjangnya
            Cart currentUserCart = cartService.getCurrentUserCart(authentication);
            if (currentUserCart != null && currentUserCart.getItems() != null) {
                for (CartItem ci : currentUserCart.getItems()) {
                    if (ci.getMenuItem() != null) {
                       itemQuantitiesInCart.put(ci.getMenuItem().getId(), ci.getQuantity());
                    }
                }
            }
            totalCartItemCountForBadge = cartService.getCartItemCount(authentication);
        }
        
        // Kirim data kuantitas dan total item ke view
        model.addAttribute("itemQuantitiesInCart", itemQuantitiesInCart);
        model.addAttribute("cartItemCount", totalCartItemCountForBadge);

        return "index";
    }
}