package com.bmweast.vapecalc.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bmweast.vapecalc.R
import kotlin.math.*

class CoilFragment : Fragment() {

    private lateinit var rgWires: RadioGroup
    private lateinit var rgCoils: RadioGroup
    private lateinit var rgType: RadioGroup
    private lateinit var spinnerWireDiam: Spinner
    private lateinit var spinnerWireType: Spinner
    private lateinit var spinnerCoilDiam: Spinner
    private lateinit var spinnerWindings: Spinner
    private lateinit var spinnerLegs: Spinner
    private lateinit var cbTwisted: CheckBox
    private lateinit var tvTCR: TextView
    private lateinit var layoutClapton: View
    private lateinit var spinnerClapWireDiam: Spinner
    private lateinit var spinnerClapWireType: Spinner
    private lateinit var etVoltage: EditText
    private lateinit var cbOhmCorrection: CheckBox
    private lateinit var btnCalculate: Button
    
    private lateinit var resultContainer: View
    private lateinit var tvCoolHot: TextView
    private lateinit var tvPowerValue: TextView
    private lateinit var tvOptPower: TextView
    private lateinit var tvResistance: TextView
    private lateinit var tvWireLength: TextView
    private lateinit var tvCurrent: TextView
    private lateinit var tvPowDen: TextView
    private lateinit var tvCoWidth: TextView
    private lateinit var tvTemperature: TextView

    // Data from HTML
    private val wireDiamsData = linkedMapOf(
        "0.10 мм (AWG 38)" to 0.10, "0.11 мм (AWG 37)" to 0.11, "0.12 мм (AWG 36)" to 0.12,
        "0.14 мм (AWG 35)" to 0.14, "0.15 мм" to 0.15, "0.16 мм (AWG 34)" to 0.16,
        "0.18 мм (AWG 33)" to 0.18, "0.20 мм (AWG 32)" to 0.20, "0.22 мм (AWG 31)" to 0.22,
        "0.25 мм (AWG 30)" to 0.25, "0.28 мм (AWG 29)" to 0.28, "0.30 мм" to 0.30,
        "0.32 мм (AWG 28)" to 0.32, "0.35 мм" to 0.35, "0.36 мм (AWG 27)" to 0.36,
        "0.38 мм" to 0.38, "0.40 мм (AWG 26)" to 0.40, "0.45 мм (AWG 25)" to 0.45,
        "0.50 мм" to 0.50, "0.51 мм (AWG 24)" to 0.51, "0.57 мм (AWG 23)" to 0.57,
        "0.60 мм" to 0.60, "0.63 мм" to 0.63, "0.64 мм (AWG 22)" to 0.64,
        "0.70 мм" to 0.70, "0.71 мм (AWG 21)" to 0.71, "0.80 мм" to 0.80,
        "0.81 мм (AWG 20)" to 0.81, "0.90 мм" to 0.90, "0.91 мм (AWG 19)" to 0.91,
        "1.0 мм" to 1.0, "1.02 мм (AWG 18)" to 1.02
    )

    private val coilDiamsData = listOf(2.0, 2.25, 2.5, 2.75, 3.0, 3.5, 4.0, 4.5, 5.0)
    private val windingsList = (2..40).map { it / 2.0 } // 1.0 to 20.0 with 0.5 steps
    private val legsList = listOf(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0)

    private val materialsData = linkedMapOf(
        "Nickel 200 (Ni)" to 0.09, "NiFe30" to 0.24, "Dicodes Resist" to 0.28,
        "NiFe48" to 0.36, "Titanium (Ti)" to 0.42, "SS AISI 316" to 0.74,
        "SS AISI 304" to 0.8, "Nichrome Ni80" to 1.08, "Nichrome Ni60" to 1.11,
        "Фехраль" to 1.39, "Kanthal D" to 1.35, "Kanthal A1" to 1.45
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_coil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rgWires = view.findViewById(R.id.rgWires)
        rgCoils = view.findViewById(R.id.rgCoils)
        rgType = view.findViewById(R.id.rgType)
        spinnerWireDiam = view.findViewById(R.id.spinnerWireDiam)
        spinnerWireType = view.findViewById(R.id.spinnerWireType)
        spinnerCoilDiam = view.findViewById(R.id.spinnerCoilDiam)
        spinnerWindings = view.findViewById(R.id.spinnerWindings)
        spinnerLegs = view.findViewById(R.id.spinnerLegs)
        cbTwisted = view.findViewById(R.id.cbTwisted)
        tvTCR = view.findViewById(R.id.tvTCR)
        layoutClapton = view.findViewById(R.id.layoutClapton)
        spinnerClapWireDiam = view.findViewById(R.id.spinnerClapWireDiam)
        spinnerClapWireType = view.findViewById(R.id.spinnerClapWireType)
        etVoltage = view.findViewById(R.id.etVoltage)
        cbOhmCorrection = view.findViewById(R.id.cbOhmCorrection)
        btnCalculate = view.findViewById(R.id.btnCalculate)

        resultContainer = view.findViewById(R.id.resultContainer)
        tvCoolHot = view.findViewById(R.id.tvCoolHot)
        tvPowerValue = view.findViewById(R.id.tvPowerValue)
        tvOptPower = view.findViewById(R.id.tvOptPower)
        tvResistance = view.findViewById(R.id.tvResistance)
        tvWireLength = view.findViewById(R.id.tvWireLength)
        tvCurrent = view.findViewById(R.id.tvCurrent)
        tvPowDen = view.findViewById(R.id.tvPowDen)
        tvCoWidth = view.findViewById(R.id.tvCoWidth)
        tvTemperature = view.findViewById(R.id.tvTemperature)

        setupSpinners()

        rgType.setOnCheckedChangeListener { _, checkedId ->
            layoutClapton.visibility = if (checkedId == R.id.ct3) View.VISIBLE else View.GONE
        }

        spinnerWireType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateTCR()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnCalculate.setOnClickListener { compute() }
    }

    private fun updateTCR() {
        val materialKey = materialsData.keys.toList()[spinnerWireType.selectedItemPosition]
        val rho = materialsData[materialKey] ?: 1.08
        val tcr = when (rho) {
            0.09 -> "0.00520"
            0.24 -> "0.00506"
            0.28 -> "0.00320"
            0.36 -> "0.00405"
            0.42 -> "0.00350"
            0.74 -> "0.00095"
            0.8 -> "0.00105"
            1.08 -> "0.00012"
            1.11 -> "0.00018"
            1.39 -> "0.00004"
            1.35 -> "0.00005"
            1.45 -> "0.00001"
            else -> " "
        }
        tvTCR.text = "TCR: $tcr"
    }

    private fun setupSpinners() {
        val ctx = requireContext()
        
        val diamLabels = wireDiamsData.keys.toList()
        spinnerWireDiam.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, diamLabels)
        spinnerWireDiam.setSelection(diamLabels.indexOf("0.32 мм (AWG 28)"))
        
        spinnerClapWireDiam.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, diamLabels)
        
        val materialLabels = materialsData.keys.toList()
        spinnerWireType.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, materialLabels)
        spinnerWireType.setSelection(materialLabels.indexOf("Nichrome Ni80"))
        
        spinnerClapWireType.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, materialLabels)
        spinnerClapWireType.setSelection(materialLabels.indexOf("Nichrome Ni80"))

        spinnerCoilDiam.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, coilDiamsData.map { "$it мм" })
        spinnerCoilDiam.setSelection(2) // 2.5 mm

        spinnerWindings.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, windingsList.map { it.toString() })
        spinnerWindings.setSelection(8) // 5 windings

        spinnerLegs.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, legsList.map { "2x$it мм" })
        spinnerLegs.setSelection(legsList.indexOf(5.0))
        
        updateTCR()
    }

    private fun compute() {
        val pi = 3.14159265359
        val ohmCorrect = 1.1

        val wiresNum = when(rgWires.checkedRadioButtonId) {
            R.id.wn1 -> 1; R.id.wn2 -> 2; R.id.wn3 -> 3; R.id.wn4 -> 4; R.id.wn5 -> 5; R.id.wn6 -> 6; else -> 1
        }
        val coilsNum = when(rgCoils.checkedRadioButtonId) {
            R.id.cn1 -> 1; R.id.cn2 -> 2; R.id.cn3 -> 3; R.id.cn4 -> 4; else -> 1
        }
        
        val coilType = when(rgType.checkedRadioButtonId) {
            R.id.ct2 -> 2; R.id.ct1 -> 1; R.id.ct3 -> 3; else -> 1
        }
        
        val wireDiamKey = wireDiamsData.keys.toList()[spinnerWireDiam.selectedItemPosition]
        val c3 = wireDiamsData[wireDiamKey] ?: 0.32
        val c4 = coilDiamsData[spinnerCoilDiam.selectedItemPosition]
        val windingsNum = windingsList[spinnerWindings.selectedItemPosition]
        val voltage = etVoltage.text.toString().toDoubleOrNull() ?: 3.7
        val legsLen = legsList[spinnerLegs.selectedItemPosition]
        
        val wireTypeKey = materialsData.keys.toList()[spinnerWireType.selectedItemPosition]
        val c8 = materialsData[wireTypeKey] ?: 1.08
        
        val doOhmCorrection = cbOhmCorrection.isChecked
        val twisted = cbTwisted.isChecked

        val clapWireDiamKey = wireDiamsData.keys.toList()[spinnerClapWireDiam.selectedItemPosition]
        val cldiam = wireDiamsData[clapWireDiamKey] ?: 0.10
        val clapWireTypeKey = materialsData.keys.toList()[spinnerClapWireType.selectedItemPosition]
        val cltype = materialsData[clapWireTypeKey] ?: 1.08

        // Logic from JS
        val rArea = pi * (c3 / 2.0).pow(2)
        val avDiam = c3 + c4
        
        // r_wirelength calculation
        var rWireLength = sqrt((pi * avDiam).pow(2) + (c3 * wiresNum * coilType).pow(2)) * windingsNum + (legsLen * 2)
        
        if (twisted && wiresNum >= 2) {
            rWireLength *= 1.2
        }
        if (wiresNum == 1) {
            cbTwisted.isChecked = false
        }

        // Clapton logic
        val clpWireLength = round(c3 * pi * (rWireLength / cldiam) * (wiresNum / 10.0 * 6.0 + 0.4))
        val clpArea = pi * (cldiam / 2.0).pow(2)
        val clpResist = (cltype * clpWireLength / clpArea / 100.0)

        // Spiral resistance
        var rResist = (c8 * (rWireLength + (wiresNum * c3)) / rArea / 1000.0) / (coilsNum * wiresNum)
        if (doOhmCorrection) {
            rResist *= ohmCorrect
        }
        
        if (coilType == 3) { // Clapton
            rResist = (rResist * clpResist) / (rResist + clpResist)
        }

        val power = voltage.pow(2) / rResist
        val current = voltage / rResist
        val rCoWidth = wiresNum * (c3 * coilType) * windingsNum
        
        // Power density
        val rPowDen = power / ((pi * 2.0) * ((avDiam / 2.0) * ((c3 * 2.0 * (coilsNum * wiresNum * 1.8)) * windingsNum)))
        
        val mmRas = (((pi * 2.0) * ((avDiam / 2.0) * ((c3 * 2.0 * (coilsNum * wiresNum * 1.8)) * windingsNum)))) * 0.3
        var koef = (43.0 - mmRas) / 100.0
        if (koef <= 0.2) koef = 0.2
        
        val rOptPower = ((pi * 2.0) * ((avDiam / 2.0) * ((c3 * 2.0 * (coilsNum * wiresNum * 1.8)) * windingsNum))) * koef

        // Temperature
        val aResist = (c8 * (rWireLength + (wiresNum * c3)) / rArea / 1000.0) / wiresNum.toDouble()
        val correctedAResist = if (doOhmCorrection) aResist * ohmCorrect else aResist
        val aCurrent = voltage / correctedAResist
        val rTempK = ((voltage * aCurrent) / (0.31 * pi * 5.67 * 1e-8 * c3 * 1e-3 * rWireLength * 1e-3)).pow(0.25)

        // Status logic
        var status = "Оптимальна"
        var statusColor = Color.parseColor("#00C800")
        if (rPowDen >= 0.40) {
            status = "Висока"
            statusColor = Color.parseColor("#C8B400")
        }
        if (rPowDen >= 0.45) {
            status = "Перегрів"
            statusColor = Color.parseColor("#C80046")
        }
        if (rPowDen <= 0.2) {
            status = "Недостатня"
            statusColor = Color.parseColor("#6464C8")
        }

        // Power background color logic from JS
        var cR = 0
        var cG = ((170 - (20 / rPowDen)) * 2).roundToInt()
        var cB = 0
        if (rPowDen <= 0.17) cG = 100
        if (rPowDen <= 0.24) cB = ((20 / rPowDen) * 2).roundToInt()
        if (rPowDen >= 0.35) {
            cR = ((70 - (20 / rPowDen)) * 10).roundToInt()
            cG = ((100 - rPowDen * 200) * 10).roundToInt()
        }
        cR = cR.coerceIn(0, 230); cG = cG.coerceIn(0, 200); cB = cB.coerceIn(0, 220)
        tvPowerValue.setBackgroundColor(Color.rgb(cR, cG, cB))

        var overrateStr = ""
        if (power > rOptPower * 2) {
            overrateStr = " x${(power / rOptPower).roundToInt()}"
        }

        tvCoolHot.text = status + overrateStr
        tvCoolHot.setTextColor(statusColor)
        tvPowerValue.text = "${"%.2f".format(power)} Watt"
        tvOptPower.text = "рекомендована: ${"%.2f".format(rOptPower)} Watt"

        tvResistance.text = "${"%.2f".format(rResist)} Ω"
        tvWireLength.text = "${"%.2f".format(rWireLength)} мм х ${wiresNum * coilsNum}"
        tvCurrent.text = "${"%.2f".format(current)} Amp"
        tvPowDen.text = "${"%.2f".format(rPowDen)} W/мм²"
        tvCoWidth.text = "${"%.1f".format(rCoWidth)} мм"

        val tempRGB = colorTemperatureToRGB(rTempK)
        tvTemperature.setBackgroundColor(Color.rgb(tempRGB.r, tempRGB.g, tempRGB.b))
        tvTemperature.text = "${rTempK.roundToInt()} K"
        
        resultContainer.visibility = View.VISIBLE
    }

    private data class RGB(val r: Int, val g: Int, val b: Int)
    private fun colorTemperatureToRGB(kelvin: Double): RGB {
        val temp = kelvin / 100.0
        var r: Double; var g: Double; var b: Double
        if (temp <= 66) {
            r = 255.0
            g = temp
            g = 99.4708025861 * ln(g) - 161.1195681661
            if (g < 0) g = 0.0; if (g > 255) g = 255.0
            if (temp <= 19) {
                b = 0.0
            } else {
                b = temp - 10
                b = 138.5177312231 * ln(b) - 305.0447927307
                if (b < 0) b = 0.0; if (b > 255) b = 255.0
            }
        } else {
            r = temp - 60
            r = 329.698727446 * r.pow(-0.1332047592)
            if (r < 0) r = 0.0; if (r > 255) r = 255.0
            g = temp - 60
            g = 288.1221695283 * g.pow(-0.0755148492)
            if (g < 0) g = 0.0; if (g > 255) g = 255.0
            b = 255.0
        }
        return RGB(r.roundToInt(), g.roundToInt(), b.roundToInt())
    }
}
