package com.example.sensematev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class ITSupportFragment extends Fragment {

    public ITSupportFragment() {
        // Constructor vacío requerido
    }


    public void mandarCorreo() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // Usa este MIME type para clientes de correo
        intent.putExtra(Intent.EXTRA_SUBJECT, "Asunto del soporte");
        intent.putExtra(Intent.EXTRA_TEXT, "Por favor describe tu problema o consulta aquí...");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"suport.sensemate@gmail.com"});

        // Verifica que haya una aplicación que pueda manejar el intent
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Selecciona tu aplicación de correo:"));
        } else {
            // Notifica al usuario que no hay aplicaciones de correo disponibles
            Toast.makeText(requireContext(), "No tienes aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.itsupport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencia al botón "Atrás"
        ImageView backButton = view.findViewById(R.id.backButton);

        // Configurar la navegación al menú principal
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_itSupportFragment_to_menuMainFragment);
        });

        // Referencia al botón "Contactarnos"
        View contactButton = view.findViewById(R.id.contactButton);

        // Configurar el evento de clic para enviar un correo
        contactButton.setOnClickListener(v -> {
            mandarCorreo(); // Llama al método que envía el correo
        });
    }

}
