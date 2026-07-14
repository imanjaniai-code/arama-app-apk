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

            // Chime & harmonic states for advanced synthesis
            val chimeFreqs = doubleArrayOf(396.0, 417.0, 528.0, 639.0, 741.0, 852.0)
            val chimeAmps = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            val chimeDecays = doubleArrayOf(0.99992, 0.99994, 0.99995, 0.99993, 0.99992, 0.99991)
            var chimeTimer = 0

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
                        "zen" -> {
                            // Solfeggio healing frequencies & pulsing zen music drone (432Hz & 528Hz)
                            val sampleRateFloat = sampleRate.toFloat()
                            phase += 1.0 / sampleRateFloat
                            
                            // LFOs for deep breathing volume modulations
                            val lfo1 = (sin(2.0 * Math.PI * 0.05 * phase) + 1.0) / 2.0 // 20s wave
                            val lfo2 = (sin(2.0 * Math.PI * 0.03 * phase) + 1.0) / 2.0 // 33s wave
                            
                            val w1 = sin(2.0 * Math.PI * 136.1 * phase) * 0.32 * (0.4 + 0.6 * lfo1)
                            val w2 = sin(2.0 * Math.PI * 272.2 * phase) * 0.18 * (0.3 + 0.7 * lfo2)
                            val w3 = sin(2.0 * Math.PI * 528.0 * phase) * 0.12 * (0.2 + 0.8 * lfo1 * lfo2)
                            
                            // Add ocean wind whispering in the background
                            val white = random.nextGaussian().toFloat() * 0.012f
                            lastOut = (lastOut + (0.008f * white)) / 1.008f
                            
                            val sample = (w1 + w2 + w3).toFloat() + lastOut
                            buffer[i] = (sample * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                        "chimes" -> {
                            // Tibetan wind chimes on Solfeggio frequencies over soft healing sound pad
                            val sampleRateFloat = sampleRate.toFloat()
                            phase += 1.0 / sampleRateFloat
                            
                            chimeTimer++
                            if (chimeTimer >= sampleRate * 2.5) {
                                chimeTimer = 0
                                if (random.nextFloat() > 0.35f) {
                                    val idx = random.nextInt(chimeFreqs.size)
                                    chimeAmps[idx] = 0.15 + random.nextFloat() * 0.18
                                }
                            }
                            
                            var chimeSum = 0.0
                            for (j in chimeFreqs.indices) {
                                if (chimeAmps[j] > 0.001) {
                                    chimeSum += sin(2.0 * Math.PI * chimeFreqs[j] * phase) * chimeAmps[j]
                                    chimeAmps[j] *= chimeDecays[j]
                                }
                            }
                            
                            val white = random.nextGaussian().toFloat() * 0.01f
                            lastOut = (lastOut + (0.02f * white)) / 1.02f
                            
                            val sample = chimeSum.toFloat() + lastOut
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
