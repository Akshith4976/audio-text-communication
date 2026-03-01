# Audio-Based Text Communication

This project demonstrates text communication using sound frequencies instead of traditional data links.

An Android application encodes text as audio tones.  
A NodeMCU (ESP8266) with a microphone and LM358 preamplifier listens to the sound, detects the frequencies in real time, and decodes them back into readable text.

The system uses a simple frequency-based protocol with start and stop tones to frame each message.

This project is inspired by early acoustic modems and audio-based communication systems.

---

## How It Works

- Each character is mapped to a specific frequency range.
- The Android app generates audio tones corresponding to the input text.
- The receiver samples the microphone signal and estimates the frequency using zero-crossing detection.
- Start and stop tones indicate the beginning and end of a message.
- Detected frequencies are converted back into characters and displayed on the serial monitor.

---

## Components Used

**Hardware**
- NodeMCU (ESP8266)
- LM358 op-amp (microphone preamplifier)
- Electret microphone
- External power supply

**Software**
- Android app (Kotlin)
- Arduino IDE
- Serial Monitor

---

## Features

- Real-time text decoding
- Frequency-based character encoding
- Start/stop framing for reliable message detection
- Works with arbitrary text input
- No wired data connection required

---

## Limitations & Future Work

- Environmental noise affects reliability at higher symbol rates.
- FSK-based communication was explored but postponed due to analog front-end noise.
- Future improvements include better filtering, Goertzel-based frequency detection, and checksum-based error detection.

---

## Inspiration

This project was inspired by early acoustic modem systems and experiments in audio-based communication.

---

## Author

Akshith
