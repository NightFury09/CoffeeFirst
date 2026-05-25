# ☕ CoffeeFirst: Custom Coffee Machine HMI

[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-1.5.4-green.svg)](https://developer.android.com/jetpack/compose)
[![Platform](https://img.shields.io/badge/Platform-Quectel_SC200EE-orange.svg)](https://www.quectel.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An elegant, high-fidelity Human-Machine Interface (HMI) Android application designed for capacitive touch display panels (1280x800 resolution) in landscape mode, running on the **Quectel SC200EE** platform (Android 12/13/14). Built entirely using **Jetpack Compose**, this project showcases a modern, minimalist dark espresso design with custom animations, interactive parameters, and robust state machine management.

---

## 🎨 Interactive User Interface

Here is a preview of the main HMI design layouts:

### 🖥️ Beverage Selection Dashboard
![HMI Dashboard Mockup](file:///C:/Users/akhil/.gemini/antigravity-ide/brain/d281b51b-97b2-4b85-be11-2af74a44257f/hmi_dashboard_1779691945616.png)
*A sleek, glassmorphic layout displaying available beverage configurations (Espresso, Cappuccino, Latte) with high-end typography and interactive selections.*

### ⏳ Real-time Brewing & Progress View
![HMI Brewing Interface](file:///C:/Users/akhil/.gemini/antigravity-ide/brain/d281b51b-97b2-4b85-be11-2af74a44257f/hmi_brewing_1779691966861.png)
*Dynamic progress display featuring a glowing fill animation and clean state notifications during the mock dispensing process.*

---

## ⚙️ Target Specifications & Display

* **Target Hardware:** Quectel SC200EE Smart Module (Qualcomm QCM2290 SoC, Adreno 702 GPU)
* **Screen Resolution:** 1280 × 800 (Landscape Mode)
* **Touch Panel:** Capacitive Multi-touch
* **Design Theme:** Dark Espresso (`#3E2723`) with Caramel Accent (`#FF8F00`) and Golden Highlights
* **Kiosk Configuration:** Immersive Fullscreen Mode (System Navigation & Status Bars hidden)

---

## 🚀 Key Features

* **Beverage Catalog:** Options for Espresso, Americano, Latte, Cappuccino, Macchiato, Hot Water, and Steam.
* **Customization Interface:** Adjust beverage volume, coffee strength, milk froth levels, and temperature through interactive slider components.
* **Mock State Machine:** Models realistic hardware steps like `Heating`, `Grinding`, `Brewing`, and `Dispensing` with clean transition timers.
* **Advanced Diagnostics:**
  * **Motor Settings & Movement Screen:** Control and test physical grinder, pump, and brewer motor parameters. Includes simulated RPM and current draw monitors.
  * **Factory & Maintenance Settings:** Calibrate sensors, clean coffee outlets, view error logs, and manage technical parameters behind a PIN security check.
* **Auto-Attract Loop:** Idle detection automatically triggers an elegant rotating screensaver/attract loop after periods of inactivity.

---

## 🛠️ Build & Installation

### Prerequisites
* **Android Studio Koala / Ladybug** or newer
* **Android SDK 31+** (Android 12+)
* **Gradle 8.0+**

### Local Setup & Compilation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/NightFury09/CoffeeFirst.git
   cd CoffeeFirst
   ```

2. **Build the Application via Gradle Wrapper:**
   * **Windows:**
     ```cmd
     gradlew.bat assembleDebug
     ```
   * **macOS / Linux:**
     ```bash
     ./gradlew assembleDebug
     ```

3. **Install the APK via ADB:**
   Connect your Quectel EVB or Android device and run:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🏗️ Architecture & Navigation

This application implements a **Single-Activity Architecture** utilizing Jetpack Compose's Navigation component:

* **[MainActivity](file:///c:/AJ/qt_app/qt_app/app/src/main/kotlin/com/coffeehmi/app/MainActivity.kt):** Sets full-screen kiosk attributes (forces landscape, hides system status and navigation bars) and initializes the theme context.
* **[NavGraph](file:///c:/AJ/qt_app/qt_app/app/src/main/kotlin/com/coffeehmi/app/ui/NavGraph.kt):** Governs transitions across all UI states:
  ```
  [Splash] ──→ [Idle Attract Loop] ──→ [Main Selection] 
                                              │
                      ┌───────────────────────┼───────────────────────┐
                      ↓                       ↓                       ↓
                [Customization]        [Settings Screen]      [Maintenance & Tech]
                      │                       │                       │
              [Brewing Flow]          [Factory Settings]      [Motor Diagnostics]
                      │
              [Completion Timer] ──→ (Timeout) ──→ [Back to Idle]
  ```

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
