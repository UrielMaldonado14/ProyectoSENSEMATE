package com.example.sensematev2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class MenuMainFragment extends Fragment {

    public MenuMainFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.menumain, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a los botones y descripciones del menú principal
        Button traductorButton = view.findViewById(R.id.traductorButton);
        Button indicadorButton = view.findViewById(R.id.indicadorButton);
        Button soporteButton = view.findViewById(R.id.soporteButton);
        TextView traductorDescription = view.findViewById(R.id.traductorDescription);

        // Navegación al Traductor de Señas (Button)
        traductorButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_menuMainFragment_to_signalTranslatorFragment);
        });

        // Navegación al Indicador de Camino (Description)
        traductorDescription.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_menuMainFragment_to_trackIndicatorFragment);
        });

        // Navegación al Indicador de Camino (Button)
        indicadorButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_menuMainFragment_to_trackIndicatorFragment);
        });

        // Navegación al Soporte Técnico
        soporteButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_menuMainFragment_to_itSupportFragment);
        });
    }
}
