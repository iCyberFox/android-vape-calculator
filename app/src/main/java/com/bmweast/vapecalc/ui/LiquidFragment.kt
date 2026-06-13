package com.bmweast.vapecalc.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bmweast.vapecalc.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class LiquidFragment : Fragment() {

    private lateinit var etTotalVolume: EditText
    private lateinit var mainScrollView: ScrollView
    private lateinit var spinnerBaseType: Spinner
    private lateinit var spinnerNicotine: Spinner
    private lateinit var flavorsContainer: LinearLayout
    private lateinit var btnAddFlavor: Button
    private lateinit var btnCalculate: Button
    private lateinit var btnSaveRecipe: View
    private lateinit var btnRecipesList: View
    private lateinit var btnSettings: View
    private lateinit var resultContainer: LinearLayout
    private lateinit var tvResultContentConsolidated: TextView
    private lateinit var warningContainer: LinearLayout
    private lateinit var tvWarning: TextView

    private val baseTypes = arrayOf("50/50", "60/40", "70/30", "65/35")
    private val nicotineValues = arrayOf("0", "3", "4", "5", "6", "9", "12")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_liquid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTotalVolume = view.findViewById(R.id.etTotalVolume)
        mainScrollView = view.findViewById(R.id.mainScrollView)
        spinnerBaseType = view.findViewById(R.id.spinnerBaseType)
        spinnerNicotine = view.findViewById(R.id.spinnerNicotine)
        flavorsContainer = view.findViewById(R.id.flavorsContainer)
        btnAddFlavor = view.findViewById(R.id.btnAddFlavor)
        btnCalculate = view.findViewById(R.id.btnCalculate)
        btnSaveRecipe = view.findViewById(R.id.btnSaveRecipe)
        btnRecipesList = view.findViewById(R.id.btnRecipesList)
        btnSettings = view.findViewById(R.id.btnSettings)
        resultContainer = view.findViewById(R.id.resultContainer)
        tvResultContentConsolidated = view.findViewById(R.id.tvResultContentConsolidated)
        warningContainer = view.findViewById(R.id.warningContainer)
        tvWarning = view.findViewById(R.id.tvWarning)

        etTotalVolume.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                calculate()
                true
            } else {
                false
            }
        }

        setupSpinners()
        loadDefaultValues()
        
        arguments?.getString("recipe_to_load")?.let { jsonStr ->
            loadRecipe(JSONObject(jsonStr))
        } ?: run {
            addFlavorRow("Ароматизатор", 15.0)
        }

        btnAddFlavor.setOnClickListener { addFlavorRow() }
        btnCalculate.setOnClickListener { calculate() }
        btnSaveRecipe.setOnClickListener { showSaveRecipeDialog() }
        btnRecipesList.setOnClickListener { showRecipesListDialog() }
        btnSettings.setOnClickListener { showSettingsDialog() }
    }

    private fun setupSpinners() {
        val baseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, baseTypes)
        baseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBaseType.adapter = baseAdapter

        val nicAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nicotineValues)
        nicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNicotine.adapter = nicAdapter
    }

    private fun loadDefaultValues() {
        val prefs = requireContext().getSharedPreferences("vape_settings", 0)
        val defaultVol = prefs.getString("default_volume", "20")
        val defaultBasePos = prefs.getInt("default_base_pos", 2)
        val defaultNicPos = prefs.getInt("default_nic_pos", 1)

        etTotalVolume.setText(defaultVol)
        spinnerBaseType.setSelection(defaultBasePos)
        spinnerNicotine.setSelection(defaultNicPos)
    }

    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_settings, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).setView(dialogView).create()

        val etVol = dialogView.findViewById<EditText>(R.id.etDefaultVolume)
        val spBase = dialogView.findViewById<Spinner>(R.id.spinnerDefaultBase)
        val spNic = dialogView.findViewById<Spinner>(R.id.spinnerDefaultNicotine)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveSettings)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)

        spBase.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, baseTypes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spNic.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nicotineValues).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val prefs = requireContext().getSharedPreferences("vape_settings", 0)
        etVol.setText(prefs.getString("default_volume", "20"))
        spBase.setSelection(prefs.getInt("default_base_pos", 2))
        spNic.setSelection(prefs.getInt("default_nic_pos", 1))

        btnClose.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener {
            val vol = etVol.text.toString().trim()
            if (vol.isEmpty()) return@setOnClickListener

            prefs.edit().apply {
                putString("default_volume", vol)
                putInt("default_base_pos", spBase.selectedItemPosition)
                putInt("default_nic_pos", spNic.selectedItemPosition)
                apply()
            }

            etTotalVolume.setText(vol)
            spinnerBaseType.setSelection(spBase.selectedItemPosition)
            spinnerNicotine.setSelection(spNic.selectedItemPosition)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addFlavorRow(name: String = "Ароматизатор", percent: Double = 5.0) {
        val row = LayoutInflater.from(requireContext()).inflate(R.layout.item_flavor_row, flavorsContainer, false)
        val etName = row.findViewById<EditText>(R.id.etFlavorName)
        val etPercent = row.findViewById<EditText>(R.id.etFlavorPercent)
        val btnRemove = row.findViewById<ImageButton>(R.id.btnRemoveFlavor)

        etName.setText(name)
        etPercent.setText(percent.toString())
        etPercent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { calculate(); true } else false
        }
        btnRemove.setOnClickListener { flavorsContainer.removeView(row) }
        flavorsContainer.addView(row)
    }

    private fun calculate() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        view?.clearFocus()

        val totalVolume = etTotalVolume.text.toString().toDoubleOrNull()
        if (totalVolume == null || totalVolume <= 0) {
            Toast.makeText(requireContext(), "Введіть коректний об'єм", Toast.LENGTH_SHORT).show()
            return
        }

        val baseTypeStr = spinnerBaseType.selectedItem.toString()
        val parts = baseTypeStr.split("/")
        val baseVG = parts[0].toDouble()
        val basePG = parts[1].toDouble()
        val nicotineStrength = nicotineValues[spinnerNicotine.selectedItemPosition].toDouble()

        val flavors = mutableListOf<Pair<String, Double>>()
        var totalFlavorPercent = 0.0
        for (i in 0 until flavorsContainer.childCount) {
            val row = flavorsContainer.getChildAt(i)
            val name = row.findViewById<EditText>(R.id.etFlavorName).text.toString().trim()
            val percent = row.findViewById<EditText>(R.id.etFlavorPercent).text.toString().toDoubleOrNull() ?: 0.0
            if (name.isNotEmpty() && percent > 0) {
                flavors.add(name to percent)
                totalFlavorPercent += percent
            }
        }

        if (flavors.isEmpty()) {
            Toast.makeText(requireContext(), "Додайте хоча б один ароматизатор", Toast.LENGTH_SHORT).show()
            return
        }
        if (totalFlavorPercent > 30) {
            Toast.makeText(requireContext(), "Загальний % ароматизаторів не повинен перевищувати 30%", Toast.LENGTH_SHORT).show()
            return
        }

        val flavorVolume = totalVolume * totalFlavorPercent / 100
        val baseVolume = totalVolume - flavorVolume
        val finalVG = baseVolume * baseVG / 100
        val finalPG = totalVolume - finalVG
        val finalNicotine = (baseVolume * nicotineStrength) / totalVolume
        val finalVGRatio = (finalVG / totalVolume) * 100
        val finalPGRatio = (finalPG / totalVolume) * 100

        val resultsHtml = StringBuilder()
        resultsHtml.append("<b>База:</b> ${"%.2f".format(baseVolume)} мл ($baseVG VG / $basePG PG)<br>")
        
        resultsHtml.append("<br><b>Ароматизатори (всього ${"%.2f".format(totalFlavorPercent)}%):</b><br>")
        flavors.forEach { (name, p) ->
            resultsHtml.append("$name: ${"%.2f".format(totalVolume * p / 100)} мл ($p%)<br>")
        }
        
        resultsHtml.append("<br><b>Кінцеве співвідношення:</b> ${"%.2f".format(finalVGRatio)} VG / ${"%.2f".format(finalPGRatio)} PG<br>")
        
        resultsHtml.append("<br><b>Кінцевий об'єм:</b> ${"%.2f".format(totalVolume)} мл<br>")
        
        resultsHtml.append("<br><b>Вміст нікотину:</b> ${"%.2f".format(finalNicotine)} мг/мл")

        tvResultContentConsolidated.text = Html.fromHtml(resultsHtml.toString(), Html.FROM_HTML_MODE_COMPACT)

        if (finalVGRatio < 50) {
            val neededVG = (0.5 * totalVolume - finalVG) / 0.5
            val newTotal = totalVolume + neededVG
            val newNicotineStrength = (baseVolume * nicotineStrength) / newTotal
            val newVGPercent = (finalVG + neededVG) / newTotal * 100
            val newPGPercent = finalPG / newTotal * 100
            val newFlavorPercent = flavorVolume / newTotal * 100

            tvWarning.text = "⚠ VG менше 50%. Додайте ${"%.2f".format(neededVG)} мл чистого VG.\n" +
                    "Нові параметри:\n" +
                    "• Об'єм: ${"%.2f".format(newTotal)} мл\n" +
                    "• VG/PG: ${"%.2f".format(newVGPercent)} / ${"%.2f".format(newPGPercent)}\n" +
                    "• Ароматизатори: ${"%.2f".format(newFlavorPercent)}%\n" +
                    "• Нікотин: ${"%.2f".format(newNicotineStrength)} мг/мл"
            warningContainer.visibility = View.VISIBLE
        } else {
            warningContainer.visibility = View.GONE
        }

        resultContainer.visibility = View.VISIBLE
        mainScrollView.post { mainScrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun showSaveRecipeDialog(indexToEdit: Int = -1) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_save_recipe, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).setView(dialogView).create()
        val etName = dialogView.findViewById<EditText>(R.id.etRecipeName)
        val etDesc = dialogView.findViewById<EditText>(R.id.etRecipeDescription)
        
        if (indexToEdit != -1) {
            val recipe = JSONArray(requireContext().getSharedPreferences("vape_recipes", 0).getString("recipes", "[]")).getJSONObject(indexToEdit)
            etName.setText(recipe.getString("name"))
            etDesc.setText(recipe.optString("desc", ""))
            dialogView.findViewById<TextView>(R.id.tvSaveRecipeTitle)?.text = "Редагувати рецепт"
        }

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) return@setOnClickListener
            if (indexToEdit != -1) updateRecipe(indexToEdit, name, etDesc.text.toString().trim())
            else saveRecipe(name, etDesc.text.toString().trim())
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.btnClose).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun updateRecipe(index: Int, name: String, desc: String) {
        val prefs = requireContext().getSharedPreferences("vape_recipes", 0)
        val array = JSONArray(prefs.getString("recipes", "[]"))
        val obj = array.getJSONObject(index)
        obj.put("name", name); obj.put("desc", desc)
        prefs.edit().putString("recipes", array.toString()).apply()
    }

    private fun saveRecipe(name: String, desc: String) {
        val prefs = requireContext().getSharedPreferences("vape_recipes", 0)
        val array = JSONArray(prefs.getString("recipes", "[]"))
        array.put(JSONObject().apply {
            put("name", name); put("desc", desc)
            put("totalVolume", etTotalVolume.text.toString())
            put("baseTypePos", spinnerBaseType.selectedItemPosition)
            put("nicotinePos", spinnerNicotine.selectedItemPosition)
            val fArray = JSONArray()
            for (i in 0 until flavorsContainer.childCount) {
                val row = flavorsContainer.getChildAt(i)
                fArray.put(JSONObject().apply {
                    put("name", row.findViewById<EditText>(R.id.etFlavorName).text.toString())
                    put("percent", row.findViewById<EditText>(R.id.etFlavorPercent).text.toString())
                })
            }
            put("flavors", fArray)
        })
        prefs.edit().putString("recipes", array.toString()).apply()
    }

    private fun showRecipesListDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_recipes_list, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).setView(dialogView).create()
        val container = dialogView.findViewById<LinearLayout>(R.id.recipesContainer)
        val array = JSONArray(requireContext().getSharedPreferences("vape_recipes", 0).getString("recipes", "[]"))

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_recipe, container, false)
            item.findViewById<TextView>(R.id.tvRecipeName).text = obj.getString("name")
            item.findViewById<TextView>(R.id.tvRecipeDescription).text = obj.optString("desc", "")
            item.findViewById<View>(R.id.recipeClickArea).setOnClickListener { loadRecipe(obj); dialog.dismiss() }
            item.findViewById<View>(R.id.btnDeleteRecipe).setOnClickListener {
                val newArr = JSONArray()
                for (j in 0 until array.length()) if (i != j) newArr.put(array.get(j))
                requireContext().getSharedPreferences("vape_recipes", 0).edit().putString("recipes", newArr.toString()).apply()
                dialog.dismiss(); showRecipesListDialog()
            }
            item.findViewById<View>(R.id.btnEditRecipe).setOnClickListener { dialog.dismiss(); showSaveRecipeDialog(i) }
            container.addView(item)
        }
        dialogView.findViewById<View>(R.id.btnClose).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun loadRecipe(recipe: JSONObject) {
        etTotalVolume.setText(recipe.optString("totalVolume", "20"))
        spinnerBaseType.setSelection(recipe.optInt("baseTypePos", 2))
        spinnerNicotine.setSelection(recipe.optInt("nicotinePos", 1))
        flavorsContainer.removeAllViews()
        val fArray = recipe.optJSONArray("flavors") ?: return
        for (i in 0 until fArray.length()) {
            val f = fArray.getJSONObject(i)
            addFlavorRow(f.getString("name"), f.optString("percent", "0").toDoubleOrNull() ?: 0.0)
        }
        calculate()
    }
}
