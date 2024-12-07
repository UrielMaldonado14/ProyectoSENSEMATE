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

public class TranslationHistoryFragment extends Fragment {

    public TranslationHistoryFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.translationhistory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencia al botón "Atrás"
        ImageView backButton = view.findViewById(R.id.backButton);

        // Configurar la navegación al menú principal
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_translationHistoryFragment_to_menuMainFragment);
        });
    }
}
