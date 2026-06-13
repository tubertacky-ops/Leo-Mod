package com.leo.bgmimod.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.leo.bgmimod.R
import com.leo.bgmimod.services.ModLoaderService

class ModMenuFragment : Fragment() {
    private var modService: ModLoaderService? = null
    private var espEnabled = false
    private var aimbotEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mod_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ESP Toggle
        val espButton: Button = view.findViewById(R.id.btn_esp)
        espButton.setOnClickListener {
            espEnabled = !espEnabled
            if (espEnabled) {
                modService?.enableESP()
                espButton.text = "ESP: ON"
                espButton.setBackgroundColor(0xFF00FF00.toInt())
                Toast.makeText(context, "ESP Enabled", Toast.LENGTH_SHORT).show()
            } else {
                modService?.disableESP()
                espButton.text = "ESP: OFF"
                espButton.setBackgroundColor(0xFFFF0000.toInt())
                Toast.makeText(context, "ESP Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Aimbot Toggle
        val aimbotButton: Button = view.findViewById(R.id.btn_aimbot)
        aimbotButton.setOnClickListener {
            aimbotEnabled = !aimbotEnabled
            if (aimbotEnabled) {
                modService?.enableAimbot()
                aimbotButton.text = "Aimbot: ON"
                aimbotButton.setBackgroundColor(0xFF00FF00.toInt())
                Toast.makeText(context, "Aimbot Enabled", Toast.LENGTH_SHORT).show()
            } else {
                modService?.disableAimbot()
                aimbotButton.text = "Aimbot: OFF"
                aimbotButton.setBackgroundColor(0xFFFF0000.toInt())
                Toast.makeText(context, "Aimbot Disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}