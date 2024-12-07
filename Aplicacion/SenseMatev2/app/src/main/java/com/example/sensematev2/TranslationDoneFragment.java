package com.example.sensematev2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class TranslationDoneFragment extends Fragment {

    public TranslationDoneFragment() {
        // Constructor vacío requerido
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencia al botón "Atrás"
        ImageView backButton = view.findViewById(R.id.backButton);

        // Configurar la navegación al menú principal
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_translationDoneFragment_to_menuMainFragment);
        });
    }
}