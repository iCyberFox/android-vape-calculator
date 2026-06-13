package com.bmweast.vapecalc.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bmweast.vapecalc.R

class BoostFragment : Fragment() {

    private lateinit var etVolume: EditText
    private lateinit var flavorsContainer: LinearLayout
    private lateinit var btnAddFlavor: Button
    private lateinit var btnCalculateBoost: Button
    private lateinit var resultContainerBoost: LinearLayout
    private lateinit var tvBoostResult: TextView

    private lateinit var etCurrentVolume: EditText
    private lateinit var etCurrentPercent: EditText
    private lateinit var etTargetPercent: EditText
    private lateinit var btnCalculateDilution: Button
    private lateinit var resultContainerDilution: LinearLayout
    private lateinit var tvDilutionResult: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_boost, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etVolume = view.findViewById(R.id.etVolume)
        flavorsContainer = view.findViewById(R.id.flavorsContainer)
        btnAddFlavor = view.findViewById(R.id.btnAddFlavor)
        btnCalculateBoost = view.findViewById(R.id.btnCalculateBoost)
        resultContainerBoost = view.findViewById(R.id.resultContainerBoost)
        tvBoostResult = view.findViewById(R.id.tvBoostResult)

        etCurrentVolume = view.findViewById(R.id.etCurrentVolume)
        etCurrentPercent = view.findViewById(R.id.etCurrentPercent)
        etTargetPercent = view.findViewById(R.id.etTargetPercent)
        btnCalculateDilution = view.findViewById(R.id.btnCalculateDilution)
        resultContainerDilution = view.findViewById(R.id.resultContainerDilution)
        tvDilutionResult = view.findViewById(R.id.tvDilutionResult)

        addBoostFlavorRow()

        btnAddFlavor.setOnClickListener { addBoostFlavorRow() }
        btnCalculateBoost.setOnClickListener { calculateBoost() }
        btnCalculateDilution.setOnClickListener { calculateDilution() }
    }

    private fun addBoostFlavorRow(name: String = "") {
        val inflater = LayoutInflater.from(requireContext())
        val row = inflater.inflate(R.layout.item_boost_flavor_row, flavorsContainer, false)

        val etName = row.findViewById<EditText>(R.id.etFlavorName)
        val btnRemove = row.findViewById<ImageButton>(R.id.btnRemove)

        val count = flavorsContainer.childCount + 1
        etName.setText(if (name.isEmpty()) "Ароматизатор $count" else name)

        btnRemove.setOnClickListener { flavorsContainer.removeView(row) }
        flavorsContainer.addView(row)
    }

    private fun calculateBoost() {
        val volume = etVolume.text.toString().toDoubleOrNull()
        if (volume == null || volume <= 0) {
            Toast.makeText(requireContext(), "Введіть об'єм рідини", Toast.LENGTH_SHORT).show()
            return
        }

        val resultsHtml = StringBuilder()
        var hasValidData = false

        for (i in 0 until flavorsContainer.childCount) {
            val row = flavorsContainer.getChildAt(i)
            val etName = row.findViewById<EditText>(R.id.etFlavorName)
            val etCurrent = row.findViewById<EditText>(R.id.etCurrent)
            val etTarget = row.findViewById<EditText>(R.id.etTarget)

            val name = etName.text.toString()
            val current = etCurrent.text.toString().toDoubleOrNull() ?: continue
            val target = etTarget.text.toString().toDoubleOrNull() ?: continue

            val currentFlavor = volume * (current / 100)
            val x = (target / 100 * volume - currentFlavor) / (1 - target / 100)

            if (x >= 0 && x.isFinite()) {
                val drops = x * 33
                resultsHtml.append("<b>$name:</b> додати ${"%.3f".format(x)} мл (${drops.toInt()} краплі)<br>")
                hasValidData = true
            } else {
                resultsHtml.append("<b>$name:</b> помилка в розрахунках<br>")
            }
        }

        if (hasValidData) {
            tvBoostResult.text = Html.fromHtml(resultsHtml.toString(), Html.FROM_HTML_MODE_COMPACT)
            resultContainerBoost.visibility = View.VISIBLE
        } else {
            Toast.makeText(requireContext(), "Введіть дані ароматизаторів", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateDilution() {
        val v1 = etCurrentVolume.text.toString().toDoubleOrNull()
        val c1 = etCurrentPercent.text.toString().toDoubleOrNull()
        val c2 = etTargetPercent.text.toString().toDoubleOrNull()

        if (v1 == null || c1 == null || c2 == null || c2 >= c1 || c2 <= 0 || c1 <= 0) {
            Toast.makeText(requireContext(), "Цільовий % має бути менше поточного", Toast.LENGTH_SHORT).show()
            return
        }

        val v2 = v1 * c1 / c2
        val additional = v2 - v1

        val resultsHtml = StringBuilder()
        resultsHtml.append("Додати <b>${"%.2f".format(additional)} мл</b> бази.<br>")
        resultsHtml.append("Кінцевий об'єм: <b>${"%.2f".format(v2)} мл</b>.")

        tvDilutionResult.text = Html.fromHtml(resultsHtml.toString(), Html.FROM_HTML_MODE_COMPACT)
        resultContainerDilution.visibility = View.VISIBLE
    }
}
