package com.akshithtextfreq

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.PI
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private val sampleRate = 44100

    // ---------- PROTOCOL (MATCH RECEIVER) ----------
    private val START_BIT_FREQ = 1205.0
    private val STOP_BIT_FREQ  = 1505.0

    private val LETTER_START_FREQ = 1220.0
    private val BIN_WIDTH = 10.0
    private val BIN_CENTER_OFFSET = 5.0

    // ---------- TIMING (LOW DELAY, SAFE) ----------
    private val toneTimeMs = 180     // > WINDOW_MS (100)
    private val silenceTimeMs = 120  // forces bin change

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.sendButton)

        button.setOnClickListener {
            val text = input.text.toString().uppercase()
            Thread {
                playEncodedAudio(text)
            }.start()
        }
    }

    private fun playEncodedAudio(text: String) {
        val buffer = ArrayList<Short>()

        fun emitTone(freqHz: Double, durationMs: Int) {
            val samples = (sampleRate * durationMs / 1000.0).toInt()
            for (i in 0 until samples) {
                val v = sin(2.0 * PI * freqHz * i / sampleRate)
                buffer.add((v * Short.MAX_VALUE * 0.45).toInt().toShort())
            }
        }

        fun emitSilence(durationMs: Int) {
            val samples = (sampleRate * durationMs / 1000.0).toInt()
            repeat(samples) { buffer.add(0) }
        }

        // ---------- START BIT ----------
        emitTone(START_BIT_FREQ, toneTimeMs)
        emitSilence(silenceTimeMs)

        // ---------- DATA ----------
        for (ch in text) {
            val freq = when {
                ch in 'A'..'Z' ->
                    LETTER_START_FREQ +
                            (ch - 'A') * BIN_WIDTH +
                            BIN_CENTER_OFFSET

                ch == ' ' ->
                    LETTER_START_FREQ +
                            26 * BIN_WIDTH +
                            BIN_CENTER_OFFSET

                else -> -1.0
            }

            if (freq > 0) {
                emitTone(freq, toneTimeMs)
                emitSilence(silenceTimeMs)
            }
        }

        // ---------- STOP BIT ----------
        emitTone(STOP_BIT_FREQ, toneTimeMs)
        emitSilence(silenceTimeMs)

        // ---------- PLAY ----------
        val track = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            buffer.size * 2,
            AudioTrack.MODE_STATIC
        )

        track.write(buffer.toShortArray(), 0, buffer.size)
        track.play()
    }
}