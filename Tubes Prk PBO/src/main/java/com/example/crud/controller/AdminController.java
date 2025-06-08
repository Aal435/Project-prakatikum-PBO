package com.example.crud.controller;

import com.example.crud.model.MenuItem;
import com.example.crud.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Opsional, untuk validasi
// import jakarta.validation.Valid; // Opsional, untuk validasi
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin") // Semua URL di controller ini akan diawali dengan /admin
public class AdminController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // Menampilkan daftar menu untuk dikelola (CRUD)
    @GetMapping("/menu/manage")
    public String manageMenuItems(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAll());
        model.addAttribute("pageTitle", "Kelola Menu");
        return "admin/manage-menu"; // Halaman HTML untuk kelola menu
    }

    // Menampilkan form untuk menambah menu baru (atau mengedit menu yang ada)
    @GetMapping({"/menu/add", "/menu/edit/{id}"})
    public String showMenuItemForm(@PathVariable(name = "id", required = false) Long id, Model model) {
        MenuItem menuItem;
        String pageTitle;
        if (id != null) {
            menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Menu item tidak ditemukan dengan ID: " + id));
            pageTitle = "Edit Menu Item";
        } else {
            menuItem = new MenuItem();
            pageTitle = "Tambah Menu Baru";
        }
        model.addAttribute("menuItem", menuItem);
        model.addAttribute("pageTitle", pageTitle);
        return "admin/form-menu-item"; // Halaman HTML untuk form tambah/edit
    }

    // Menyimpan menu baru atau hasil editan
    @PostMapping("/menu/save")
    public String saveMenuItem(@ModelAttribute("menuItem") /*@Valid*/ MenuItem menuItem,
                               BindingResult bindingResult, // Aktifkan jika menggunakan @Valid
                               RedirectAttributes redirectAttributes) {

        // Contoh validasi sederhana (bisa diperluas atau menggunakan Bean Validation dengan @Valid)
        if (menuItem.getName() == null || menuItem.getName().trim().isEmpty() ||
            menuItem.getPrice() == null || menuItem.getPrice() < 0 ||
            menuItem.getCategory() == null || menuItem.getCategory().trim().isEmpty() ||
            menuItem.getImageUrl() == null || menuItem.getImageUrl().trim().isEmpty()) {
            
            redirectAttributes.addFlashAttribute("errorMessage", "Semua field (Nama, Harga, Kategori, URL Gambar) wajib diisi dan harga tidak boleh negatif.");
            if (menuItem.getId() != null) {
                return "redirect:/admin/menu/edit/" + menuItem.getId();
            } else {
                // Untuk data yang error kembali ke form, idealnya data formUser dikirim kembali
                // redirectAttributes.addFlashAttribute("menuItem", menuItem); // Ini akan hilang saat redirect
                // Cara yang lebih baik adalah return "admin/form-menu-item" dan tambahkan menuItem ke model
                // Tapi untuk kesederhanaan redirect, kita korbankan data form yang sudah diisi saat error.
                return "redirect:/admin/menu/add";
            }
        }
        // Jika menggunakan @Valid dan BindingResult:
        // if (bindingResult.hasErrors()) {
        //     // model.addAttribute("pageTitle", menuItem.getId() == null ? "Tambah Menu Baru" : "Edit Menu Item");
        //     return "admin/form-menu-item"; // Kembali ke form jika ada error validasi
        // }

        System.out.println("Menyimpan MenuItem: " + menuItem.getName() + ", Kategori: " + menuItem.getCategory() + ", Harga: " + menuItem.getPrice());
        menuItemRepository.save(menuItem);
        System.out.println("MenuItem berhasil disimpan dengan ID: " + menuItem.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Menu item '" + menuItem.getName() + "' berhasil disimpan!");
        return "redirect:/admin/menu/manage";
    }

    // Menghapus menu item
    @GetMapping("/menu/delete/{id}")
    public String deleteMenuItem(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (!menuItemRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu item tidak ditemukan.");
            return "redirect:/admin/menu/manage";
        }
        menuItemRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item berhasil dihapus.");
        return "redirect:/admin/menu/manage";
    }

    @GetMapping("/menu/test") // URL akan menjadi /admin/menu/test
    @ResponseBody // Untuk langsung mengembalikan String, bukan nama view
    public String testAdminAccess() {
        return "Admin Test Page Reached!";
    }
}