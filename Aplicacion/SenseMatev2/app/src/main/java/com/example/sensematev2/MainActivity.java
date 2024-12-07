package com.example.sensematev2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Obtener el NavHostFragment y el NavController
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();

            // Configurar el BottomNavigationView con el NavController
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            // Listener para ocultar el BottomNavigationView en pantallas específicas
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // IDs de las pantallas donde no quieres mostrar la barra
                if (destination.getId() == R.id.firstLookFragment ||
                        destination.getId() == R.id.loginFragment ||
                        destination.getId() == R.id.registerFragment) {
                    bottomNavigationView.setVisibility(android.view.View.GONE);
                } else {
                    bottomNavigationView.setVisibility(android.view.View.VISIBLE);
                }
            });

        } catch (IllegalStateException e) {
            Log.e(TAG, "Error inicializando el NavController: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        try {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            return navController.navigateUp() || super.onSupportNavigateUp();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error navegando hacia arriba: " + e.getMessage(), e);
            return super.onSupportNavigateUp();
        }
    }
}
