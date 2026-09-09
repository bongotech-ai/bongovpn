# BongoVPN 🛡️

[![Maven Central](https://img.shields.io/maven-central/v/ai.bongotech/bongovpn.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/ai.bongotech/bongovpn)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)

A robust, modern OpenVPN client library for Android, optimized for Android 14+ (API 34+) and built on the trusted core architecture of `ics-openvpn`.

This repository hosts the official integration guide, policy compliance instructions, and an open-source sample implementation using the `ai.bongotech:bongovpn` library.

---

## 🚀 Installation

Add the dependency to your app module's `build.gradle` file (Groovy / Kotlin DSL):

```groovy
dependencies {
    implementation 'ai.bongotech:bongovpn:1.0.2'
}
```

---

## ⚙️ 1. Essential Packaging Configuration

Add this to your app-level `build.gradle` to support native binaries:

### Groovy / Kotlin DSL (`app/build.gradle`):
```groovy
android {
    ...
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}
```

---


## 📋 2. Required Permissions

Add the necessary permissions to your `app/src/main/AndroidManifest.xml`:

```xml
<!-- Core Network & Foreground Service Permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SYSTEM_EXEMPTED" />
<!-- Required for Android 13+ (API 33+) Status Notifications -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---


## 🛡️ 3. Google Play Policy & FGS Declaration

For apps targeting Android 14+ (API 34+), Google Play enforces strict policies on background tasks. `BongoVPN` uses the `systemExempted` foreground service type with the `activate_vpn` subtype.

When preparing your release in the **Google Play Console**, navigate to **App Content ➔ Foreground Services Declaration** and provide the following details:

| Form Field | Declaration Details |
| :--- | :--- |
| **FGS Type** | `systemExempted` |
| **Use Case Description** | *"The app utilizes VpnService to establish and manage an encrypted OpenVPN tunnel. A system-exempted foreground service is required to maintain active network routing, prevent unwanted OS termination, and provide real-time connection status via ongoing notifications."* |
| **Video Demonstration** | Provide a link to a short screen recording (e.g., YouTube or Google Drive) demonstrating the user initiating the VPN connection and the active status notification appearing in the system tray. |

---




## 💡 4. Quick Start Usage

Choose your preferred language to see the implementation:

<details open>
<summary><b>☕ Java Implementation</b></summary>

### 1. Initialize & Request Notification Permission
```java
import ai.bongotech.bongovpn.BongoVpn;

public class MainActivity extends AppCompatActivity {
    private BongoVpn bongoVpn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bongoVpn = new BongoVpn(this);

        // Optional: Request notification permission on Android 13+
        if (!bongoVpn.hasNotificationPermission()) {
            bongoVpn.requestNotificationPermission();
        }
    }
}
```

### 2. Attach Profile & Setup Listeners
```java
// Attach .ovpn from assets
bongoVpn.attachFromAsset("japan.ovpn", "vpn_user", "vpn_pass");

//Or pass string directly
// bongoVpn.attachFromString("your_entire_ovpn_file_as_string", "vpn_user", "vpn_pass");

// Setup event callbacks
bongoVpn.setVpnListener(new BongoVpn.VpnListener() {
    @Override
    public void onVpnConnected() {
        // Triggered when tunnel is established
    }

    @Override
    public void onVpnStopped() {
        // Triggered when connection is disconnected
    }

    @Override
    public void onStatusUpdate(String status) {
        // Triggered during connection state changes
    }

    @Override
    public void onError(String errorMessage) {
        // Triggered on connection/auth errors
    }

    @Override
    public void onSpeedUpdate(long downloadBytes, long uploadBytes, long downloadSpeed, long uploadSpeed) {
        // Live bandwidth stats & speeds
        String speed = bongoVpn.formatSpeed(downloadSpeed);
        String usage = bongoVpn.formatBytes(downloadBytes);
    }
});
```

### 3. Connect, Disconnect & Cleanup
```java
// Connect
private void connectVpn() {
    if (bongoVpn.hasVpnPermission()) {
        bongoVpn.startVpn();
    } else {
        bongoVpn.requestVpnPermission();
    }
}

// Disconnect
private void stopVpn() {
    bongoVpn.stopVpn();
}

// Cleanup
@Override
protected void onDestroy() {
    super.onDestroy();
    if (bongoVpn != null) {
        bongoVpn.release();
    }
}
```

</details>

<details>
<summary><b>🎯 Kotlin Implementation</b></summary>

### 1. Initialize & Request Notification Permission
```kotlin
import ai.bongotech.bongovpn.BongoVpn

class MainActivity : AppCompatActivity() {
    private lateinit var bongoVpn: BongoVpn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bongoVpn = BongoVpn(this)

        // Optional: Request notification permission on Android 13+
        if (!bongoVpn.hasNotificationPermission()) {
            bongoVpn.requestNotificationPermission()
        }
    }
}
```

### 2. Attach Profile & Setup Listeners
```kotlin
// Attach .ovpn from assets
bongoVpn.attachFromAsset("japan.ovpn", "vpn_user", "vpn_pass")

//Or pass string directly
// bongoVpn.attachFromString("your_entire_ovpn_file_as_string", "vpn_user", "vpn_pass")

// Setup event callbacks
bongoVpn.setVpnListener(object : BongoVpn.VpnListener {
    override fun onVpnConnected() {
        // Triggered when tunnel is established
    }

    override fun onVpnStopped() {
        // Triggered when connection is disconnected
    }

    override fun onStatusUpdate(status: String) {
        // Triggered during connection state changes
    }

    override fun onError(errorMessage: String) {
        // Triggered on connection/auth errors
    }

    override fun onSpeedUpdate(
        downloadBytes: Long,
        uploadBytes: Long,
        downloadSpeed: Long,
        uploadSpeed: Long
    ) {
        // Live bandwidth stats & speeds
        val speed = BongoVpn.formatSpeed(downloadSpeed)
        val usage = BongoVpn.formatBytes(downloadBytes)
    }
})
```

### 3. Connect, Disconnect & Cleanup
```kotlin
// Connect
private fun connectVpn() {
    if (bongoVpn.hasVpnPermission()) {
        bongoVpn.startVpn()
    } else {
        bongoVpn.requestVpnPermission()
    }
}

// Disconnect
private fun stopVpn() {
    bongoVpn.stopVpn()
}

// Cleanup
override fun onDestroy() {
    super.onDestroy()
    bongoVpn.release()
}
```

</details>


---

## 🤝 Credits & Acknowledgements

BongoVPN is crafted and maintained by **Bongo Tech**.

Special recognition and credit to:
* **[ics-openvpn](https://github.com/schwabe/ics-openvpn)** by Arne Schwabe — the robust foundational OpenVPN core implementation for Android that powers modern mobile tunneling.
* The open-source OpenVPN community for maintaining world-class security and protocol standards.

---

## 📄 License

```text
Copyright 2026 Bongo Tech

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
