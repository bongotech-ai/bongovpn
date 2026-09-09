package com.example.bongovpn

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import ai.bongotech.bongovpn.BongoVpn

class MainActivity_Kotlin : AppCompatActivity() {

    private lateinit var btnConnect: MaterialButton
    private lateinit var tvDownloadSpeed: TextView
    private lateinit var tvUploadSpeed: TextView
    private lateinit var tvSessionUsage: TextView

    private var isConnected = false
    private lateinit var bongoVpn: BongoVpn

    companion object {
        const val VPN_USERNAME = "vpn"
        const val VPN_PASSWORD = "vpn"
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        btnConnect = findViewById(R.id.btnConnect)
        tvDownloadSpeed = findViewById(R.id.tvDownloadSpeed)
        tvUploadSpeed = findViewById(R.id.tvUploadSpeed)
        tvSessionUsage = findViewById(R.id.tvSessionUsage)

        // Initialize Library
        bongoVpn = BongoVpn(this)

        // Request notification permission on startup (Android 13+)
        if (!bongoVpn.hasNotificationPermission()) {
            bongoVpn.requestNotificationPermission()
        }

        /*
         * Attach your .ovpn file from asset folder
         * If your config doesn't require credentials, pass null or empty string
         */
        bongoVpn.attachFromAsset("japan.ovpn", VPN_USERNAME, VPN_PASSWORD)

        // Setup VPN Listener
        bongoVpn.setVpnListener(object : BongoVpn.VpnListener {
            override fun onVpnConnected() {
                btnConnect.isEnabled = true
                isConnected = true
                btnConnect.text = "Disconnect"
                btnConnect.setTextColor(Color.RED)
            }

            override fun onVpnStopped() {
                btnConnect.isEnabled = true
                isConnected = false
                btnConnect.text = "Connect VPN"
                btnConnect.setTextColor(Color.BLACK)
            }

            override fun onStatusUpdate(status: String) {
                tvSessionUsage.text = status
            }

            override fun onError(errorMessage: String) {
                btnConnect.isEnabled = true
                tvSessionUsage.text = errorMessage
            }

            override fun onSpeedUpdate(
                downloadBytes: Long,
                uploadBytes: Long,
                downloadSpeed: Long,
                uploadSpeed: Long
            ) {
                tvDownloadSpeed.text = BongoVpn.formatSpeed(downloadSpeed)
                tvUploadSpeed.text = BongoVpn.formatSpeed(uploadSpeed)
                tvSessionUsage.text = "Down: ${BongoVpn.formatBytes(downloadBytes)} | Up: ${BongoVpn.formatBytes(uploadBytes)}"
            }
        })

        // Connect/Disconnect Button
        btnConnect.setOnClickListener {
            btnConnect.isEnabled = false
            if (isConnected) {
                btnConnect.text = "Disconnecting..."
                stopVpn()
            } else {
                btnConnect.text = "Connecting..."
                connectVpn()
            }
        }

        // Configure Foreground Notification
        bongoVpn.showNotification()
            .title("Bongo VPN")
            .connectedText("Connected and Secured")
            .smallIcon(R.drawable.security_icon)
            .showSpeed(true)
            .targetActivity(MainActivity::class.java)
    }

    private fun connectVpn() {
        if (bongoVpn.hasVpnPermission()) {
            bongoVpn.startVpn()
        } else {
            bongoVpn.requestVpnPermission()
        }
    }

    private fun stopVpn() {
        bongoVpn.stopVpn()
    }

    override fun onDestroy() {
        super.onDestroy()
        bongoVpn.release()
    }
}