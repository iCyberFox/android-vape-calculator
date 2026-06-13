package com.bmweast.vapecalc.ui

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bmweast.vapecalc.R

class BaseFragment : Fragment() {

    private lateinit var etVolume1: EditText
    private lateinit var etNic1: EditText
    private lateinit var etVG1: EditText
    private lateinit var etPG1: EditText
    private lateinit var seekBar1: SeekBar
    private lateinit var tvSlider1Label: TextView

    private lateinit var etVolume2: EditText
    private lateinit var etNic2: EditText
    private lateinit var etVG2: EditText
    private lateinit var etPG2: EditText
    private lateinit var seekBar2: SeekBar
    private lateinit var tvSlider2Label: TextView

    private lateinit var btnCalculate: Button
    private lateinit var resultContainer: LinearLayout
    private lateinit var tvResultContent: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etVolume1 = view.findViewById(R.id.etVolume1)
        etNic1 = view.findViewById(R.id.etNic1)
        etVG1 = view.findViewById(R.id.etVG1)
        etPG1 = view.findViewById(R.id.etPG1)
        seekBar1 = view.findViewById(R.id.seekBar1)
        tvSlider1Label = view.findViewById(R.id.tvSlider1Label)

        etVolume2 = view.findViewById(R.id.etVolume2)
        etNic2 = view.findViewById(R.id.etNic2)
        etVG2 = view.findViewById(R.id.etVG2)
        etPG2 = view.findViewById(R.id.etPG2)
        seekBar2 = view.findViewById(R.id.seekBar2)
        tvSlider2Label = view.findViewById(R.id.tvSlider2Label)

        btnCalculate = view.findViewById(R.id.btnCalculate)
        resultContainer = view.findViewById(R.id.resultContainer)
        tvResultContent = view.findViewById(R.id.tvResultContent)

        setupSeekBars()
        setupInputSync()

        btnCalculate.setOnClickListener { calculate() }
    }

    private fun setupSeekBars() {
        seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val pg = progress
                    val vg = 100 - progress
                    etVG1.setText(vg.toString())
                    etPG1.setText(pg.toString())
                    tvSlider1Label.text = "$vg/$pg"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val pg = progress
                    val vg = 100 - progress
                    etVG2.setText(vg.toString())
                    etPG2.setText(pg.toString())
                    tvSlider2Label.text = "$vg/$pg"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupInputSync() {
        // Simple sync for Base 1
        etVG1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val vg = s.toString().toIntOrNull() ?: return
                if (vg in 0..100) {
                    val pg = 100 - vg
                    if (etPG1.text.toString() != pg.toString()) {
                        etPG1.setText(pg.toString())
                        seekBar1.progress = pg
                        tvSlider1Label.text = "$vg/$pg"
                    }
                }
            }
        })
    }

    private fun calculate() {
        val v1 = etVolume1.text.toString().toDoubleOrNull() ?: 0.0
        val n1 = etNic1.text.toString().toDoubleOrNull() ?: 0.0
        val vg1 = etVG1.text.toString().toDoubleOrNull() ?: 0.0
        val pg1 = etPG1.text.toString().toDoubleOrNull() ?: 0.0

        val v2 = etVolume2.text.toString().toDoubleOrNull() ?: 0.0
        val n2 = etNic2.text.toString().toDoubleOrNull() ?: 0.0
        val vg2 = etVG2.text.toString().toDoubleOrNull() ?: 0.0
        val pg2 = etPG2.text.toString().toDoubleOrNull() ?: 0.0

        if (v1 <= 0 && v2 <= 0) {
            Toast.makeText(requireContext(), "Введіть об'єм", Toast.LENGTH_SHORT).show()
            return
        }

        val totalVol = v1 + v2
        val finalNic = (v1 * n1 + v2 * n2) / totalVol
        val finalVG = (v1 * vg1 + v2 * vg2) / totalVol
        val finalPG = (v1 * pg1 + v2 * pg2) / totalVol

        val resultsHtml = StringBuilder()
        resultsHtml.append("<b>Загальний об'єм:</b> ${"%.2f".format(totalVol)} мл<br>")
        resultsHtml.append("<b>Кінцеве співвідношення:</b> ${"%.1f".format(finalVG)} VG / ${"%.1f".format(finalPG)} PG<br>")
        resultsHtml.append("<b>Кінцева міцність:</b> ${"%.2f".format(finalNic)} мг/мл")

        tvResultContent.text = Html.fromHtml(resultsHtml.toString(), Html.FROM_HTML_MODE_COMPACT)
        resultContainer.visibility = View.VISIBLE
    }
}
