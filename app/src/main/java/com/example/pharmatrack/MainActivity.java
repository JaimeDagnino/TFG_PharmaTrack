package com.example.pharmatrack;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import com.example.pharmatrack.Notifications.NotificationHelper;
import com.example.pharmatrack.db.DbMedicamentos;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pharmatrack.databinding.ActivityMainBinding;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidThreeTen.init(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        resetDosisTomadasHoySiEsNuevoDia(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_hoy, R.id.navigation_progreso, R.id.navigation_citas, R.id.navigation_medicamentos)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        String abrir = getIntent().getStringExtra("openFragment");
        if ("citas".equals(abrir)) {
            binding.navView.setSelectedItemId(R.id.navigation_citas);
        }


    }
    private void resetDosisTomadasHoySiEsNuevoDia(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences("pharma_prefs", MODE_PRIVATE);
        long ultimoReset = prefs.getLong("ultimo_reset_dosis", 0);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long inicioDiaActual = c.getTimeInMillis();

        if (ultimoReset < inicioDiaActual) {
            DbMedicamentos dbm = new DbMedicamentos(context);
            SQLiteDatabase db = dbm.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("dosis_tomadas_hoy", 0);
            db.update("t_medicamentos", cv, null, null);

            prefs.edit().putLong("ultimo_reset_dosis", inicioDiaActual).apply();
        }
    }
}