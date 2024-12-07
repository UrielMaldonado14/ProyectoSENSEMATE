package com.example.sensematev2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.pm.PackageManager;

public class SignalTranslatorFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int VIDEO_REQUEST_CODE = 101;

    public SignalTranslatorFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.signalstraductor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configuración del botón "Atrás" para navegar al menú principal
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_signalTranslatorFragment_to_menuMainFragment)
        );

        // Configuración del ícono de la cámara para iniciar grabación de video
        ImageView cameraIcon = view.findViewById(R.id.cameraIcon);
        cameraIcon.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                // Iniciar la grabación de video
                recordVideo();
            } else {
                // Solicitar permisos de cámara y grabación de audio
                requestCameraAndAudioPermission();
            }
        });
        // Referencia al botón "Iniciar Traducción"
        Button startTranslationButton = view.findViewById(R.id.startTranslationButton);


        startTranslationButton.setOnClickListener(v -> {
            // Llamar al método para abrir el navegador con una URL fija
            openBrowserWithText();
        });


    }

    private void openBrowserWithText() {
        try {
            String url = "http://192.168.187.208"; // Puedes cambiarlo por cualquier URL

            // Intent para abrir el navegador con la URL
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            // Intent sin flags ni verificación adicional
            startActivity(browserIntent);
        } catch (Exception e) {
            // Capturar cualquier error
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void recordVideo() {
        // Intent para grabar video
        Intent videoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        if (videoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(videoIntent, VIDEO_REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(), "No se encontró una aplicación de grabación de video", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestCameraAndAudioPermission() {
        // Solicitar permisos de cámara y grabación de audio
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permisos concedidos, iniciar grabación de video
                recordVideo();
            } else {
                // Permiso denegado, mostrar mensaje
                Toast.makeText(getActivity(), "Permiso de cámara o audio denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            // Obtener la URI del video grabado
            Uri videoUri = data.getData();

            // Referencia al VideoView
            VideoView videoView = getView().findViewById(R.id.videoView);

            // Establecer la URI del video en el VideoView
            videoView.setVideoURI(videoUri);

            // Configurar el VideoView para reproducir el video
            videoView.start();
        }
    }
}
