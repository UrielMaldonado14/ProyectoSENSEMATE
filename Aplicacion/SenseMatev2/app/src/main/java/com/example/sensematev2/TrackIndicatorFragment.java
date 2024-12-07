package com.example.sensematev2;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackIndicatorFragment extends Fragment {

    private EditText editOrigen, editDestino;
    private TextView textDistancia, textTiempo;
    private MapView mapView;
    private Button btnInicio, btnConfigVoz, btnFinalizar;
    private TextToSpeech textToSpeech;
    private float velocidad = 1.0f;
    private Locale idioma = Locale.getDefault();
    private OkHttpClient client;
    private Queue<String> instruccionesQueue;

    public TrackIndicatorFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        return inflater.inflate(R.layout.trackindicator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar componentes
        inicializarComponentes(view);

        // Inicializar TTS
        inicializarTextToSpeech();

        // Configurar eventos
        configurarEventos(view);
    }

    private void inicializarComponentes(View view) {
        editOrigen = view.findViewById(R.id.edit_origen);
        editDestino = view.findViewById(R.id.edit_destino);
        textDistancia = view.findViewById(R.id.distancia);
        textTiempo = view.findViewById(R.id.tiempo_estimado);
        mapView = view.findViewById(R.id.mapa);
        btnInicio = view.findViewById(R.id.btn_inicio);
        btnConfigVoz = view.findViewById(R.id.btn_configuracion_voz);
        btnFinalizar = view.findViewById(R.id.btn_finalizar);

        mapView.setMultiTouchControls(true);
        client = new OkHttpClient();
        instruccionesQueue = new LinkedBlockingQueue<>();
    }

    private void inicializarTextToSpeech() {
        textToSpeech = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(idioma);
                textToSpeech.setSpeechRate(velocidad);
            } else {
                Toast.makeText(requireContext(), "Error al inicializar TTS", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarEventos(View view) {
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_trackIndicatorFragment_to_menuMainFragment)
        );

        btnInicio.setOnClickListener(v -> iniciarRuta());
        btnConfigVoz.setOnClickListener(v -> mostrarDialogoConfiguracion());
        btnFinalizar.setOnClickListener(v -> finalizarRuta());

        View.OnFocusChangeListener focusListener = (view1, hasFocus) -> {
            if (hasFocus) {
                String mensaje = view1 == editOrigen ? "Campo de dirección de origen seleccionado"
                        : "Campo de dirección de destino seleccionado";
                anunciarTexto(mensaje);
            }
        };

        editOrigen.setOnFocusChangeListener(focusListener);
        editDestino.setOnFocusChangeListener(focusListener);
    }

    private void iniciarRuta() {
        String origen = editOrigen.getText().toString().trim();
        String destino = editDestino.getText().toString().trim();
        if (origen.isEmpty() || destino.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese direcciones de origen y destino", Toast.LENGTH_SHORT).show();
            return;
        }
        anunciarTexto("Iniciando ruta");
        obtenerCoordenadas(origen, destino);
    }

    private void finalizarRuta() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        instruccionesQueue.clear();
        Toast.makeText(requireContext(), "Ruta finalizada", Toast.LENGTH_SHORT).show();
        anunciarTexto("Ruta finalizada");
    }

    private void mostrarDialogoConfiguracion() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.options, null);
        Spinner spinnerIdioma = dialogView.findViewById(R.id.spinner_idioma);
        Spinner spinnerVelocidad = dialogView.findViewById(R.id.spinner_velocidad);

        ArrayAdapter<String> adapterIdiomas = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, obtenerIdiomasDisponibles());
        spinnerIdioma.setAdapter(adapterIdiomas);

        ArrayAdapter<String> adapterVelocidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, obtenerRangosDeVelocidad());
        spinnerVelocidad.setAdapter(adapterVelocidades);

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialogInterface, i) -> {
                    configurarIdioma(spinnerIdioma.getSelectedItem().toString());
                    configurarVelocidad(spinnerVelocidad.getSelectedItem().toString());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void obtenerCoordenadas(String direccionOrigen, String direccionDestino) {
        String urlOrigen = "https://nominatim.openstreetmap.org/search?q=" + direccionOrigen + "&format=json";
        String urlDestino = "https://nominatim.openstreetmap.org/search?q=" + direccionDestino + "&format=json";

        client.newCall(new Request.Builder().url(urlOrigen).header("User-Agent", "OSMDroidApp").build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error al conectar con Nominatim (origen)", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray results = new JSONArray(response.body().string());
                        if (results.length() > 0) {
                            double latOrigen = results.getJSONObject(0).getDouble("lat");
                            double lonOrigen = results.getJSONObject(0).getDouble("lon");
                            obtenerCoordenadasDestino(latOrigen, lonOrigen, urlDestino);
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Dirección de origen no encontrada", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void obtenerCoordenadasDestino(double latOrigen, double lonOrigen, String urlDestino) {
        client.newCall(new Request.Builder().url(urlDestino).header("User-Agent", "OSMDroidApp").build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error al conectar con Nominatim (destino)", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray results = new JSONArray(response.body().string());
                        if (results.length() > 0) {
                            double latDestino = results.getJSONObject(0).getDouble("lat");
                            double lonDestino = results.getJSONObject(0).getDouble("lon");
                            requireActivity().runOnUiThread(() -> calcularRuta(latOrigen, lonOrigen, latDestino, lonDestino));
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Dirección de destino no encontrada", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void calcularRuta(double latOrigen, double lonOrigen, double latDestino, double lonDestino) {
        String url = "https://router.project-osrm.org/route/v1/driving/" + lonOrigen + "," + latOrigen + ";" + lonDestino + "," + latDestino + "?overview=full&steps=true";

        client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error al calcular la ruta", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        JSONArray routes = json.optJSONArray("routes");
                        if (routes != null && routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            double distancia = route.optDouble("distance", 0) / 1000.0;
                            double tiempo = route.optDouble("duration", 0) / 60.0;

                            requireActivity().runOnUiThread(() -> actualizarEtiquetas(distancia, tiempo));

                            String geometry = route.optString("geometry");
                            List<GeoPoint> puntosRuta = decodePolyline(geometry);

                            requireActivity().runOnUiThread(() -> trazarRutaEnMapa(puntosRuta));

                            JSONArray steps = route.getJSONArray("legs").getJSONObject(0).optJSONArray("steps");
                            List<String> instrucciones = new ArrayList<>();

                            if (steps != null) {
                                for (int i = 0; i < steps.length(); i++) {
                                    JSONObject step = steps.getJSONObject(i);

                                    String maneuver = step.optJSONObject("maneuver").optString("type", "continuar");
                                    String streetName = step.optString("name", "sin nombre");
                                    double distance = step.optDouble("distance", 0);

                                    String instruccion = String.format("En %s, %s por %.0f metros", streetName, obtenerAccion(maneuver), distance);
                                    instrucciones.add(instruccion);
                                }
                            }

                            if (instrucciones.isEmpty()) {
                                instrucciones.add("Sin instrucciones disponibles");
                            }

                            iniciarInstrucciones(instrucciones);

                        } else {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "No se encontraron rutas disponibles", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void trazarRutaEnMapa(List<GeoPoint> puntosRuta) {
        if (puntosRuta == null || puntosRuta.isEmpty()) {
            Toast.makeText(requireContext(), "No se pudo trazar la ruta", Toast.LENGTH_SHORT).show();
            return;
        }

        Polyline polyline = new Polyline();
        polyline.setPoints(puntosRuta);
        polyline.setColor(Color.BLUE);
        polyline.setWidth(8f);

        mapView.getOverlays().clear();
        mapView.getOverlays().add(polyline);
        mapView.invalidate();
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(puntosRuta.get(0));
    }

    private String obtenerAccion(String maneuver) {
        switch (maneuver) {
            case "turn-left":
                return "gire a la izquierda";
            case "turn-right":
                return "gire a la derecha";
            case "straight":
                return "siga recto";
            case "arrive":
                return "ha llegado a su destino";
            default:
                return "continúe";
        }
    }

    private List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> geoPoints = new ArrayList<>();
        if (encoded == null || encoded.isEmpty()) return geoPoints;

        int index = 0, len = encoded.length();
        int lat = 0, lon = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlon = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lon += dlon;

            GeoPoint point = new GeoPoint((lat / 1E5), (lon / 1E5));
            geoPoints.add(point);
        }

        return geoPoints;
    }

    private void iniciarInstrucciones(List<String> instrucciones) {
        for (String instruccion : instrucciones) {
            anunciarTexto(instruccion);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void actualizarEtiquetas(double distancia, double tiempo) {
        textDistancia.setText(String.format("Distancia: %.2f km", distancia));
        textTiempo.setText(String.format("Tiempo estimado: %.0f min", tiempo));
    }

    private List<String> obtenerIdiomasDisponibles() {
        List<String> idiomas = new ArrayList<>();
        idiomas.add("Español (España)");
        idiomas.add("Inglés (Estados Unidos)");
        return idiomas;
    }

    private List<String> obtenerRangosDeVelocidad() {
        List<String> velocidades = new ArrayList<>();
        velocidades.add("Lenta (0.5x)");
        velocidades.add("Normal (1.0x)");
        velocidades.add("Rápida (1.5x)");
        return velocidades;
    }

    private void configurarIdioma(String idiomaSeleccionado) {
        switch (idiomaSeleccionado) {
            case "Español (España)":
                idioma = new Locale("es", "ES");
                break;
            case "Inglés (Estados Unidos)":
                idioma = Locale.US;
                break;
            default:
                idioma = Locale.getDefault();
        }
        textToSpeech.setLanguage(idioma);
    }

    private void configurarVelocidad(String velocidadSeleccionada) {
        velocidad = velocidadSeleccionada.equals("Lenta (0.5x)") ? 0.5f : velocidadSeleccionada.equals("Rápida (1.5x)") ? 1.5f : 1.0f;
        textToSpeech.setSpeechRate(velocidad);
    }

    private void anunciarTexto(String texto) {
        if (textToSpeech != null) {
            textToSpeech.speak(texto, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
