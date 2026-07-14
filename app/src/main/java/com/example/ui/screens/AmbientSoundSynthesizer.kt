package com.example.ui.screens

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.sin

class AmbientSoundSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var currentVolume = 0.7f

    fun startPlaying(type: String, volume: Float) {
        stopPlaying()
        currentVolume = volume
        
        job = scope.launch {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build()

            val track = AudioTrack(
                attributes,
                format,
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            
            audioTrack = track
            track.setVolume(currentVolume)
            
            try {
                track.play()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }

            val buffer = ShortArray(bufferSize)
            val random = Random()
            var lastOut = 0.0f
            var phase = 0.0

            while (isActive) {
                for (i in buffer.indices) {
                    when (type) {
                        "white" -> {
                            // White noise: pure Gaussian random
                            val white = random.nextGaussian().toFloat() * 0.12f
                            buffer[i] = (white * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                        "rain" -> {
                            // Brown/Red noise (deep, rain-like): low-pass filtered white noise
                            val white = random.nextGaussian().toFloat() * 0.12f
                            // First-order low-pass filter (Brown noise approximation)
                            lastOut = (lastOut + (0.02f * white)) / 1.02f
                            // Scale for volume and convert to 16-bit PCM
                            buffer[i] = (lastOut * 4.0f * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                        "ocean" -> {
                            // Ocean waves: brown-ish noise with slow amplitude modulation (sine wave)
                            val white = random.nextGaussian().toFloat() * 0.12f
                            lastOut = (lastOut + (0.05f * white)) / 1.05f
                            phase += 2.0 * Math.PI / (sampleRate * 5) // 5 seconds cycle for gentle wave
                            val modulation = (sin(phase) + 1.0) / 2.0 // scale between 0.0 and 1.0
                            val sample = lastOut * 2.5f * (0.2f + 0.8f * modulation.toFloat())
                            buffer[i] = (sample * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                        "forest" -> {
                            // Gentle rustling wind: very low-pass filtered white noise with slow random modulation
                            val white = random.nextGaussian().toFloat() * 0.1f
                            lastOut = (lastOut + (0.01f * white)) / 1.01f
                            phase += 2.0 * Math.PI / (sampleRate * 8) // 8 seconds cycle
                            val modulation = (sin(phase) + 1.0) / 2.0
                            val sample = lastOut * 3.0f * (0.3f + 0.7f * modulation.toFloat())
                            buffer[i] = (sample * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                        "fire" -> {
                            // Campfire: low-pass rumbling background noise + random crackles (high frequency pops)
                            val white = random.nextGaussian().toFloat() * 0.08f
                            lastOut = (lastOut + (0.015f * white)) / 1.015f
                            var sample = lastOut * 2.0f
                            // Generate random crackle (1 in ~1500 samples)
                            if (random.nextFloat() > 0.9995f) {
                                val crackle = (random.nextFloat() * 2.0f - 1.0f) * 0.6f
                                sample += crackle
                            }
                            buffer[i] = (sample * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                }
                try {
                    track.write(buffer, 0, buffer.size)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume
        scope.launch {
            audioTrack?.let {
                try {
                    it.setVolume(volume)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopPlaying() {
        val activeJob = job
        job = null
        activeJob?.cancel()

        val activeTrack = audioTrack
        audioTrack = null

        scope.launch {
            try {
                activeTrack?.apply {
                    stop()
                    release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
