package com.example.sensematev2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class FirstLookFragment extends Fragment {

    public FirstLookFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.firstlook, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a los botones
        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.registerButton);

        // Navegar al LoginFragment
        loginButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_firstLookFragment_to_loginFragment)
        );

        // Navegar al RegisterFragment
        registerButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_firstLookFragment_to_registerFragment)
        );
    }
}

