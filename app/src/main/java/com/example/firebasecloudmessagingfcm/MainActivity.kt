package com.example.firebasecloudmessagingfcm

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    lateinit var etName : EditText //declaration
    lateinit var etpassword: EditText
    var TAG = MainActivity::class.java.simpleName
    //val db = Firebase.firestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etName = findViewById(R.id.etName)
        etpassword = findViewById(R.id.etPassword)
    }



    fun getRegnTokenFCM(view: View) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token: String = task.getResult().toString()

                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, token)
                Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
            })
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: Resuming-restore state")
        restoreData();
    }

    private fun restoreData() {
        //get the file and open it
        var sharedprefs = getSharedPreferences("preferencesync", MODE_PRIVATE)
        //read from the file
        var name = sharedprefs.getString("namekey"," ")
        var password = sharedprefs.getString("passwordkey"," ")
        //restore the data into edittexts
        etName.setText(name)
        etpassword.setText(password)

    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: Pausing- save state")
        saveData();
    }

    private fun saveData() {
        //get the data from the edittexts
        var name = etName.text.toString()
        var password = etpassword.text.toString()
        //create a file
        var sharedprefs = getSharedPreferences("preferencesync", MODE_PRIVATE)
        //open the file in edit mode
        var editor = sharedprefs.edit()
        //write to the file
        editor.putString("namekey",name)
        editor.putString("passwordkey",password)
        //save the file
        editor.apply()
    }


    fun putFirestore(view: View) {
       val db = FirebaseFirestore.getInstance()

        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to etName.text.toString(),
            "last" to etpassword.text.toString(),
        )

// Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }
    fun getFirestore(view: View) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

    }


}