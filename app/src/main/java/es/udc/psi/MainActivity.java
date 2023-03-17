package es.udc.psi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "CHANNEL";
    int notificationId = 100;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view);
        createNotificationChannel();

        String[] data = {"zero","one", "two", "three", "four", "five", "six", "seven"}; // data
        ArrayList<String> dataList = new ArrayList<String>(Arrays.asList(data)); // dynamic list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, // layout
                dataList); // data
        listView.setAdapter(adapter);

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_list, menu);
            }
        });
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        switch (item.getItemId()) {
            case R.id.opcion1:

                String elementoABorrar = adapter.getItem(position);
                adapter.remove(elementoABorrar);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Eliminar " + position, Toast.LENGTH_SHORT).show();
                snackbarWithUndo("Deshacer",position,elementoABorrar);
                return true;
            case R.id.opcion2:
                Toast.makeText(MainActivity.this, "Compartir " + position, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void snackbarWithUndo(String mensaje, final int posicion, final String elemento) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        @SuppressLint("ResourceType") Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG)
                .setAction("Deshacer", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.insert(elemento, posicion);
                        adapter.notifyDataSetChanged();
                    }
                });
        snackbar.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.snackbar:
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Esto es un snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Acción cuando se presiona el botón "Ok"
                                // ...
                            }
                        });
                snackbar.show();
                return true;
            case R.id.lanzar_noti:
                String title = "Título de la notificación";
                String text = "Texto de la notificación";
                //int notificationId = new Random().nextInt();
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.putExtra("boton_pulsado", "Botón 1");

                Intent intent2 = new Intent(this, MainActivity.class);
                intent2.putExtra("boton_pulsado", "Botón 2");

                Intent intent3 = new Intent(this, MainActivity.class);
                intent3.putExtra("boton_pulsado", "Botón 3");

                PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 3, intent2, PendingIntent.FLAG_MUTABLE);
                PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 6, intent3, PendingIntent.FLAG_MUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.ic_launcher_background, "Botón 1", pendingIntent1)
                        .addAction(R.drawable.ic_launcher_foreground, "Botón 2", pendingIntent2)
                        .addAction(R.mipmap.ic_launcher, "Botón 3", pendingIntent3);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(notificationId, builder.build());
                return true;
            case R.id.noti_clear:
                // Acción cuando se selecciona la opción "Borrar notificaciones"
                NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notificationId);
                return true;
            case R.id.menu_toast:
                Toast.makeText(MainActivity.this, "Hola Toast", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void createNotificationChannel() {
// Create the NotificationChannel, but only on API 26+ because
// the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    name, importance);
            channel.setDescription(description);
// Register the channel with the system; you can't change the importance
// or other notification behaviors after this
            NotificationManager notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(channel);
        }
    }
}