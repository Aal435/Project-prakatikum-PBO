package com.example.crud.controller;

import com.example.crud.model.MenuItem;
import com.example.crud.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping({"/", "/index"})
    public String PetaSitus(Model model) {
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

        return "index";
    }
}