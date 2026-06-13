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

class ShotFragment : Fragment() {

    private lateinit var etShotVolume: EditText
    private lateinit var tvTotalPercent: TextView
    private lateinit var flavorsContainer: LinearLayout
    private lateinit var btnAddFlavor: Button
    private lateinit var btnCalculate: Button
    private lateinit var resultContainer: LinearLayout
    private lateinit var tvResultContent: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etShotVolume = view.findViewById(R.id.etShotVolume)
        tvTotalPercent = view.findViewById(R.id.tvTotalPercent)
        flavorsContainer = view.findViewById(R.id.flavorsContainer)
        btnAddFlavor = view.findViewById(R.id.btnAddFlavor)
        btnCalculate = view.findViewById(R.id.btnCalculate)
        resultContainer = view.findViewById(R.id.resultContainer)
        tvResultContent = view.findViewById(R.id.tvResultContent)

        addFlavorRow()

        btnAddFlavor.setOnClickListener { addFlavorRow() }
        btnCalculate.setOnClickListener { calculate() }
    }

    private fun addFlavorRow() {
        val inflater = LayoutInflater.from(requireContext())
        val row = inflater.inflate(R.layout.item_flavor_row, flavorsContainer, false)

        val etPercent = row.findViewById<EditText>(R.id.etFlavorPercent)
        val btnRemove = row.findViewById<ImageButton>(R.id.btnRemoveFlavor)

        etPercent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updateTotalPercent() }
        })

        btnRemove.setOnClickListener {
            flavorsContainer.removeView(row)
            updateTotalPercent()
        }
        flavorsContainer.addView(row)
    }

    private fun updateTotalPercent() {
        var total = 0.0
        for (i in 0 until flavorsContainer.childCount) {
            val row = flavorsContainer.getChildAt(i)
            val etPercent = row.findViewById<EditText>(R.id.etFlavorPercent)
            total += etPercent.text.toString().toDoubleOrNull() ?: 0.0
        }
        tvTotalPercent.text = "Сумарний %: $total%"
    }

    private fun calculate() {
        val shotVolume = etShotVolume.text.toString().toDoubleOrNull()
        if (shotVolume == null || shotVolume <= 0) {
            Toast.makeText(requireContext(), "Введіть об'єм шоту", Toast.LENGTH_SHORT).show()
            return
        }

        var totalPercent = 0.0
        for (i in 0 until flavorsContainer.childCount) {
            val row = flavorsContainer.getChildAt(i)
            totalPercent += row.findViewById<EditText>(R.id.etFlavorPercent).text.toString().toDoubleOrNull() ?: 0.0
        }

        if (totalPercent <= 0) {
            Toast.makeText(requireContext(), "Сумарний % має бути більше 0", Toast.LENGTH_SHORT).show()
            return
        }

        val resultsHtml = StringBuilder()
        resultsHtml.append("<b>Об'єм шоту:</b> ${"%.2f".format(shotVolume)} мл<br><br>")
        resultsHtml.append("<b>Склад шоту:</b><br>")

        for (i in 0 until flavorsContainer.childCount) {
            val row = flavorsContainer.getChildAt(i)
            val etName = row.findViewById<EditText>(R.id.etFlavorName)
            val etPercent = row.findViewById<EditText>(R.id.etFlavorPercent)
            val name = etName.text.toString().trim()
            val p = etPercent.text.toString().toDoubleOrNull() ?: continue
            if (name.isEmpty()) continue
            val ml = (p / totalPercent) * shotVolume
            resultsHtml.append("$name: ${"%.2f".format(ml)} мл (${"%.2f".format(p)}%)<br>")
        }

        tvResultContent.text = Html.fromHtml(resultsHtml.toString(), Html.FROM_HTML_MODE_COMPACT)
        resultContainer.visibility = View.VISIBLE
    }
}
