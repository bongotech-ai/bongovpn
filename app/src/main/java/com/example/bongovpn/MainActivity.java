package com.example.bongovpn;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import ai.bongotech.bongovpn.BongoVpn;

public class MainActivity extends AppCompatActivity {

    MaterialButton btnConnect;
    TextView tvDownloadSpeed, tvUploadSpeed,  tvSessionUsage;

    boolean isConnected = false;
    public static String VPN_USERNAME = "vpn";
    public static String VPN_PASSWORD = "vpn";
    private BongoVpn bongoVpn = new BongoVpn(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnConnect = findViewById(R.id.btnConnect);
        tvDownloadSpeed = findViewById(R.id.tvDownloadSpeed);
        tvUploadSpeed = findViewById(R.id.tvUploadSpeed);
        tvSessionUsage = findViewById(R.id.tvSessionUsage);

        // Request notification permission on startup (NOT Mandatory)
        if (!bongoVpn.hasNotificationPermission()){
            bongoVpn.requestNotificationPermission();
        }

        /*
        * Attach your .ovpn file from asset folder
        * If your config doesn't require username and password then keep null
         */
        bongoVpn.attachFromAsset("japan.ovpn", VPN_USERNAME, VPN_PASSWORD);

        /*
        * You can also attach your .ovpn file from string
        * You can parse your ovpn as string from your server. then attach to Bongo VPN lib runtime
        * Example:
        * bongoVpn.attachFromString("your_entire_ovpn_file_as_string", VPN_USERNAME, VPN_PASSWORD);
         */

        // Attach Listener
        bongoVpn.setVpnListener(new BongoVpn.VpnListener() {
            @Override
            public void onVpnConnected() {
                btnConnect.setEnabled(true);
                isConnected = true;
                btnConnect.setText("Disconnect");
                btnConnect.setTextColor(Color.RED);
            }

            @Override
            public void onVpnStopped() {
                btnConnect.setEnabled(true);
                isConnected = false;
                btnConnect.setText("Connect VPN");
                btnConnect.setTextColor(Color.BLACK);
            }

            @Override
            public void onStatusUpdate(String status) {
                tvSessionUsage.setText(status);
            }

            @Override
            public void onError(String errorMessage) {
                btnConnect.setEnabled(true);
                tvSessionUsage.setText(errorMessage);
            }

            @Override
            public void onSpeedUpdate(long downloadBytes, long uploadBytes, long downloadSpeed, long uploadSpeed) {
                tvDownloadSpeed.setText(bongoVpn.formatSpeed(downloadSpeed));
                tvUploadSpeed.setText(bongoVpn.formatSpeed(uploadSpeed));
                tvSessionUsage.setText("Down: " + BongoVpn.formatBytes(downloadBytes) + " | Up: " + BongoVpn.formatBytes(uploadBytes));

            }
        });


        // Connect Button Onclick Listener
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnConnect.setEnabled(false);
                if (isConnected) {
                    btnConnect.setText("Disconnecting...");
                    stopVpn();
                } else {
                    btnConnect.setText("Connecting...");
                    connectVpn();
                }
            }
        });


        // Update Notification if needed
        bongoVpn.showNotification()
                .title("Bongo VPN")
                .connectedText("Connected and Secured")
                .smallIcon(R.drawable.security_icon)
                .showSpeed(true)
                .targetActivity(MainActivity.class);




    } // end of oncCreate()



    private void connectVpn() {
        if (bongoVpn.hasVpnPermission()) {
            bongoVpn.startVpn();
        } else {
            bongoVpn.requestVpnPermission();
        }
    }


    // Connect Method
    private void stopVpn() {
        bongoVpn.stopVpn();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bongoVpn!=null) bongoVpn.release();
    }

    //-----------------------------------
}