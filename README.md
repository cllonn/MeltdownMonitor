# MeltdownMonitor

MeltdownMonitor is a wearable AI-powered system designed to predict and prevent meltdown episodes in children with behavioral disorders. By combining physiological sensing with machine learning, the system provides real-time predictions and alerts to caregivers, enabling proactive intervention and reducing risk for both the child and their surroundings.

## Target Problem

Children with behavioral disorders often experience meltdowns that are difficult to anticipate. Current solutions are mostly reactive, leading to caregiver stress and missed opportunities for early intervention. MeltdownMonitor addresses this by predicting distress episodes up to 30 seconds in advance based on physiological indicators.

## Objectives

- Predict meltdown episodes using real-time sensor data.
- Deliver early alerts to caregivers via a mobile app.
- Ensure comfort and wearability for sensory-sensitive children.

## System Overview

MeltdownMonitor consists of:

- **Wearable Vest** with:
  - Heart Rate Sensor
  - Temperature Sensor
  - Galvanic Skin Response (GSR) Sensor
  - ESP32-WROOM-DA for data processing and Wi-Fi transmission
- **Mobile Application** developed in Android Studio (Jetpack Compose)
- **Firebase** for real-time data storage and user management
- **Machine Learning Model** trained using Random Forest Classifier in Python (Jupyter Notebook), optimized with TensorFlow Lite for mobile deployment

![System Architecture](https://github.com/cllonn/MeltdownMonitor/Images/system_architecture.png)
![Poster](https://github.com/cllonn/MeltdownMonitor/Images/poster.png)

## Machine Learning

- **Algorithm Used**: Random Forest Classifier
- **Metrics**: 100% Accuracy, Precision, Recall (based on controlled test data)
- **Features**:
  - Heart rate (BPM)
  - Skin temperature
  - GSR (sweat levels)

Data was collected via wearable sensors and logged using Google Sheets and Firebase. The model predicts escalation patterns and triggers alerts before visible signs emerge.

## Mobile App Features

- Real-time monitoring dashboard
- Caregiver alerts with intervention suggestions
- User-friendly design built with Jetpack Compose
- Firebase authentication and secure data handling

## Tech Stack

| Component       | Tool/Technology             |
|----------------|-----------------------------|
| Hardware        | ESP32-WROOM-DA, sensors     |
| Firmware        | Arduino C/C++               |
| ML Development  | Python, Jupyter, TensorFlow |
| App Development | Kotlin, Jetpack Compose     |
| Backend         | Firebase Realtime Database  |

## Results

- Achieved 80% real-world early prediction accuracy during pilot testing
- 100% performance in controlled testing with labeled datasets
- Successfully tested with a real child subject (with parental consent)

## Key Contributions

- Real-time physiological data acquisition via embedded systems
- End-to-end IoT integration from sensors to mobile app
- Machine learning-driven predictive analytics for behavioral health
- Intuitive UX for non-technical caregivers

## Documentation

- [Final Report (PDF)](link-if-available)
- [Poster and Slide Deck](link-if-available)

## Repository Link

All code is public here:  
https://github.com/cllonn/MeltdownMonitor
Visit the Master Branch to view the Application!

---

## Acknowledgments

This project was developed as part of the final year capstone at United Arab Emirates University by:

- Sara Almarzooqi (Computer Engineering & AI Minor)
- Rafeea Alahbabi
- Ameera Alyafei
- Alreem Alghfeli
- Sumaya Alshamsi  
Advisor: Dr. Fady Alnajjar

---

## Future Work

- Expand training dataset across diverse subjects and behavioral patterns
- Improve accuracy using advanced deep learning models
- Explore wearable miniaturization and cloud AI deployment

