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

public class CloseSessionFragment extends Fragment {

    public CloseSessionFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.closesession, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencia al botón de confirmación de cierre de sesión
        Button confirmButton = view.findViewById(R.id.logoutButton);

        // Configurar la navegación al menú principal
        confirmButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_closeSessionFragment_to_menuMainFragment);
        });
    }
}
