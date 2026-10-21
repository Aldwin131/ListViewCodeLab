package com.example.listview

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    var data = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        data.addAll(listOf("1","2","3","4","5"))

        val lvAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            data
        )
        val lv1 = findViewById<ListView>(R.id.lv1)
        lv1.adapter = lvAdapter

        val btnTambah = findViewById<Button>(R.id.btnTambah)
        btnTambah.setOnClickListener {
            val dtAkhir = Integer.parseInt(data.get(data.size - 1)) + 1
            data.add("$dtAkhir")

            lvAdapter.notifyDataSetChanged()
        }

        lv1.setOnItemClickListener { parent, view, position, id ->
            android.widget.Toast.makeText(
                this,
                data[position],
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        val gestureDetector = android.view.GestureDetector(
            this,
            object : android.view.GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: android.view.MotionEvent): Boolean {
                    val position = lv1.pointToPosition(e.x.toInt(), e.y.toInt())
                    if (position != ListView.INVALID_POSITION) {
                        val selectedItem = data[position]
                        showActionDialog(position, selectedItem, data, lvAdapter)
                    }
                    return true
                }
            }
        )

        lv1.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun showActionDialog(
        position: Int,
        selectedItem: String,
        data: MutableList<String>,
        adapter: ArrayAdapter<String>
    ) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("ITEM $selectedItem")
        builder.setMessage("Pilih tindakan yang ingin dilakukan:")

        builder.setPositiveButton("Update") { _, _ ->
            showUpdateDialog(position, selectedItem, data, adapter)
        }

        builder.setNegativeButton("Hapus") { _, _ ->
            data.removeAt(position)
            adapter.notifyDataSetChanged()
            android.widget.Toast.makeText(
                this,
                "Hapus Item $selectedItem",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNeutralButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showUpdateDialog(
        position: Int,
        oldValue: String,
        data: MutableList<String>,
        adapter: ArrayAdapter<String>
    ) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Update Data")

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val tvOld = android.widget.TextView(this)
        tvOld.text = "Data lama: $oldValue"
        tvOld.textSize = 16f

        val etNew = android.widget.EditText(this)
        etNew.hint = "Masukkan data baru"
        etNew.setText(oldValue)

        layout.addView(tvOld)
        layout.addView(etNew)
        builder.setView(layout)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newValue = etNew.text.toString().trim()
            if (newValue.isNotEmpty()) {
                data[position] = newValue
                adapter.notifyDataSetChanged()
                android.widget.Toast.makeText(
                    this,
                    "Data diupdate jadi: $newValue",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } else {
                android.widget.Toast.makeText(
                    this,
                    "Data baru tidak boleh kosong!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}