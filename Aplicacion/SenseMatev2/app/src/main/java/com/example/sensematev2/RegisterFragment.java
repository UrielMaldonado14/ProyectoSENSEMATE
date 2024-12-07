package com.example.sensematev2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {

    private EditText edtUsername, edtPassword;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.register, container, false);

        edtUsername = rootView.findViewById(R.id.emailField);
        edtPassword = rootView.findViewById(R.id.passwordField);
        btnRegister = rootView.findViewById(R.id.registerButton);

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> attemptRegister());

        return rootView;
    }

    private void attemptRegister() {
        String email = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the user using Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(getContext(), "Registration successful! Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        // Navigate to the main menu fragment
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_registerFragment_to_menuMainFragment);
                    } else {
                        // Registration failed
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
