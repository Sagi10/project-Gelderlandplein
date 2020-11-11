package com.example.gelderlandplein.ui.search

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.gelderlandplein.R
import com.example.gelderlandplein.adapters.ShopAdapter
import com.example.gelderlandplein.models.Shop
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_event_list.*
import kotlinx.android.synthetic.main.fragment_search_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var database: DatabaseReference
    private var storeListener: ValueEventListener? = null

    private val shops = arrayListOf<Shop>()
    private val shopsAdapter = ShopAdapter(shops)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        database = Firebase.database.reference.child("shops")

        if (shops.isNotEmpty()){
            pb_loading.isVisible = false
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_search_list.adapter = shopsAdapter
        getAllStores()
    }

    private fun getAllStores() {
        this.storeListener = null
        val storeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                shops.clear()
                for (child: DataSnapshot in snapshot.children) {
                    // Code voor het ophalen van de openingstijden
                    var openingstijden = arrayOf("Maandag: " + child.child("openingstijden").child("maandag").value,
                            "Dinsdag: " + child.child("openingstijden").child("dinsdag").value,
                    "Woensdag: " + child.child("openingstijden").child("woensdag").value,
                    "Donderdag: " + child.child("openingstijden").child("donderdag").value,
                    "Vrijdag: " + child.child("openingstijden").child("vrijdag").value,
                    "Zaterdag: " + child.child("openingstijden").child("zaterdag").value,
                    "Zondag: " + child.child("openingstijden").child("zondag").value)
                    val shop = Shop(child.child("name").value.toString(), child.child("tag").value.toString(), child.child("logo").value.toString(), openingstijden, child.child("latitude").value.toString().toFloat(), child.child("longitude").value.toString().toFloat(), null)
                    shops.add(shop)
                }
            shopsAdapter.notifyDataSetChanged()
                pb_loading.isVisible = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Er gaat iets mis met het ophalen van de stores")
            }

        }
        database.addValueEventListener(storeListener)
        this.storeListener = storeListener
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment secondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SearchListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}