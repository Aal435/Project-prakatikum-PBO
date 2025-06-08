package com.example.crud.controller;

import com.example.crud.model.User;
import com.example.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Untuk validasi (opsional lanjutan)
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController { // Atau tambahkan metode ini ke HomeController/AuthController

    @Autowired
    private UserRepository userRepository; // Autowire jika Anda perlu mengambil data User dari DB

    @GetMapping("/profile")
    public String userProfilePage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            User currentUser = userRepository.findByUsername(username); // Ambil data user dari DB
            model.addAttribute("currentUser", currentUser); // Kirim objek User ke template
        }
        return "profile"; // Ini akan mencari file profile.html di folder templates
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (!isUserAuthenticated(authentication)) {
            return "redirect:/login";
        }
        String username = getUsernameFromAuthentication(authentication);
        User currentUser = userRepository.findByUsername(username);

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pengguna tidak ditemukan untuk diedit.");
            return "redirect:/profile"; // Atau halaman error
        }

        // Jika ada error dari proses update sebelumnya, model userToEdit mungkin sudah ada di flash attributes
        if (!model.containsAttribute("userToEdit")) {
             model.addAttribute("userToEdit", currentUser);
        }
        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateUserProfile(@ModelAttribute("userToEdit") User formUser, 
                                    BindingResult bindingResult, // Opsional untuk validasi error
                                    Authentication authentication, 
                                    RedirectAttributes redirectAttributes,
                                    Model model) { // Tambahkan model untuk kirim balik data jika error validasi

        if (!isUserAuthenticated(authentication)) {
            return "redirect:/login";
        }

        String loggedInUsername = getUsernameFromAuthentication(authentication);
        User existingUserInDB = userRepository.findByUsername(loggedInUsername);

        if (existingUserInDB == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan: Pengguna tidak ditemukan.");
            return "redirect:/profile";
        }

        

        if (formUser.getNama() == null || formUser.getNama().trim().isEmpty()) {
            bindingResult.rejectValue("nama", "error.userToEdit", "Nama lengkap tidak boleh kosong.");
        }
        if (formUser.getEmail() == null || formUser.getEmail().trim().isEmpty()) {
            bindingResult.rejectValue("email", "error.userToEdit", "Email tidak boleh kosong.");
        } else {
            // Cek apakah email diubah dan apakah email baru sudah digunakan pengguna lain
            if (!existingUserInDB.getEmail().equalsIgnoreCase(formUser.getEmail())) {
                User userByNewEmail = userRepository.findByEmail(formUser.getEmail());
                if (userByNewEmail != null && !userByNewEmail.getUsername().equals(loggedInUsername)) {
                    bindingResult.rejectValue("email", "error.userToEdit", "Email ini sudah digunakan oleh pengguna lain.");
                }
            }
        }

        if (bindingResult.hasErrors()) {
            // Jika ada error validasi, kembali ke form edit dengan membawa data yang sudah diisi dan pesan error
            // 'formUser' (yang merupakan userToEdit dengan data dari form) sudah otomatis ada di model karena @ModelAttribute
            // model.addAttribute("userToEdit", formUser); // Tidak perlu eksplisit jika nama objek sama
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userToEdit", bindingResult);
            redirectAttributes.addFlashAttribute("userToEdit", formUser); // Kirim kembali data form
            redirectAttributes.addFlashAttribute("errorMessage", "Periksa kembali data yang Anda masukkan.");
            return "redirect:/profile/edit"; // Redirect untuk mencegah resubmission dan menggunakan flash attributes
        }


        // Update field yang diizinkan
        existingUserInDB.setNama(formUser.getNama());
        existingUserInDB.setEmail(formUser.getEmail());
        // Username, password, dan role tidak diubah melalui form ini untuk menjaga keamanan dan kesederhanaan
        // Perubahan password sebaiknya memiliki alur terpisah yang lebih aman.

        userRepository.save(existingUserInDB);

        redirectAttributes.addFlashAttribute("successMessage", "Profil berhasil diperbarui!");
        return "redirect:/profile";
    }

    private boolean isUserAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));
    }

    private String getUsernameFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}