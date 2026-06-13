package com.bmweast.vapecalc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bmweast.vapecalc.R

class GeneralShotFragment : Fragment() {

    private lateinit var flavorsContainer: LinearLayout
    private lateinit var btnAddFlavor: Button
    private lateinit var btnCalculate: Button
    private lateinit var resultContainer: LinearLayout
    private lateinit var tvMaxVolume: TextView
    private lateinit var tvUsage: TextView
    private lateinit var tvLeftovers: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general_shot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flavorsContainer = view.findViewById(R.id.flavorsContainer)
        btnAddFlavor = view.findViewById(R.id.btnAddFlavor)
        btnCalculate = view.findViewById(R.id.btnCalculate)
        resultContainer = view.findViewById(R.id.resultContainer)
        tvMaxVolume = view.findViewById(R.id.tvMaxVolume)
        tvUsage = view.findViewById(R.id.tvUsage)
        tvLeftovers = view.findViewById(R.id.tvLeftovers)

        addFlavorRow()

        btnAddFlavor.setOnClickListener { addFlavorRow() }
        btnCalculate.setOnClickListener { calculate() }
    }

    private fun addFlavorRow() {
        val inflater = LayoutInflater.from(requireContext())
        val row = inflater.inflate(R.layout.item_general_flavor_row, flavorsContainer, false)

        val etName = row.findViewById<EditText>(R.id.etFlavorName)
        val etVolume = row.findViewById<EditText>(R.id.etVolume)
        val etPercent = row.findViewById<EditText>(R.id.etPercent)
        val btnRemove = row.findViewById<ImageButton>(R.id.btnRemove)

        val count = flavorsContainer.childCount + 1
        etName.setText("Ароматизатор $count")
        etVolume.setText("10")
        etPercent.setText("1")

        btnRemove.setOnClickListener { flavorsContainer.removeView(row) }
        flavorsContainer.addView(row)
    }

    private fun calculate() {
        data class FlavorItem(val name: String, val volume: Double, val percent: Double)

        val flavors = mutableListOf<FlavorItem>()

        for (i in 0 until flavorsContainer.childCount) {
            val row = flavorsContainer.getChildAt(i)
            val name = row.findViewById<EditText>(R.id.etFlavorName).text.toString().trim()
            val volume = row.findViewById<EditText>(R.id.etVolume).text.toString().toDoubleOrNull() ?: continue
            val percent = row.findViewById<EditText>(R.id.etPercent).text.toString().toDoubleOrNull() ?: continue
            if (volume > 0 && percent > 0) {
                flavors.add(FlavorItem(name.ifEmpty { "Ароматизатор" }, volume, percent))
            }
        }

        if (flavors.isEmpty()) {
            Toast.makeText(requireContext(), "Додайте хоча б один ароматизатор", Toast.LENGTH_SHORT).show()
            return
        }

        // Знаходимо максимально можливий об'єм рідини для кожного ароматизатора
        // і беремо мінімум — це обмежуючий ароматизатор
        val maxVolumes = flavors.map { it.volume / (it.percent / 100.0) }
        val maxTotal = maxVolumes.min()

        tvMaxVolume.text = "Максимальний об'єм рідини: ${"%.2f".format(maxTotal)} мл"

        var usageText = "Використання ароматизаторів:\n"
        var leftoversText = "Залишки:\n"

        flavors.forEachIndexed { index, flavor ->
            val used = maxTotal * (flavor.percent / 100.0)
            val leftover = flavor.volume - used
            usageText += "${flavor.name}: ${"%.2f".format(used)} мл (${flavor.percent}%)\n"
            if (leftover > 0.001) {
                leftoversText += "${flavor.name}: ${"%.2f".format(leftover)} мл залишок\n"
            }
        }

        tvUsage.text = usageText.trimEnd()

        val hasLeftovers = flavors.any { it.volume - maxTotal * (it.percent / 100.0) > 0.001 }
        if (hasLeftovers) {
            tvLeftovers.text = leftoversText.trimEnd()
            tvLeftovers.visibility = View.VISIBLE
        } else {
            tvLeftovers.visibility = View.GONE
        }

        resultContainer.visibility = View.VISIBLE
    }
}
